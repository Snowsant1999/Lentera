-- Migration: 20261110000001_fix_lowongan_and_profile_photos.sql
-- Description: Fix RLS permissions for lowongan and tempat_magang, setup profile photo column and storage bucket

-- 1. Berikan hak akses SELECT pada view dan tabel lowongan & tempat_magang untuk anon dan authenticated
GRANT SELECT ON public.v_lowongan_terbit TO anon, authenticated;
GRANT SELECT ON public.lowongan TO anon, authenticated;
GRANT SELECT ON public.tempat_magang TO anon, authenticated;

-- Pastikan view berjalan dengan hak owner (security_invoker = false) agar pembacaan lowongan terbit tidak terhalang RLS internal
ALTER VIEW IF EXISTS public.v_lowongan_terbit SET (security_invoker = false);

-- 2. Kebijakan RLS pada tabel lowongan
ALTER TABLE public.lowongan ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS lowongan_read_public ON public.lowongan;
CREATE POLICY lowongan_read_public ON public.lowongan
  FOR SELECT TO anon, authenticated
  USING (status = 'terbit' OR public.is_admin());

DROP POLICY IF EXISTS lowongan_admin_manage ON public.lowongan;
CREATE POLICY lowongan_admin_manage ON public.lowongan
  FOR ALL TO authenticated
  USING (public.is_admin())
  WITH CHECK (public.is_admin());

-- 3. Kebijakan RLS pada tabel tempat_magang
ALTER TABLE public.tempat_magang ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS tempat_magang_read_public ON public.tempat_magang;
CREATE POLICY tempat_magang_read_public ON public.tempat_magang
  FOR SELECT TO anon, authenticated
  USING (status = 'aktif' OR public.is_admin());

DROP POLICY IF EXISTS tempat_magang_admin_manage ON public.tempat_magang;
CREATE POLICY tempat_magang_admin_manage ON public.tempat_magang
  FOR ALL TO authenticated
  USING (public.is_admin())
  WITH CHECK (public.is_admin());

-- 4. Tambah kolom foto_profil_url di tabel profiles jika belum ada
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS foto_profil_url text;

-- Izinkan pengguna memperbarui foto profil mereka sendiri
-- (Jika ada trigger guard_profile_update, pastikan kolom foto_profil_url diizinkan)
GRANT UPDATE (nama_lengkap, no_telepon, perguruan_tinggi, jurusan, program_studi, angkatan, foto_profil_url) 
  ON public.profiles TO authenticated;

-- 5. Konfigurasi Storage Bucket untuk foto-profil
INSERT INTO storage.buckets (id, name, public)
VALUES ('foto-profil', 'foto-profil', true)
ON CONFLICT (id) DO UPDATE SET public = true;

DROP POLICY IF EXISTS "Public read foto profil" ON storage.objects;
CREATE POLICY "Public read foto profil" ON storage.objects
  FOR SELECT TO anon, authenticated
  USING (bucket_id = 'foto-profil');

DROP POLICY IF EXISTS "User upload foto profil" ON storage.objects;
CREATE POLICY "User upload foto profil" ON storage.objects
  FOR INSERT TO authenticated
  WITH CHECK (bucket_id = 'foto-profil');

DROP POLICY IF EXISTS "User update foto profil" ON storage.objects;
CREATE POLICY "User update foto profil" ON storage.objects
  FOR UPDATE TO authenticated
  USING (bucket_id = 'foto-profil');

DROP POLICY IF EXISTS "User delete foto profil" ON storage.objects;
CREATE POLICY "User delete foto profil" ON storage.objects
  FOR DELETE TO authenticated
  USING (bucket_id = 'foto-profil');

