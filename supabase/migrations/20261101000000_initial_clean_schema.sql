-- Baseline Clean Schema LENTERA (Kosongan untuk Instansi Kampus Baru)
-- Semua tabel transaksional kosong; jalankan seluruh migrasi berurutan hingga 20261116000003_seed_default_admin.sql.

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Custom Enum Types
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'lamaran_status') THEN CREATE TYPE public.lamaran_status AS ENUM ('terkirim', 'pemeriksaan', 'perlu_perbaikan', 'seleksi', 'diterima', 'ditolak'); END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'laporan_status') THEN CREATE TYPE public.laporan_status AS ENUM ('baru', 'diperiksa', 'diproses', 'selesai', 'ditolak'); END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'lowongan_status') THEN CREATE TYPE public.lowongan_status AS ENUM ('draf', 'terbit', 'ditutup'); END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'notif_kategori') THEN CREATE TYPE public.notif_kategori AS ENUM ('laporan', 'magang', 'surat'); END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'sistem_kerja') THEN CREATE TYPE public.sistem_kerja AS ENUM ('On-site', 'Remote', 'Hybrid'); END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'status_akun') THEN CREATE TYPE public.status_akun AS ENUM ('aktif', 'belum_aktivasi', 'lulus', 'nonaktif'); END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tempat_status') THEN CREATE TYPE public.tempat_status AS ENUM ('draf', 'aktif', 'arsip'); END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN CREATE TYPE public.user_role AS ENUM ('admin', 'mahasiswa'); END IF; END $$;

-- 2. Tables
CREATE TABLE IF NOT EXISTS public.profiles (
    id uuid NOT NULL,
    role public.user_role NOT NULL DEFAULT 'mahasiswa'::user_role,
    nama_lengkap text NOT NULL,
    nim text,
    email text NOT NULL,
    no_telepon text,
    perguruan_tinggi text,
    jurusan text,
    program_studi text,
    angkatan varchar,
    status_akun public.status_akun NOT NULL DEFAULT 'belum_aktivasi'::status_akun,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    foto_profil_url text,
    CONSTRAINT profiles_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.kategori_laporan (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    nama text NOT NULL,
    CONSTRAINT kategori_laporan_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.tempat_magang (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    nama_perusahaan text NOT NULL,
    bidang_usaha text NOT NULL,
    nama_cabang text NOT NULL,
    deskripsi text NOT NULL,
    alamat_lengkap text NOT NULL,
    kota text NOT NULL,
    provinsi text NOT NULL,
    sistem_kerja public.sistem_kerja NOT NULL,
    nama_kontak text NOT NULL,
    email_kontak text NOT NULL,
    telepon_kontak text NOT NULL,
    situs_web text,
    logo_path text,
    status public.tempat_status NOT NULL DEFAULT 'draf'::tempat_status,
    catatan_internal text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT tempat_magang_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.dokumen_tempat_magang (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    tempat_magang_id uuid NOT NULL,
    nama_file text NOT NULL,
    ukuran_bytes bigint NOT NULL,
    file_path text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT dokumen_tempat_magang_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.lowongan (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    tempat_magang_id uuid NOT NULL,
    posisi text NOT NULL,
    kategori text,
    lokasi text NOT NULL,
    sistem_kerja public.sistem_kerja NOT NULL,
    deskripsi_pekerjaan text NOT NULL,
    persyaratan text NOT NULL,
    periode_magang text NOT NULL,
    batas_pendaftaran date NOT NULL,
    kuota integer NOT NULL,
    status public.lowongan_status NOT NULL DEFAULT 'draf'::lowongan_status,
    catatan_internal text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT lowongan_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.bookmark_lowongan (
    mahasiswa_id uuid NOT NULL,
    lowongan_id uuid NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT bookmark_lowongan_pkey PRIMARY KEY (mahasiswa_id, lowongan_id)
);

CREATE TABLE IF NOT EXISTS public.dokumen_mahasiswa (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    mahasiswa_id uuid NOT NULL,
    jenis text NOT NULL,
    nama_file text NOT NULL,
    file_path text NOT NULL,
    ukuran_bytes bigint NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT dokumen_mahasiswa_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.lamaran (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    nomor_lamaran text NOT NULL,
    mahasiswa_id uuid NOT NULL,
    lowongan_id uuid NOT NULL,
    nama_lengkap text NOT NULL,
    nim text NOT NULL,
    tempat_tanggal_lahir text NOT NULL,
    alamat_ktp text NOT NULL,
    alamat_domisili text NOT NULL,
    agama text NOT NULL,
    no_telepon text NOT NULL,
    email text NOT NULL,
    golongan_darah text NOT NULL,
    status public.lamaran_status NOT NULL DEFAULT 'terkirim'::lamaran_status,
    catatan_admin text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT lamaran_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.dokumen_lamaran (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    lamaran_id uuid NOT NULL,
    jenis text NOT NULL,
    nama_file text NOT NULL,
    file_path text NOT NULL,
    ukuran_bytes bigint NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT dokumen_lamaran_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.lamaran_riwayat (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    lamaran_id uuid NOT NULL,
    status public.lamaran_status NOT NULL,
    catatan text,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT lamaran_riwayat_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.laporan (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    nomor_laporan text NOT NULL,
    pelapor_id uuid NOT NULL,
    kategori_id uuid NOT NULL,
    judul text NOT NULL,
    kronologi text NOT NULL,
    lokasi_kejadian text NOT NULL,
    tanggal_kejadian timestamptz NOT NULL,
    is_anonim boolean NOT NULL DEFAULT false,
    status public.laporan_status NOT NULL DEFAULT 'baru'::laporan_status,
    jalur_tindak_lanjut text,
    unit_penanganan text,
    catatan_penugasan text,
    hasil_penanganan text,
    tanggal_penyelesaian timestamptz,
    hasil_dipublikasi boolean NOT NULL DEFAULT false,
    ditangani_oleh uuid,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT laporan_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.bukti_laporan (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    laporan_id uuid NOT NULL,
    nama_file text NOT NULL,
    tipe_file varchar NOT NULL,
    ukuran_bytes bigint NOT NULL,
    file_path text NOT NULL,
    is_excluded boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT bukti_laporan_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.permintaan_informasi (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    laporan_id uuid NOT NULL,
    admin_id uuid NOT NULL,
    pesan_ke_pelapor text NOT NULL,
    balasan_pelapor text,
    lampiran_balasan text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT permintaan_informasi_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.dokumen_hasil (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    laporan_id uuid NOT NULL,
    nama_file text NOT NULL,
    tipe_file varchar NOT NULL,
    ukuran_bytes bigint NOT NULL,
    file_path text NOT NULL,
    boleh_dibagikan boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT dokumen_hasil_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.log_tindakan (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    laporan_id uuid NOT NULL,
    admin_id uuid NOT NULL,
    judul_tindakan text NOT NULL,
    deskripsi text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT log_tindakan_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.surat (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    thread_id uuid NOT NULL DEFAULT gen_random_uuid(),
    pengirim_id uuid NOT NULL,
    penerima_id uuid,
    subjek text NOT NULL,
    isi text NOT NULL,
    konteks_tipe text,
    konteks_id uuid,
    is_dibaca boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT surat_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.surat_lampiran (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    surat_id uuid NOT NULL,
    nama_file text NOT NULL,
    file_path text NOT NULL,
    ukuran_bytes bigint NOT NULL,
    CONSTRAINT surat_lampiran_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.notifikasi (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    judul text NOT NULL,
    isi text NOT NULL,
    kategori public.notif_kategori NOT NULL,
    is_dibaca boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    push_terkirim_at timestamptz,
    referensi_id uuid,
    CONSTRAINT notifikasi_pkey PRIMARY KEY (id)
);

-- 3. Unique & Foreign Key Constraints
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'bookmark_lowongan_lowongan_id_fkey') THEN ALTER TABLE public.bookmark_lowongan ADD CONSTRAINT bookmark_lowongan_lowongan_id_fkey FOREIGN KEY (lowongan_id) REFERENCES public.lowongan(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'bookmark_lowongan_mahasiswa_id_fkey') THEN ALTER TABLE public.bookmark_lowongan ADD CONSTRAINT bookmark_lowongan_mahasiswa_id_fkey FOREIGN KEY (mahasiswa_id) REFERENCES public.profiles(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'bukti_laporan_laporan_id_fkey') THEN ALTER TABLE public.bukti_laporan ADD CONSTRAINT bukti_laporan_laporan_id_fkey FOREIGN KEY (laporan_id) REFERENCES public.laporan(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'dokumen_hasil_laporan_id_fkey') THEN ALTER TABLE public.dokumen_hasil ADD CONSTRAINT dokumen_hasil_laporan_id_fkey FOREIGN KEY (laporan_id) REFERENCES public.laporan(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'dokumen_lamaran_lamaran_id_fkey') THEN ALTER TABLE public.dokumen_lamaran ADD CONSTRAINT dokumen_lamaran_lamaran_id_fkey FOREIGN KEY (lamaran_id) REFERENCES public.lamaran(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'dokumen_mahasiswa_mahasiswa_id_fkey') THEN ALTER TABLE public.dokumen_mahasiswa ADD CONSTRAINT dokumen_mahasiswa_mahasiswa_id_fkey FOREIGN KEY (mahasiswa_id) REFERENCES public.profiles(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'dokumen_tempat_magang_tempat_magang_id_fkey') THEN ALTER TABLE public.dokumen_tempat_magang ADD CONSTRAINT dokumen_tempat_magang_tempat_magang_id_fkey FOREIGN KEY (tempat_magang_id) REFERENCES public.tempat_magang(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'kategori_laporan_nama_key') THEN ALTER TABLE public.kategori_laporan ADD CONSTRAINT kategori_laporan_nama_key UNIQUE (nama); END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'lamaran_lowongan_id_fkey') THEN ALTER TABLE public.lamaran ADD CONSTRAINT lamaran_lowongan_id_fkey FOREIGN KEY (lowongan_id) REFERENCES public.lowongan(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'lamaran_mahasiswa_id_fkey') THEN ALTER TABLE public.lamaran ADD CONSTRAINT lamaran_mahasiswa_id_fkey FOREIGN KEY (mahasiswa_id) REFERENCES public.profiles(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'lamaran_nomor_lamaran_key') THEN ALTER TABLE public.lamaran ADD CONSTRAINT lamaran_nomor_lamaran_key UNIQUE (nomor_lamaran); END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_lamaran_mhs_lowongan') THEN ALTER TABLE public.lamaran ADD CONSTRAINT uq_lamaran_mhs_lowongan UNIQUE (mahasiswa_id, lowongan_id); END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'lamaran_riwayat_lamaran_id_fkey') THEN ALTER TABLE public.lamaran_riwayat ADD CONSTRAINT lamaran_riwayat_lamaran_id_fkey FOREIGN KEY (lamaran_id) REFERENCES public.lamaran(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'laporan_ditangani_oleh_fkey') THEN ALTER TABLE public.laporan ADD CONSTRAINT laporan_ditangani_oleh_fkey FOREIGN KEY (ditangani_oleh) REFERENCES public.profiles(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'laporan_kategori_id_fkey') THEN ALTER TABLE public.laporan ADD CONSTRAINT laporan_kategori_id_fkey FOREIGN KEY (kategori_id) REFERENCES public.kategori_laporan(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'laporan_pelapor_id_fkey') THEN ALTER TABLE public.laporan ADD CONSTRAINT laporan_pelapor_id_fkey FOREIGN KEY (pelapor_id) REFERENCES public.profiles(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'laporan_nomor_laporan_key') THEN ALTER TABLE public.laporan ADD CONSTRAINT laporan_nomor_laporan_key UNIQUE (nomor_laporan); END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'log_tindakan_admin_id_fkey') THEN ALTER TABLE public.log_tindakan ADD CONSTRAINT log_tindakan_admin_id_fkey FOREIGN KEY (admin_id) REFERENCES public.profiles(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'log_tindakan_laporan_id_fkey') THEN ALTER TABLE public.log_tindakan ADD CONSTRAINT log_tindakan_laporan_id_fkey FOREIGN KEY (laporan_id) REFERENCES public.laporan(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'lowongan_tempat_magang_id_fkey') THEN ALTER TABLE public.lowongan ADD CONSTRAINT lowongan_tempat_magang_id_fkey FOREIGN KEY (tempat_magang_id) REFERENCES public.tempat_magang(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'notifikasi_user_id_fkey') THEN ALTER TABLE public.notifikasi ADD CONSTRAINT notifikasi_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.profiles(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'permintaan_informasi_admin_id_fkey') THEN ALTER TABLE public.permintaan_informasi ADD CONSTRAINT permintaan_informasi_admin_id_fkey FOREIGN KEY (admin_id) REFERENCES public.profiles(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'permintaan_informasi_laporan_id_fkey') THEN ALTER TABLE public.permintaan_informasi ADD CONSTRAINT permintaan_informasi_laporan_id_fkey FOREIGN KEY (laporan_id) REFERENCES public.laporan(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'profiles_id_fkey') THEN ALTER TABLE public.profiles ADD CONSTRAINT profiles_id_fkey FOREIGN KEY (id) REFERENCES public.() ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'profiles_email_key') THEN ALTER TABLE public.profiles ADD CONSTRAINT profiles_email_key UNIQUE (email); END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'profiles_nim_key') THEN ALTER TABLE public.profiles ADD CONSTRAINT profiles_nim_key UNIQUE (nim); END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'surat_penerima_id_fkey') THEN ALTER TABLE public.surat ADD CONSTRAINT surat_penerima_id_fkey FOREIGN KEY (penerima_id) REFERENCES public.profiles(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'surat_pengirim_id_fkey') THEN ALTER TABLE public.surat ADD CONSTRAINT surat_pengirim_id_fkey FOREIGN KEY (pengirim_id) REFERENCES public.profiles(id) ON DELETE CASCADE; END IF; END $$;
DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'surat_lampiran_surat_id_fkey') THEN ALTER TABLE public.surat_lampiran ADD CONSTRAINT surat_lampiran_surat_id_fkey FOREIGN KEY (surat_id) REFERENCES public.surat(id) ON DELETE CASCADE; END IF; END $$;

-- 4. Views
CREATE OR REPLACE VIEW public.v_laporan_admin AS
 SELECT l.id,
    l.nomor_laporan,
    k.nama AS kategori,
    l.judul,
    l.kronologi,
    l.lokasi_kejadian,
    l.tanggal_kejadian,
    l.is_anonim,
    l.status,
    l.jalur_tindak_lanjut,
    l.unit_penanganan,
    l.catatan_penugasan,
    l.hasil_penanganan,
    l.tanggal_penyelesaian,
    l.hasil_dipublikasi,
    l.ditangani_oleh,
        CASE
            WHEN l.is_anonim THEN NULL::uuid
            ELSE l.pelapor_id
        END AS pelapor_id,
        CASE
            WHEN l.is_anonim THEN NULL::text
            ELSE p.nama_lengkap
        END AS pelapor_nama,
        CASE
            WHEN l.is_anonim THEN NULL::text
            ELSE p.nim
        END AS pelapor_nim,
    l.created_at,
    l.updated_at
   FROM laporan l
     JOIN kategori_laporan k ON k.id = l.kategori_id
     JOIN profiles p ON p.id = l.pelapor_id
  WHERE is_admin();

CREATE OR REPLACE VIEW public.v_laporan_saya AS
 SELECT l.id,
    l.nomor_laporan,
    k.nama AS kategori,
    l.judul,
    l.kronologi,
    l.lokasi_kejadian,
    l.tanggal_kejadian,
    l.is_anonim,
    l.status,
    l.tanggal_penyelesaian,
        CASE
            WHEN l.hasil_dipublikasi THEN l.hasil_penanganan
            ELSE NULL::text
        END AS hasil_penanganan,
    l.created_at,
    l.updated_at
   FROM laporan l
     JOIN kategori_laporan k ON k.id = l.kategori_id
  WHERE l.pelapor_id = auth.uid();

CREATE OR REPLACE VIEW public.v_lowongan_terbit AS
 SELECT l.id,
    l.posisi,
    l.kategori,
    l.lokasi,
    l.sistem_kerja,
    l.deskripsi_pekerjaan,
    l.persyaratan,
    l.periode_magang,
    l.batas_pendaftaran,
    l.kuota,
    t.id AS tempat_magang_id,
    t.nama_perusahaan,
    t.bidang_usaha,
    t.kota,
    t.provinsi,
    t.logo_path,
    t.situs_web
   FROM lowongan l
     JOIN tempat_magang t ON t.id = l.tempat_magang_id
  WHERE l.status = 'terbit'::lowongan_status AND t.status = 'aktif'::tempat_status AND l.batas_pendaftaran >= CURRENT_DATE;

CREATE OR REPLACE VIEW public.v_timeline_laporan_saya AS
 SELECT id,
    laporan_id,
    judul_tindakan,
    created_at
   FROM log_tindakan t
  WHERE laporan_milik_saya(laporan_id);

-- 5. Functions & RPCs
CREATE OR REPLACE FUNCTION public.admin_create_mahasiswa_v2(p_email text, p_password text, p_nama_lengkap text, p_nim text, p_no_telepon text DEFAULT NULL::text, p_jurusan text DEFAULT NULL::text, p_program_studi text DEFAULT NULL::text, p_angkatan text DEFAULT NULL::text, p_perguruan_tinggi text DEFAULT NULL::text)
 RETURNS uuid
 LANGUAGE plpgsql
 SECURITY DEFINER
 SET search_path TO 'public', 'auth', 'extensions'
AS $function$
declare
  new_user_id uuid;
  v_email text;
  v_nim text;
  v_nama text;
  v_pt text;
begin
  if not public.is_admin() then
    raise exception 'Hanya admin kampus yang memiliki izin untuk membuat akun mahasiswa';
  end if;

  v_email := lower(trim(coalesce(p_email, '')));
  v_nim := trim(coalesce(p_nim, ''));
  v_nama := trim(coalesce(p_nama_lengkap, ''));
  v_pt := coalesce(nullif(trim(p_perguruan_tinggi), ''), 'Politeknik Negeri Samarinda');

  if v_email = '' or v_nim = '' or v_nama = '' then
    raise exception 'Nama lengkap, NIM, dan email kampus wajib diisi';
  end if;

  if p_password is null or length(trim(p_password)) < 8 or trim(p_password) = 'Password123!' then
    raise exception 'Password sementara harus minimal 8 karakter dan bukan password default';
  end if;

  if exists (select 1 from public.profiles where nim = v_nim) then
    raise exception 'NIM sudah terdaftar dalam sistem';
  end if;

  if exists (select 1 from public.profiles where email = v_email)
     or exists (select 1 from auth.users where lower(email) = v_email) then
    raise exception 'Email kampus sudah terdaftar dalam sistem';
  end if;

  new_user_id := gen_random_uuid();

  insert into auth.users (
    id,
    instance_id,
    email,
    encrypted_password,
    email_confirmed_at,
    raw_app_meta_data,
    raw_user_meta_data,
    created_at,
    updated_at,
    role,
    aud,
    confirmation_token,
    recovery_token,
    email_change_token_new,
    email_change,
    email_change_token_current,
    phone_change,
    phone_change_token,
    reauthentication_token,
    is_sso_user,
    is_anonymous
  ) values (
    new_user_id,
    '00000000-0000-0000-0000-000000000000',
    v_email,
    extensions.crypt(p_password, extensions.gen_salt('bf')),
    now(),
    '{"provider":"email","providers":["email"]}'::jsonb,
    jsonb_build_object('nama_lengkap', v_nama, 'nim', v_nim),
    now(),
    now(),
    'authenticated',
    'authenticated',
    '',
    '',
    '',
    '',
    '',
    '',
    '',
    '',
    false,
    false
  );

  insert into auth.identities (
    id,
    user_id,
    provider_id,
    identity_data,
    provider,
    last_sign_in_at,
    created_at,
    updated_at
  ) values (
    gen_random_uuid(),
    new_user_id,
    new_user_id::text,
    jsonb_build_object(
      'sub', new_user_id::text,
      'email', v_email,
      'email_verified', true,
      'phone_verified', false
    ),
    'email',
    now(),
    now(),
    now()
  );

  insert into public.profiles (
    id,
    role,
    nama_lengkap,
    nim,
    email,
    no_telepon,
    jurusan,
    program_studi,
    angkatan,
    perguruan_tinggi,
    status_akun
  ) values (
    new_user_id,
    'mahasiswa',
    v_nama,
    v_nim,
    v_email,
    nullif(trim(p_no_telepon), ''),
    nullif(trim(p_jurusan), ''),
    nullif(trim(p_program_studi), ''),
    nullif(trim(p_angkatan), ''),
    v_pt,
    'belum_aktivasi'
  );

  return new_user_id;
end $function$
;

CREATE OR REPLACE FUNCTION public.admin_delete_mahasiswa(p_user_id uuid)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
 SET search_path TO 'public', 'auth', 'extensions'
AS $function$
declare
  v_role user_role;
begin
  -- 1. Validasi hak akses admin
  if not public.is_admin() then
    raise exception 'Hanya admin kampus yang memiliki izin untuk menghapus akun mahasiswa';
  end if;

  -- 2. Cegah admin menghapus dirinya sendiri
  if p_user_id = auth.uid() then
    raise exception 'Tidak dapat menghapus akun sendiri';
  end if;

  -- 3. Cek apakah akun ada dan bertipe mahasiswa
  select role into v_role
  from public.profiles
  where id = p_user_id;

  if v_role is null then
    raise exception 'Akun tidak ditemukan';
  end if;

  if v_role <> 'mahasiswa'::user_role then
    raise exception 'Hanya akun mahasiswa yang dapat dihapus melalui fungsi ini';
  end if;

  -- 4. Bersihkan seluruh data relasi mahasiswa
  delete from public.bookmark_lowongan where mahasiswa_id = p_user_id;
  delete from public.notifikasi where user_id = p_user_id;
  delete from public.dokumen_mahasiswa where mahasiswa_id = p_user_id;
  delete from public.surat where pengirim_id = p_user_id or penerima_id = p_user_id;

  -- Hapus relasi lamaran magang & riwayatnya
  delete from public.dokumen_lamaran where lamaran_id in (select id from public.lamaran where mahasiswa_id = p_user_id);
  delete from public.lamaran_riwayat where lamaran_id in (select id from public.lamaran where mahasiswa_id = p_user_id);
  delete from public.lamaran where mahasiswa_id = p_user_id;

  -- Hapus relasi laporan siaga/pengaduan berserta bukti & lognya
  delete from public.bukti_laporan where laporan_id in (select id from public.laporan where pelapor_id = p_user_id);
  delete from public.dokumen_hasil where laporan_id in (select id from public.laporan where pelapor_id = p_user_id);
  delete from public.log_tindakan where laporan_id in (select id from public.laporan where pelapor_id = p_user_id);
  delete from public.permintaan_informasi where laporan_id in (select id from public.laporan where pelapor_id = p_user_id);
  delete from public.laporan where pelapor_id = p_user_id;

  -- 5. Hapus profil dari tabel public.profiles
  delete from public.profiles where id = p_user_id;

  -- 6. Hapus akun permanen dari auth.users Supabase (Hard Delete)
  delete from auth.users where id = p_user_id;
end;
$function$
;

CREATE OR REPLACE FUNCTION public.admin_update_laporan(p_id uuid, p_status laporan_status DEFAULT NULL::laporan_status, p_jalur text DEFAULT NULL::text, p_unit text DEFAULT NULL::text, p_catatan text DEFAULT NULL::text, p_hasil text DEFAULT NULL::text, p_dipublikasi boolean DEFAULT NULL::boolean, p_ditangani uuid DEFAULT NULL::uuid)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
begin
  if not public.is_admin() then raise exception 'Hanya admin'; end if;
  update public.laporan set
    status               = coalesce(p_status, status),
    jalur_tindak_lanjut  = coalesce(p_jalur, jalur_tindak_lanjut),
    unit_penanganan      = coalesce(p_unit, unit_penanganan),
    catatan_penugasan    = coalesce(p_catatan, catatan_penugasan),
    hasil_penanganan     = coalesce(p_hasil, hasil_penanganan),
    hasil_dipublikasi    = coalesce(p_dipublikasi, hasil_dipublikasi),
    ditangani_oleh       = coalesce(p_ditangani, ditangani_oleh),
    tanggal_penyelesaian = case when coalesce(p_status, status) = 'selesai'
                                then coalesce(tanggal_penyelesaian, now())
                                else tanggal_penyelesaian end
  where id = p_id;
end $function$
;

CREATE OR REPLACE FUNCTION public.admin_update_mahasiswa(p_user_id uuid, p_nama_lengkap text, p_nim text, p_email text, p_perguruan_tinggi text DEFAULT NULL::text, p_program_studi text DEFAULT NULL::text, p_angkatan text DEFAULT NULL::text, p_jurusan text DEFAULT NULL::text, p_no_telepon text DEFAULT NULL::text)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
 SET search_path TO 'public', 'auth', 'extensions'
AS $function$
DECLARE
  v_old_email text;
  v_old_nim text;
  v_email text;
  v_nim text;
  v_nama text;
BEGIN
  IF NOT public.is_admin() THEN
    RAISE EXCEPTION 'Hanya admin kampus yang memiliki izin untuk memperbarui akun mahasiswa';
  END IF;

  SELECT email, nim INTO v_old_email, v_old_nim
  FROM public.profiles
  WHERE id = p_user_id AND role = 'mahasiswa';

  IF v_old_email IS NULL THEN
    RAISE EXCEPTION 'Akun mahasiswa tidak ditemukan';
  END IF;

  v_email := lower(trim(coalesce(p_email, '')));
  v_nim := trim(coalesce(p_nim, ''));
  v_nama := trim(coalesce(p_nama_lengkap, ''));

  IF v_email = '' OR v_nim = '' OR v_nama = '' THEN
    RAISE EXCEPTION 'Nama lengkap, NIM, dan email kampus wajib diisi';
  END IF;

  IF v_nim <> v_old_nim AND EXISTS (SELECT 1 FROM public.profiles WHERE nim = v_nim AND id <> p_user_id) THEN
    RAISE EXCEPTION 'NIM sudah terdaftar untuk mahasiswa lain';
  END IF;

  IF v_email <> v_old_email THEN
    IF EXISTS (SELECT 1 FROM public.profiles WHERE email = v_email AND id <> p_user_id)
       OR EXISTS (SELECT 1 FROM auth.users WHERE lower(email) = v_email AND id <> p_user_id) THEN
      RAISE EXCEPTION 'Email kampus sudah terdaftar untuk akun lain';
    END IF;

    UPDATE auth.users
    SET email = v_email,
        updated_at = now()
    WHERE id = p_user_id;

    UPDATE auth.identities
    SET identity_data = jsonb_set(identity_data, '{email}', to_jsonb(v_email)),
        updated_at = now()
    WHERE user_id = p_user_id;
  END IF;

  UPDATE public.profiles
  SET nama_lengkap = v_nama,
      nim = v_nim,
      email = v_email,
      perguruan_tinggi = coalesce(nullif(trim(p_perguruan_tinggi), ''), perguruan_tinggi),
      program_studi = coalesce(nullif(trim(p_program_studi), ''), program_studi),
      angkatan = coalesce(nullif(trim(p_angkatan), ''), angkatan),
      jurusan = coalesce(nullif(trim(p_jurusan), ''), jurusan),
      no_telepon = coalesce(nullif(trim(p_no_telepon), ''), no_telepon),
      updated_at = now()
  WHERE id = p_user_id;
END;
$function$
;

CREATE OR REPLACE FUNCTION public.aktivasi_akun_saya()
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
declare
  v_status public.status_akun;
begin
  if auth.uid() is null then
    raise exception 'Belum login';
  end if;

  select status_akun into v_status
  from public.profiles
  where id = auth.uid();

  if not found then
    raise exception 'Profil pengguna tidak ditemukan';
  end if;

  if v_status::text = 'nonaktif' then
    raise exception 'Akun dinonaktifkan oleh admin kampus';
  end if;

  if v_status = 'belum_aktivasi' then
    perform set_config('lentera.self_activation_uid', auth.uid()::text, true);
    update public.profiles
    set status_akun = 'aktif'
    where id = auth.uid()
      and status_akun = 'belum_aktivasi';
  end if;
end $function$
;

CREATE OR REPLACE FUNCTION public.buat_laporan(p_kategori_id uuid, p_judul text, p_kronologi text, p_lokasi text, p_tanggal timestamp with time zone, p_anonim boolean)
 RETURNS TABLE(id uuid, nomor_laporan text)
 LANGUAGE plpgsql
 SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
begin
  if auth.uid() is null then raise exception 'Belum login'; end if;
  return query
    insert into public.laporan as l (pelapor_id, kategori_id, judul, kronologi, lokasi_kejadian, tanggal_kejadian, is_anonim)
    values (auth.uid(), p_kategori_id, p_judul, p_kronologi, p_lokasi, p_tanggal, coalesce(p_anonim, false))
    returning l.id, l.nomor_laporan;
end $function$
;

CREATE OR REPLACE FUNCTION public.get_admin_dashboard_stats()
 RETURNS jsonb
 LANGUAGE plpgsql
 SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
begin
  if not public.is_admin() then
    raise exception 'Hanya admin';
  end if;

  return jsonb_build_object(
    'total_laporan', (select count(*)::int from public.laporan),
    'laporan_perlu_tindakan', (select count(*)::int from public.laporan where status in ('baru', 'diperiksa', 'diproses')),
    'total_tempat_magang', (select count(*)::int from public.tempat_magang),
    'total_lowongan_aktif', (select count(*)::int from public.lowongan where status = 'terbit'),
    'total_lamaran_masuk', (select count(*)::int from public.lamaran),
    'total_mahasiswa_aktif', (select count(*)::int from public.profiles where role = 'mahasiswa' and status_akun = 'aktif')
  );
end $function$
;

CREATE OR REPLACE FUNCTION public.get_email_by_nim(p_nim text)
 RETURNS text
 LANGUAGE sql
 STABLE SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
  select email from public.profiles where nim = trim(p_nim) limit 1;
$function$
;

CREATE OR REPLACE FUNCTION public.guard_profile_update()
 RETURNS trigger
 LANGUAGE plpgsql
 SET search_path TO 'public'
AS $function$
begin
  if auth.uid() is not null and not public.is_admin() then
    if new.role is distinct from old.role
       or new.nim is distinct from old.nim
       or new.email is distinct from old.email then
      raise exception 'Kolom role, nim, email, dan status_akun hanya dapat diubah admin';
    end if;

    if new.status_akun is distinct from old.status_akun then
      -- Transisi belum_aktivasi -> aktif hanya diizinkan melalui RPC aktivasi_akun_saya()
      if not coalesce(
        current_setting('lentera.self_activation_uid', true) = auth.uid()::text
        and old.status_akun = 'belum_aktivasi'
        and new.status_akun = 'aktif'
        and new.id = auth.uid(),
        false
      ) then
        raise exception 'Kolom role, nim, email, dan status_akun hanya dapat diubah admin';
      end if;
    end if;
  end if;
  return new;
end $function$
;

CREATE OR REPLACE FUNCTION public.is_admin()
 RETURNS boolean
 LANGUAGE sql
 STABLE SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
  select exists (select 1 from public.profiles where id = auth.uid() and role = 'admin');
$function$
;

CREATE OR REPLACE FUNCTION public.lamaran_milik_saya(p_lamaran uuid)
 RETURNS boolean
 LANGUAGE sql
 STABLE SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
  select exists (select 1 from public.lamaran where id = p_lamaran and mahasiswa_id = auth.uid());
$function$
;

CREATE OR REPLACE FUNCTION public.laporan_milik_saya(p_laporan uuid)
 RETURNS boolean
 LANGUAGE sql
 STABLE SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
  select exists (select 1 from public.laporan where id = p_laporan and pelapor_id = auth.uid());
$function$
;

CREATE OR REPLACE FUNCTION public.lowongan_terbuka(p_lowongan uuid)
 RETURNS boolean
 LANGUAGE sql
 STABLE SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
  select exists (
    select 1 from public.lowongan l join public.tempat_magang t on t.id = l.tempat_magang_id
    where l.id = p_lowongan and l.status = 'terbit' and t.status = 'aktif'
      and l.batas_pendaftaran >= current_date);
$function$
;

CREATE OR REPLACE FUNCTION public.mahasiswa_ajukan_perbaikan_lamaran(p_lamaran_id uuid)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
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
$function$
;

CREATE OR REPLACE FUNCTION public.set_updated_at()
 RETURNS trigger
 LANGUAGE plpgsql
 SET search_path TO 'public'
AS $function$
begin
  new.updated_at := now();
  return new;
end $function$
;

CREATE OR REPLACE FUNCTION public.tandai_notifikasi_terkirim(p_notif_ids uuid[])
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
BEGIN
  IF auth.uid() IS NULL OR p_notif_ids IS NULL OR array_length(p_notif_ids, 1) IS NULL THEN
    RETURN;
  END IF;

  UPDATE public.notifikasi
  SET push_terkirim_at = now()
  WHERE id = ANY(p_notif_ids)
    AND user_id = auth.uid()
    AND push_terkirim_at IS NULL;
END;
$function$
;

CREATE OR REPLACE FUNCTION public.thread_surat_boleh(p_thread uuid)
 RETURNS boolean
 LANGUAGE sql
 STABLE SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
  select public.is_admin() or exists (
    select 1 from public.surat
    where thread_id = p_thread and (pengirim_id = auth.uid() or penerima_id = auth.uid()));
$function$
;

CREATE OR REPLACE FUNCTION public.trg_lamaran_riwayat()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
begin
  if tg_op = 'INSERT' or new.status is distinct from old.status then
    insert into public.lamaran_riwayat (lamaran_id, status, catatan)
    values (new.id, new.status, new.catatan_admin);
  end if;
  return new;
end $function$
;

CREATE OR REPLACE FUNCTION public.trg_nomor_lamaran()
 RETURNS trigger
 LANGUAGE plpgsql
 SET search_path TO 'public'
AS $function$
begin
  if new.nomor_lamaran is null or new.nomor_lamaran = '' then
    new.nomor_lamaran := 'LMR-' || to_char(now(), 'YYYY') || '-' ||
                         lpad(nextval('public.seq_nomor_lamaran')::text, 5, '0');
  end if;
  return new;
end $function$
;

CREATE OR REPLACE FUNCTION public.trg_nomor_laporan()
 RETURNS trigger
 LANGUAGE plpgsql
 SET search_path TO 'public'
AS $function$
begin
  if new.nomor_laporan is null or new.nomor_laporan = '' then
    new.nomor_laporan := 'LTR-' || to_char(now(), 'YYYY') || '-' ||
                         lpad(nextval('public.seq_nomor_laporan')::text, 5, '0');
  end if;
  return new;
end $function$
;

CREATE OR REPLACE FUNCTION public.trg_notifikasi_lamaran()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
DECLARE
  v_posisi text;
BEGIN
  SELECT posisi INTO v_posisi FROM public.lowongan WHERE id = NEW.lowongan_id;

  IF (TG_OP = 'INSERT') THEN
    INSERT INTO public.notifikasi (
      user_id, judul, isi, kategori, is_dibaca, referensi_id, created_at
    )
    SELECT
      p.id,
      'Lamaran Magang Masuk',
      'Lamaran baru untuk posisi ' || coalesce(v_posisi, 'magang') || ' menunggu verifikasi admin.',
      'magang'::notif_kategori,
      false,
      NEW.id,
      now()
    FROM public.profiles p
    WHERE p.role = 'admin' AND p.status_akun = 'aktif';
  ELSIF (TG_OP = 'UPDATE' AND OLD.status IS DISTINCT FROM NEW.status) THEN
    IF (OLD.status = 'perlu_perbaikan'::lamaran_status AND NEW.status = 'pemeriksaan'::lamaran_status) THEN
      INSERT INTO public.notifikasi (
        user_id, judul, isi, kategori, is_dibaca, referensi_id, created_at
      )
      SELECT
        p.id,
        'Perbaikan Berkas Lamaran',
        'Mahasiswa telah mengajukan perbaikan berkas untuk posisi ' || coalesce(v_posisi, 'magang') || '.',
        'magang'::notif_kategori,
        false,
        NEW.id,
        now()
      FROM public.profiles p
      WHERE p.role = 'admin' AND p.status_akun = 'aktif';
    ELSE
      INSERT INTO public.notifikasi (
        user_id, judul, isi, kategori, is_dibaca, referensi_id, created_at
      ) VALUES (
        NEW.mahasiswa_id,
        'Pembaruan Lamaran Magang',
        'Status lamaran Anda untuk posisi ' || coalesce(v_posisi, 'magang') || ' telah diperbarui menjadi ' || upper(replace(NEW.status::text, '_', ' ')) || '.',
        'magang'::notif_kategori,
        false,
        NEW.id,
        now()
      );
    END IF;
  END IF;
  RETURN NEW;
END;
$function$
;

CREATE OR REPLACE FUNCTION public.trg_notifikasi_laporan()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
BEGIN
  IF (TG_OP = 'INSERT') THEN
    INSERT INTO public.notifikasi (
      user_id, judul, isi, kategori, is_dibaca, referensi_id, created_at
    )
    SELECT
      p.id,
      'Laporan Baru ' || coalesce(NEW.nomor_laporan, ''),
      'Laporan baru "' || coalesce(NEW.judul, '') || '" menunggu verifikasi tim LENTERA Siaga.',
      'laporan'::notif_kategori,
      false,
      NEW.id,
      now()
    FROM public.profiles p
    WHERE p.role = 'admin' AND p.status_akun = 'aktif';
  ELSIF (TG_OP = 'UPDATE' AND OLD.status IS DISTINCT FROM NEW.status) THEN
    INSERT INTO public.notifikasi (
      user_id, judul, isi, kategori, is_dibaca, referensi_id, created_at
    ) VALUES (
      NEW.pelapor_id,
      'Pembaruan Laporan ' || coalesce(NEW.nomor_laporan, ''),
      'Status laporan "' || coalesce(NEW.judul, '') || '" telah diperbarui menjadi ' || upper(NEW.status::text) || '.',
      'laporan'::notif_kategori,
      false,
      NEW.id,
      now()
    );
  END IF;
  RETURN NEW;
END;
$function$
;

CREATE OR REPLACE FUNCTION public.trg_notifikasi_permintaan_info()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
DECLARE
  v_pelapor_id uuid;
  v_nomor_laporan text;
BEGIN
  SELECT pelapor_id, nomor_laporan INTO v_pelapor_id, v_nomor_laporan
  FROM public.laporan
  WHERE id = NEW.laporan_id;

  IF (TG_OP = 'INSERT') THEN
    IF v_pelapor_id IS NOT NULL THEN
      INSERT INTO public.notifikasi (
        user_id, judul, isi, kategori, is_dibaca, referensi_id, created_at
      ) VALUES (
        v_pelapor_id,
        'Permintaan Informasi Tambahan',
        'Admin kampus meminta informasi tambahan untuk laporan ' || coalesce(v_nomor_laporan, '') || ': ' || coalesce(NEW.catatan_admin, ''),
        'laporan'::notif_kategori,
        false,
        NEW.laporan_id,
        now()
      );
    END IF;
  ELSIF (TG_OP = 'UPDATE' AND coalesce(OLD.jawaban_mahasiswa, '') = '' AND coalesce(NEW.jawaban_mahasiswa, '') <> '') THEN
    INSERT INTO public.notifikasi (
      user_id, judul, isi, kategori, is_dibaca, referensi_id, created_at
    )
    SELECT
      p.id,
      'Balasan Informasi Laporan ' || coalesce(v_nomor_laporan, ''),
      'Pelapor telah mengirimkan jawaban atas permintaan informasi tambahan.',
      'laporan'::notif_kategori,
      false,
      NEW.laporan_id,
      now()
    FROM public.profiles p
    WHERE p.role = 'admin' AND p.status_akun = 'aktif';
  END IF;
  RETURN NEW;
END;
$function$
;

-- 6. Triggers
DROP TRIGGER IF EXISTS trg_lamaran_nomor ON public.lamaran;
CREATE TRIGGER trg_lamaran_nomor BEFORE INSERT ON public.lamaran FOR EACH ROW EXECUTE FUNCTION trg_nomor_lamaran();
DROP TRIGGER IF EXISTS trg_lamaran_notifikasi ON public.lamaran;
CREATE TRIGGER trg_lamaran_notifikasi AFTER INSERT OR UPDATE ON public.lamaran FOR EACH ROW EXECUTE FUNCTION trg_notifikasi_lamaran();
DROP TRIGGER IF EXISTS trg_lamaran_riwayat ON public.lamaran;
CREATE TRIGGER trg_lamaran_riwayat AFTER INSERT OR UPDATE OF status ON public.lamaran FOR EACH ROW EXECUTE FUNCTION trg_lamaran_riwayat();
DROP TRIGGER IF EXISTS trg_lamaran_upd ON public.lamaran;
CREATE TRIGGER trg_lamaran_upd BEFORE UPDATE ON public.lamaran FOR EACH ROW EXECUTE FUNCTION set_updated_at();
DROP TRIGGER IF EXISTS trg_laporan_nomor ON public.laporan;
CREATE TRIGGER trg_laporan_nomor BEFORE INSERT ON public.laporan FOR EACH ROW EXECUTE FUNCTION trg_nomor_laporan();
DROP TRIGGER IF EXISTS trg_laporan_notifikasi ON public.laporan;
CREATE TRIGGER trg_laporan_notifikasi AFTER INSERT OR UPDATE ON public.laporan FOR EACH ROW EXECUTE FUNCTION trg_notifikasi_laporan();
DROP TRIGGER IF EXISTS trg_laporan_upd ON public.laporan;
CREATE TRIGGER trg_laporan_upd BEFORE UPDATE ON public.laporan FOR EACH ROW EXECUTE FUNCTION set_updated_at();
DROP TRIGGER IF EXISTS trg_lowongan_upd ON public.lowongan;
CREATE TRIGGER trg_lowongan_upd BEFORE UPDATE ON public.lowongan FOR EACH ROW EXECUTE FUNCTION set_updated_at();
DROP TRIGGER IF EXISTS trg_permintaan_info_notifikasi ON public.permintaan_informasi;
CREATE TRIGGER trg_permintaan_info_notifikasi AFTER INSERT OR UPDATE ON public.permintaan_informasi FOR EACH ROW EXECUTE FUNCTION trg_notifikasi_permintaan_info();
DROP TRIGGER IF EXISTS trg_permintaan_upd ON public.permintaan_informasi;
CREATE TRIGGER trg_permintaan_upd BEFORE UPDATE ON public.permintaan_informasi FOR EACH ROW EXECUTE FUNCTION set_updated_at();
DROP TRIGGER IF EXISTS trg_profiles_guard ON public.profiles;
CREATE TRIGGER trg_profiles_guard BEFORE UPDATE ON public.profiles FOR EACH ROW EXECUTE FUNCTION guard_profile_update();
DROP TRIGGER IF EXISTS trg_profiles_upd ON public.profiles;
CREATE TRIGGER trg_profiles_upd BEFORE UPDATE ON public.profiles FOR EACH ROW EXECUTE FUNCTION set_updated_at();
DROP TRIGGER IF EXISTS trg_tempat_upd ON public.tempat_magang;
CREATE TRIGGER trg_tempat_upd BEFORE UPDATE ON public.tempat_magang FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- 7. Enable RLS & Public Policies
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.kategori_laporan ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.tempat_magang ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.dokumen_tempat_magang ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.lowongan ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.bookmark_lowongan ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.dokumen_mahasiswa ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.lamaran ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.dokumen_lamaran ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.lamaran_riwayat ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.laporan ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.bukti_laporan ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.permintaan_informasi ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.dokumen_hasil ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.log_tindakan ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.surat ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.surat_lampiran ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notifikasi ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "bm_own" ON public.bookmark_lowongan;
CREATE POLICY "bm_own" ON public.bookmark_lowongan FOR ALL USING ((mahasiswa_id = auth.uid())) WITH CHECK ((mahasiswa_id = auth.uid()));
DROP POLICY IF EXISTS "bukti_delete" ON public.bukti_laporan;
CREATE POLICY "bukti_delete" ON public.bukti_laporan FOR DELETE USING (laporan_milik_saya(laporan_id));
DROP POLICY IF EXISTS "bukti_insert" ON public.bukti_laporan;
CREATE POLICY "bukti_insert" ON public.bukti_laporan FOR INSERT WITH CHECK (laporan_milik_saya(laporan_id));
DROP POLICY IF EXISTS "bukti_select" ON public.bukti_laporan;
CREATE POLICY "bukti_select" ON public.bukti_laporan FOR SELECT USING ((is_admin() OR laporan_milik_saya(laporan_id)));
DROP POLICY IF EXISTS "bukti_update" ON public.bukti_laporan;
CREATE POLICY "bukti_update" ON public.bukti_laporan FOR UPDATE USING (laporan_milik_saya(laporan_id)) WITH CHECK (laporan_milik_saya(laporan_id));
DROP POLICY IF EXISTS "dh_admin" ON public.dokumen_hasil;
CREATE POLICY "dh_admin" ON public.dokumen_hasil FOR ALL USING (is_admin()) WITH CHECK (is_admin());
DROP POLICY IF EXISTS "dh_select" ON public.dokumen_hasil;
CREATE POLICY "dh_select" ON public.dokumen_hasil FOR SELECT USING ((boleh_dibagikan AND laporan_milik_saya(laporan_id)));
DROP POLICY IF EXISTS "dl_delete" ON public.dokumen_lamaran;
CREATE POLICY "dl_delete" ON public.dokumen_lamaran FOR DELETE USING (lamaran_milik_saya(lamaran_id));
DROP POLICY IF EXISTS "dl_insert" ON public.dokumen_lamaran;
CREATE POLICY "dl_insert" ON public.dokumen_lamaran FOR INSERT WITH CHECK (lamaran_milik_saya(lamaran_id));
DROP POLICY IF EXISTS "dl_select" ON public.dokumen_lamaran;
CREATE POLICY "dl_select" ON public.dokumen_lamaran FOR SELECT USING ((is_admin() OR lamaran_milik_saya(lamaran_id)));
DROP POLICY IF EXISTS "dm_select" ON public.dokumen_mahasiswa;
CREATE POLICY "dm_select" ON public.dokumen_mahasiswa FOR SELECT USING (((mahasiswa_id = auth.uid()) OR is_admin()));
DROP POLICY IF EXISTS "dm_write" ON public.dokumen_mahasiswa;
CREATE POLICY "dm_write" ON public.dokumen_mahasiswa FOR ALL USING ((mahasiswa_id = auth.uid())) WITH CHECK ((mahasiswa_id = auth.uid()));
DROP POLICY IF EXISTS "dtm_admin" ON public.dokumen_tempat_magang;
CREATE POLICY "dtm_admin" ON public.dokumen_tempat_magang FOR ALL USING (is_admin()) WITH CHECK (is_admin());
DROP POLICY IF EXISTS "kategori_admin" ON public.kategori_laporan;
CREATE POLICY "kategori_admin" ON public.kategori_laporan FOR ALL USING (is_admin()) WITH CHECK (is_admin());
DROP POLICY IF EXISTS "kategori_select" ON public.kategori_laporan;
CREATE POLICY "kategori_select" ON public.kategori_laporan FOR SELECT USING (true);
DROP POLICY IF EXISTS "lamaran_admin_upd" ON public.lamaran;
CREATE POLICY "lamaran_admin_upd" ON public.lamaran FOR UPDATE USING (is_admin()) WITH CHECK (is_admin());
DROP POLICY IF EXISTS "lamaran_insert" ON public.lamaran;
CREATE POLICY "lamaran_insert" ON public.lamaran FOR INSERT WITH CHECK (((mahasiswa_id = auth.uid()) AND (status = 'terkirim'::lamaran_status) AND lowongan_terbuka(lowongan_id)));
DROP POLICY IF EXISTS "lamaran_select" ON public.lamaran;
CREATE POLICY "lamaran_select" ON public.lamaran FOR SELECT USING (((mahasiswa_id = auth.uid()) OR is_admin()));
DROP POLICY IF EXISTS "riwayat_select" ON public.lamaran_riwayat;
CREATE POLICY "riwayat_select" ON public.lamaran_riwayat FOR SELECT USING ((is_admin() OR lamaran_milik_saya(lamaran_id)));
DROP POLICY IF EXISTS "laporan_admin_all" ON public.laporan;
CREATE POLICY "laporan_admin_all" ON public.laporan FOR ALL USING (is_admin()) WITH CHECK (is_admin());
DROP POLICY IF EXISTS "laporan_select_own" ON public.laporan;
CREATE POLICY "laporan_select_own" ON public.laporan FOR SELECT USING ((pelapor_id = ( SELECT auth.uid() AS uid)));
DROP POLICY IF EXISTS "log_admin" ON public.log_tindakan;
CREATE POLICY "log_admin" ON public.log_tindakan FOR ALL USING (is_admin()) WITH CHECK ((is_admin() AND (admin_id = auth.uid())));
DROP POLICY IF EXISTS "lowongan_admin" ON public.lowongan;
CREATE POLICY "lowongan_admin" ON public.lowongan FOR ALL USING (is_admin()) WITH CHECK (is_admin());
DROP POLICY IF EXISTS "lowongan_admin_manage" ON public.lowongan;
CREATE POLICY "lowongan_admin_manage" ON public.lowongan FOR ALL USING (is_admin()) WITH CHECK (is_admin());
DROP POLICY IF EXISTS "lowongan_read_public" ON public.lowongan;
CREATE POLICY "lowongan_read_public" ON public.lowongan FOR SELECT USING (((status = 'terbit'::lowongan_status) OR is_admin()));
DROP POLICY IF EXISTS "notif_select" ON public.notifikasi;
CREATE POLICY "notif_select" ON public.notifikasi FOR SELECT USING ((user_id = auth.uid()));
DROP POLICY IF EXISTS "notif_update" ON public.notifikasi;
CREATE POLICY "notif_update" ON public.notifikasi FOR UPDATE USING ((user_id = auth.uid())) WITH CHECK ((user_id = auth.uid()));
DROP POLICY IF EXISTS "pi_admin" ON public.permintaan_informasi;
CREATE POLICY "pi_admin" ON public.permintaan_informasi FOR ALL USING (is_admin()) WITH CHECK ((is_admin() AND (admin_id = auth.uid())));
DROP POLICY IF EXISTS "pi_balas" ON public.permintaan_informasi;
CREATE POLICY "pi_balas" ON public.permintaan_informasi FOR UPDATE USING (laporan_milik_saya(laporan_id)) WITH CHECK (laporan_milik_saya(laporan_id));
DROP POLICY IF EXISTS "pi_select" ON public.permintaan_informasi;
CREATE POLICY "pi_select" ON public.permintaan_informasi FOR SELECT USING (laporan_milik_saya(laporan_id));
DROP POLICY IF EXISTS "profiles_select" ON public.profiles;
CREATE POLICY "profiles_select" ON public.profiles FOR SELECT USING (((id = auth.uid()) OR is_admin()));
DROP POLICY IF EXISTS "profiles_update" ON public.profiles;
CREATE POLICY "profiles_update" ON public.profiles FOR UPDATE USING (((id = auth.uid()) OR is_admin())) WITH CHECK (((id = auth.uid()) OR is_admin()));
DROP POLICY IF EXISTS "surat_insert_admin" ON public.surat;
CREATE POLICY "surat_insert_admin" ON public.surat FOR INSERT WITH CHECK (((pengirim_id = auth.uid()) AND (penerima_id IS NOT NULL) AND is_admin()));
DROP POLICY IF EXISTS "surat_insert_mhs" ON public.surat;
CREATE POLICY "surat_insert_mhs" ON public.surat FOR INSERT WITH CHECK (((pengirim_id = auth.uid()) AND (penerima_id IS NULL) AND (NOT is_admin())));
DROP POLICY IF EXISTS "surat_select" ON public.surat;
CREATE POLICY "surat_select" ON public.surat FOR SELECT USING ((is_admin() OR (pengirim_id = auth.uid()) OR (penerima_id = auth.uid())));
DROP POLICY IF EXISTS "surat_update" ON public.surat;
CREATE POLICY "surat_update" ON public.surat FOR UPDATE USING (((penerima_id = auth.uid()) OR ((penerima_id IS NULL) AND is_admin()))) WITH CHECK (((penerima_id = auth.uid()) OR ((penerima_id IS NULL) AND is_admin())));
DROP POLICY IF EXISTS "sl_insert" ON public.surat_lampiran;
CREATE POLICY "sl_insert" ON public.surat_lampiran FOR INSERT WITH CHECK ((EXISTS ( SELECT 1
   FROM surat s
  WHERE ((s.id = surat_lampiran.surat_id) AND (s.pengirim_id = auth.uid())))));
DROP POLICY IF EXISTS "sl_select" ON public.surat_lampiran;
CREATE POLICY "sl_select" ON public.surat_lampiran FOR SELECT USING ((EXISTS ( SELECT 1
   FROM surat s
  WHERE ((s.id = surat_lampiran.surat_id) AND (is_admin() OR (s.pengirim_id = auth.uid()) OR (s.penerima_id = auth.uid()))))));
DROP POLICY IF EXISTS "tempat_admin" ON public.tempat_magang;
CREATE POLICY "tempat_admin" ON public.tempat_magang FOR ALL USING (is_admin()) WITH CHECK (is_admin());
DROP POLICY IF EXISTS "tempat_magang_admin_manage" ON public.tempat_magang;
CREATE POLICY "tempat_magang_admin_manage" ON public.tempat_magang FOR ALL USING (is_admin()) WITH CHECK (is_admin());
DROP POLICY IF EXISTS "tempat_magang_read_public" ON public.tempat_magang;
CREATE POLICY "tempat_magang_read_public" ON public.tempat_magang FOR SELECT USING (((status = 'aktif'::tempat_status) OR is_admin()));

-- 8. Storage Buckets & Policies
INSERT INTO storage.buckets (id, name, public) VALUES ('bukti-laporan', 'bukti-laporan', false) ON CONFLICT (id) DO NOTHING;
INSERT INTO storage.buckets (id, name, public) VALUES ('dokumen-hasil', 'dokumen-hasil', false) ON CONFLICT (id) DO NOTHING;
INSERT INTO storage.buckets (id, name, public) VALUES ('dokumen-lamaran', 'dokumen-lamaran', false) ON CONFLICT (id) DO NOTHING;
INSERT INTO storage.buckets (id, name, public) VALUES ('dokumen-mahasiswa', 'dokumen-mahasiswa', false) ON CONFLICT (id) DO NOTHING;
INSERT INTO storage.buckets (id, name, public) VALUES ('dokumen-mitra', 'dokumen-mitra', false) ON CONFLICT (id) DO NOTHING;
INSERT INTO storage.buckets (id, name, public) VALUES ('foto-profil', 'foto-profil', true) ON CONFLICT (id) DO NOTHING;
INSERT INTO storage.buckets (id, name, public) VALUES ('lampiran-surat', 'lampiran-surat', false) ON CONFLICT (id) DO NOTHING;
INSERT INTO storage.buckets (id, name, public) VALUES ('logo-perusahaan', 'logo-perusahaan', false) ON CONFLICT (id) DO NOTHING;

DROP POLICY IF EXISTS "Public read foto profil" ON storage.objects;
CREATE POLICY "Public read foto profil" ON storage.objects FOR SELECT USING ((bucket_id = 'foto-profil'::text));
DROP POLICY IF EXISTS "User delete foto profil" ON storage.objects;
CREATE POLICY "User delete foto profil" ON storage.objects FOR DELETE USING ((bucket_id = 'foto-profil'::text));
DROP POLICY IF EXISTS "User update foto profil" ON storage.objects;
CREATE POLICY "User update foto profil" ON storage.objects FOR UPDATE USING ((bucket_id = 'foto-profil'::text));
DROP POLICY IF EXISTS "User upload foto profil" ON storage.objects;
CREATE POLICY "User upload foto profil" ON storage.objects FOR INSERT WITH CHECK ((bucket_id = 'foto-profil'::text));
DROP POLICY IF EXISTS "st_bukti_rw" ON storage.objects;
CREATE POLICY "st_bukti_rw" ON storage.objects FOR ALL USING (((bucket_id = 'bukti-laporan'::text) AND (is_admin() OR laporan_milik_saya(((storage.foldername(name))[1])::uuid)))) WITH CHECK (((bucket_id = 'bukti-laporan'::text) AND laporan_milik_saya(((storage.foldername(name))[1])::uuid)));
DROP POLICY IF EXISTS "st_dokmhs_rw" ON storage.objects;
CREATE POLICY "st_dokmhs_rw" ON storage.objects FOR ALL USING (((bucket_id = ANY (ARRAY['dokumen-mahasiswa'::text, 'dokumen-lamaran'::text])) AND (((storage.foldername(name))[1] = (auth.uid())::text) OR is_admin()))) WITH CHECK (((bucket_id = ANY (ARRAY['dokumen-mahasiswa'::text, 'dokumen-lamaran'::text])) AND ((storage.foldername(name))[1] = (auth.uid())::text)));
DROP POLICY IF EXISTS "st_hasil_admin" ON storage.objects;
CREATE POLICY "st_hasil_admin" ON storage.objects FOR ALL USING (((bucket_id = 'dokumen-hasil'::text) AND is_admin())) WITH CHECK (((bucket_id = 'dokumen-hasil'::text) AND is_admin()));
DROP POLICY IF EXISTS "st_hasil_read" ON storage.objects;
CREATE POLICY "st_hasil_read" ON storage.objects FOR SELECT USING (((bucket_id = 'dokumen-hasil'::text) AND laporan_milik_saya(((storage.foldername(name))[1])::uuid)));
DROP POLICY IF EXISTS "st_logo_admin" ON storage.objects;
CREATE POLICY "st_logo_admin" ON storage.objects FOR ALL USING (((bucket_id = ANY (ARRAY['logo-perusahaan'::text, 'dokumen-mitra'::text])) AND is_admin())) WITH CHECK (((bucket_id = ANY (ARRAY['logo-perusahaan'::text, 'dokumen-mitra'::text])) AND is_admin()));
DROP POLICY IF EXISTS "st_logo_read" ON storage.objects;
CREATE POLICY "st_logo_read" ON storage.objects FOR SELECT USING ((bucket_id = 'logo-perusahaan'::text));
DROP POLICY IF EXISTS "st_surat_rw" ON storage.objects;
CREATE POLICY "st_surat_rw" ON storage.objects FOR ALL USING (((bucket_id = 'lampiran-surat'::text) AND thread_surat_boleh(((storage.foldername(name))[1])::uuid))) WITH CHECK (((bucket_id = 'lampiran-surat'::text) AND thread_surat_boleh(((storage.foldername(name))[1])::uuid)));

-- 9. Master Lookup Data (Kategori Laporan)
INSERT INTO public.kategori_laporan (id, nama) VALUES ('154267c1-e5b8-42f9-9a68-f9a64a9d627f', 'Kekerasan') ON CONFLICT (id) DO NOTHING;
INSERT INTO public.kategori_laporan (id, nama) VALUES ('6a1172ef-659a-4651-95cb-2042e17dfbb5', 'Pelecehan Verbal') ON CONFLICT (id) DO NOTHING;
INSERT INTO public.kategori_laporan (id, nama) VALUES ('a0c921d0-e659-45a8-838d-8373ba79adad', 'Pelecehan Fisik') ON CONFLICT (id) DO NOTHING;
INSERT INTO public.kategori_laporan (id, nama) VALUES ('b67e4f05-ea7b-4df7-aa75-fa7422016e68', 'Lainnya') ON CONFLICT (id) DO NOTHING;
INSERT INTO public.kategori_laporan (id, nama) VALUES ('ca1095da-d5a8-4460-8316-c2f2bc465736', 'Perundungan') ON CONFLICT (id) DO NOTHING;
INSERT INTO public.kategori_laporan (id, nama) VALUES ('ea12606e-8ef7-4f76-9e13-c6447cf34962', 'Diskriminasi') ON CONFLICT (id) DO NOTHING;

