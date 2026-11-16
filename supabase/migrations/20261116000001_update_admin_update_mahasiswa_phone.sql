-- Migration: Tambahkan parameter p_no_telepon ke RPC admin_update_mahasiswa (B4)
-- Menyelaraskan signature RPC Supabase dengan AdminMahasiswaRepository.updateMahasiswaProfile.

DROP FUNCTION IF EXISTS public.admin_update_mahasiswa(uuid, text, text, text, text, text, text, text);

CREATE OR REPLACE FUNCTION public.admin_update_mahasiswa(
  p_user_id uuid,
  p_nama_lengkap text,
  p_nim text,
  p_email text,
  p_perguruan_tinggi text default null,
  p_program_studi text default null,
  p_angkatan text default null,
  p_jurusan text default null,
  p_no_telepon text default null
)
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, auth, extensions
AS $$
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
$$;

GRANT EXECUTE ON FUNCTION public.admin_update_mahasiswa(uuid, text, text, text, text, text, text, text, text) TO authenticated;
