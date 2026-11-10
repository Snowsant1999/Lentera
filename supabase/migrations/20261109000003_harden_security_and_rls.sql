-- Migration: 20261109000003_harden_security_and_rls.sql
-- Description: Hardening security privileges and RLS policies for release 1.0

-- 1. Cabut hak eksekusi anon/public pada RPC administratif dan perbaikan lamaran
REVOKE EXECUTE ON FUNCTION public.admin_delete_mahasiswa(uuid) FROM anon, public;
GRANT EXECUTE ON FUNCTION public.admin_delete_mahasiswa(uuid) TO authenticated;

REVOKE EXECUTE ON FUNCTION public.admin_update_mahasiswa(uuid, text, text, text, text, text, text, text) FROM anon, public;
GRANT EXECUTE ON FUNCTION public.admin_update_mahasiswa(uuid, text, text, text, text, text, text, text) TO authenticated;

REVOKE EXECUTE ON FUNCTION public.mahasiswa_ajukan_perbaikan_lamaran(uuid) FROM anon, public;
GRANT EXECUTE ON FUNCTION public.mahasiswa_ajukan_perbaikan_lamaran(uuid) TO authenticated;

-- 2. Cabut hak eksekusi langsung pada fungsi trigger notifikasi agar tidak bisa ditembak via REST RPC
REVOKE EXECUTE ON FUNCTION public.trg_notifikasi_lamaran() FROM public, anon, authenticated;
REVOKE EXECUTE ON FUNCTION public.trg_notifikasi_laporan() FROM public, anon, authenticated;
REVOKE EXECUTE ON FUNCTION public.trg_notifikasi_permintaan_info() FROM public, anon, authenticated;

-- 3. Kebijakan RLS pada tabel public.laporan
ALTER TABLE public.laporan ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS laporan_select_own ON public.laporan;
CREATE POLICY laporan_select_own ON public.laporan
  FOR SELECT TO authenticated
  USING (pelapor_id = (SELECT auth.uid()));

DROP POLICY IF EXISTS laporan_admin_all ON public.laporan;
CREATE POLICY laporan_admin_all ON public.laporan
  FOR ALL TO authenticated
  USING (public.is_admin())
  WITH CHECK (public.is_admin());

-- 4. Set security_invoker = true pada view lowongan
ALTER VIEW IF EXISTS public.v_lowongan_terbit SET (security_invoker = true);

