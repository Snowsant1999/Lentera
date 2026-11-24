-- Migration: 20261116000002_unify_notifications_and_admin_triggers.sql
-- Description: N1.4 & N1.6 - Konsolidasi notifikasi admin & mahasiswa melalui tabel notifikasi,
--              kolom push_terkirim_at untuk anti-duplikat server-side, dan referensi_id untuk navigasi.

ALTER TABLE public.notifikasi
  ADD COLUMN IF NOT EXISTS push_terkirim_at TIMESTAMPTZ DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS referensi_id UUID DEFAULT NULL;

CREATE INDEX IF NOT EXISTS idx_notifikasi_user_unpushed
  ON public.notifikasi(user_id, created_at DESC)
  WHERE push_terkirim_at IS NULL;

-- RPC untuk menandai notifikasi telah ditampilkan di sistem HP pengguna (dipanggil hanya setelah izin aktif)
CREATE OR REPLACE FUNCTION public.tandai_notifikasi_terkirim(p_notif_ids uuid[])
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path TO 'public'
AS $$
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
$$;

-- Trigger Laporan: INSERT -> notifikasi ke semua admin aktif; UPDATE status -> notifikasi ke mahasiswa pelapor
CREATE OR REPLACE FUNCTION public.trg_notifikasi_laporan()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path TO 'public'
AS $$
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
$$;

DROP TRIGGER IF EXISTS trg_laporan_notifikasi ON public.laporan;
CREATE TRIGGER trg_laporan_notifikasi
AFTER INSERT OR UPDATE ON public.laporan
FOR EACH ROW
EXECUTE FUNCTION public.trg_notifikasi_laporan();

-- Trigger Lamaran: INSERT / perbaikan -> notifikasi ke admin aktif; UPDATE status lain -> notifikasi ke mahasiswa
CREATE OR REPLACE FUNCTION public.trg_notifikasi_lamaran()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path TO 'public'
AS $$
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
$$;

DROP TRIGGER IF EXISTS trg_lamaran_notifikasi ON public.lamaran;
CREATE TRIGGER trg_lamaran_notifikasi
AFTER INSERT OR UPDATE ON public.lamaran
FOR EACH ROW
EXECUTE FUNCTION public.trg_notifikasi_lamaran();

-- Trigger Permintaan Informasi: INSERT -> notifikasi ke mahasiswa; UPDATE jawaban -> notifikasi ke admin aktif
CREATE OR REPLACE FUNCTION public.trg_notifikasi_permintaan_info()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path TO 'public'
AS $$
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
$$;

DROP TRIGGER IF EXISTS trg_permintaan_info_notifikasi ON public.permintaan_informasi;
CREATE TRIGGER trg_permintaan_info_notifikasi
AFTER INSERT OR UPDATE ON public.permintaan_informasi
FOR EACH ROW
EXECUTE FUNCTION public.trg_notifikasi_permintaan_info();
