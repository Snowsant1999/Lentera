-- Migration: 20261109000002_add_admin_and_notification_triggers.sql
-- Description: Menambahkan RPC admin update/delete mahasiswa, RPC pengajuan perbaikan lamaran, dan trigger otomatis notifikasi

-- 1. RPC Admin Update Mahasiswa
CREATE OR REPLACE FUNCTION public.admin_update_mahasiswa(
  p_user_id uuid,
  p_nama_lengkap text,
  p_nim text,
  p_email text,
  p_perguruan_tinggi text DEFAULT NULL::text,
  p_program_studi text DEFAULT NULL::text,
  p_angkatan text DEFAULT NULL::text,
  p_jurusan text DEFAULT NULL::text
)
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path TO 'public', 'auth', 'extensions'
AS $$
declare
  v_old_email text;
  v_old_nim text;
  v_email text;
  v_nim text;
  v_nama text;
begin
  if not public.is_admin() then
    raise exception 'Hanya admin kampus yang memiliki izin untuk memperbarui akun mahasiswa';
  end if;

  select email, nim into v_old_email, v_old_nim
  from public.profiles
  where id = p_user_id and role = 'mahasiswa';

  if v_old_email is null then
    raise exception 'Akun mahasiswa tidak ditemukan';
  end if;

  v_email := lower(trim(coalesce(p_email, '')));
  v_nim := trim(coalesce(p_nim, ''));
  v_nama := trim(coalesce(p_nama_lengkap, ''));

  if v_email = '' or v_nim = '' or v_nama = '' then
    raise exception 'Nama lengkap, NIM, dan email kampus wajib diisi';
  end if;

  if v_nim <> v_old_nim and exists (select 1 from public.profiles where nim = v_nim and id <> p_user_id) then
    raise exception 'NIM sudah terdaftar untuk mahasiswa lain';
  end if;

  if v_email <> v_old_email then
    if exists (select 1 from public.profiles where email = v_email and id <> p_user_id)
       or exists (select 1 from auth.users where lower(email) = v_email and id <> p_user_id) then
      raise exception 'Email kampus sudah terdaftar untuk akun lain';
    end if;

    update auth.users
    set email = v_email,
        updated_at = now()
    where id = p_user_id;

    update auth.identities
    set identity_data = jsonb_set(identity_data, '{email}', to_jsonb(v_email)),
        updated_at = now()
    where user_id = p_user_id;
  end if;

  update public.profiles
  set nama_lengkap = v_nama,
      nim = v_nim,
      email = v_email,
      perguruan_tinggi = coalesce(nullif(trim(p_perguruan_tinggi), ''), perguruan_tinggi),
      program_studi = coalesce(nullif(trim(p_program_studi), ''), program_studi),
      angkatan = coalesce(nullif(trim(p_angkatan), ''), angkatan),
      jurusan = coalesce(nullif(trim(p_jurusan), ''), jurusan),
      updated_at = now()
  where id = p_user_id;
end;
$$;

-- 2. RPC Admin Delete/Deactivate Mahasiswa
CREATE OR REPLACE FUNCTION public.admin_delete_mahasiswa(p_user_id uuid)
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path TO 'public', 'auth', 'extensions'
AS $$
declare
  v_role user_role;
begin
  if not public.is_admin() then
    raise exception 'Hanya admin kampus yang memiliki izin untuk menghapus akun mahasiswa';
  end if;

  if p_user_id = auth.uid() then
    raise exception 'Tidak dapat menghapus akun sendiri';
  end if;

  select role into v_role
  from public.profiles
  where id = p_user_id;

  if v_role is null then
    raise exception 'Akun tidak ditemukan';
  end if;

  if v_role <> 'mahasiswa'::user_role then
    raise exception 'Hanya akun mahasiswa yang dapat dihapus melalui fungsi ini';
  end if;

  update public.profiles
  set status_akun = 'nonaktif'::status_akun,
      updated_at = now()
  where id = p_user_id;

  update auth.users
  set banned_until = '2099-12-31 23:59:59+00'::timestamptz,
      updated_at = now()
  where id = p_user_id;
end;
$$;

-- 3. RPC Mahasiswa Ajukan Perbaikan Lamaran
CREATE OR REPLACE FUNCTION public.mahasiswa_ajukan_perbaikan_lamaran(p_lamaran_id uuid)
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path TO 'public'
AS $$
declare
  v_mhs_id uuid;
  v_status lamaran_status;
begin
  select mahasiswa_id, status into v_mhs_id, v_status
  from public.lamaran
  where id = p_lamaran_id;

  if v_mhs_id is null or v_mhs_id <> auth.uid() then
    raise exception 'Lamaran tidak ditemukan atau bukan milik Anda';
  end if;

  if v_status <> 'perlu_perbaikan'::lamaran_status then
    raise exception 'Lamaran tidak dalam status perlu perbaikan';
  end if;

  update public.lamaran
  set status = 'pemeriksaan'::lamaran_status,
      catatan_revisi = null,
      updated_at = now()
  where id = p_lamaran_id;
end;
$$;

-- 4. Trigger Otomatis Notifikasi Saat Status Laporan Berubah
CREATE OR REPLACE FUNCTION public.trg_notifikasi_laporan()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path TO 'public'
AS $$
begin
  if (TG_OP = 'UPDATE' and OLD.status is distinct from NEW.status) then
    insert into public.notifikasi (
      user_id,
      judul,
      isi,
      kategori,
      is_dibaca,
      created_at
    ) values (
      NEW.pelapor_id,
      'Pembaruan Laporan ' || coalesce(NEW.nomor_laporan, ''),
      'Status laporan "' || coalesce(NEW.judul, '') || '" telah diperbarui menjadi ' || upper(NEW.status::text) || '.',
      'laporan'::notif_kategori,
      false,
      now()
    );
  end if;
  return NEW;
end;
$$;

CREATE OR REPLACE TRIGGER trg_laporan_notifikasi
AFTER UPDATE ON public.laporan
FOR EACH ROW
EXECUTE FUNCTION public.trg_notifikasi_laporan();

-- 5. Trigger Otomatis Notifikasi Saat Status Lamaran Berubah
CREATE OR REPLACE FUNCTION public.trg_notifikasi_lamaran()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path TO 'public'
AS $$
declare
  v_posisi text;
begin
  if (TG_OP = 'UPDATE' and OLD.status is distinct from NEW.status) then
    select posisi into v_posisi from public.lowongan where id = NEW.lowongan_id;
    insert into public.notifikasi (
      user_id,
      judul,
      isi,
      kategori,
      is_dibaca,
      created_at
    ) values (
      NEW.mahasiswa_id,
      'Pembaruan Lamaran Magang',
      'Status lamaran Anda untuk posisi ' || coalesce(v_posisi, 'magang') || ' telah diperbarui menjadi ' || upper(replace(NEW.status::text, '_', ' ')) || '.',
      'magang'::notif_kategori,
      false,
      now()
    );
  end if;
  return NEW;
end;
$$;

CREATE OR REPLACE TRIGGER trg_lamaran_notifikasi
AFTER UPDATE ON public.lamaran
FOR EACH ROW
EXECUTE FUNCTION public.trg_notifikasi_lamaran();

-- 6. Trigger Otomatis Notifikasi Saat Permintaan Informasi Ditambahkan
CREATE OR REPLACE FUNCTION public.trg_notifikasi_permintaan_info()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path TO 'public'
AS $$
declare
  v_pelapor_id uuid;
  v_nomor_laporan text;
begin
  select pelapor_id, nomor_laporan into v_pelapor_id, v_nomor_laporan
  from public.laporan
  where id = NEW.laporan_id;

  if v_pelapor_id is not null then
    insert into public.notifikasi (
      user_id,
      judul,
      isi,
      kategori,
      is_dibaca,
      created_at
    ) values (
      v_pelapor_id,
      'Permintaan Informasi Tambahan',
      'Admin kampus meminta informasi tambahan untuk laporan ' || coalesce(v_nomor_laporan, '') || ': ' || coalesce(NEW.catatan_admin, ''),
      'laporan'::notif_kategori,
      false,
      now()
    );
  end if;
  return NEW;
end;
$$;

CREATE OR REPLACE TRIGGER trg_permintaan_info_notifikasi
AFTER INSERT ON public.permintaan_informasi
FOR EACH ROW
EXECUTE FUNCTION public.trg_notifikasi_permintaan_info();

