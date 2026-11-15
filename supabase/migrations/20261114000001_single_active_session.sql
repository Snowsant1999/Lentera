-- Migration: Single Active Session per Akun (C1)
-- Memastikan hanya ada 1 sesi login aktif per akun pada satu waktu (berlaku untuk mahasiswa dan admin).

CREATE TABLE IF NOT EXISTS public.sesi_aktif (
    user_id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    device_id TEXT NOT NULL,
    device_info TEXT NOT NULL DEFAULT '',
    last_seen TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Index untuk efisiensi pembersihan dan pengecekan detak (last_seen)
CREATE INDEX IF NOT EXISTS idx_sesi_aktif_last_seen ON public.sesi_aktif(last_seen);

-- Enable RLS
ALTER TABLE public.sesi_aktif ENABLE ROW LEVEL SECURITY;

-- Policy RLS: Pengguna hanya dapat membaca sesi aktifnya sendiri
DROP POLICY IF EXISTS "Pengguna dapat membaca sesi aktifnya sendiri" ON public.sesi_aktif;
CREATE POLICY "Pengguna dapat membaca sesi aktifnya sendiri"
    ON public.sesi_aktif
    FOR SELECT
    USING (auth.uid() = user_id);

-- RPC: klaim_sesi
CREATE OR REPLACE FUNCTION public.klaim_sesi(p_device_id TEXT, p_device_info TEXT DEFAULT '')
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_user_id UUID;
    v_existing RECORD;
BEGIN
    v_user_id := auth.uid();
    IF v_user_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'message', 'Sesi autentikasi tidak valid.');
    END IF;

    -- Cek sesi aktif saat ini
    SELECT * INTO v_existing FROM public.sesi_aktif WHERE user_id = v_user_id;

    IF FOUND THEN
        -- Jika perangkat berbeda dan detak terakhir masih dalam 5 menit terakhir
        IF v_existing.device_id <> p_device_id AND v_existing.last_seen > (now() - interval '5 minutes') THEN
            RETURN jsonb_build_object(
                'success', false,
                'message', 'Akun sedang aktif digunakan di perangkat lain (' || COALESCE(NULLIF(v_existing.device_info, ''), 'Perangkat lain') || '). Pastikan logout terlebih dahulu atau tunggu beberapa menit.'
            );
        END IF;

        -- Jika perangkat sama atau sesi lama sudah kedaluwarsa (> 5 menit), perbarui sesi ke perangkat ini
        UPDATE public.sesi_aktif
        SET device_id = p_device_id,
            device_info = COALESCE(NULLIF(p_device_info, ''), v_existing.device_info),
            last_seen = now()
        WHERE user_id = v_user_id;
    ELSE
        -- Buat catatan sesi baru
        INSERT INTO public.sesi_aktif (user_id, device_id, device_info, last_seen)
        VALUES (v_user_id, p_device_id, p_device_info, now());
    END IF;

    RETURN jsonb_build_object('success', true, 'message', 'Sesi berhasil diklaim.');
END;
$$;

-- RPC: detak_sesi (heartbeat)
CREATE OR REPLACE FUNCTION public.detak_sesi(p_device_id TEXT)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_user_id UUID;
BEGIN
    v_user_id := auth.uid();
    IF v_user_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'message', 'Sesi autentikasi tidak valid.');
    END IF;

    UPDATE public.sesi_aktif
    SET last_seen = now()
    WHERE user_id = v_user_id AND device_id = p_device_id;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'message', 'Sesi tidak ditemukan atau telah diambil alih.');
    END IF;

    RETURN jsonb_build_object('success', true, 'message', 'Detak berhasil diperbarui.');
END;
$$;

-- RPC: lepas_sesi (logout)
CREATE OR REPLACE FUNCTION public.lepas_sesi(p_device_id TEXT)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_user_id UUID;
BEGIN
    v_user_id := auth.uid();
    IF v_user_id IS NULL THEN
        RETURN jsonb_build_object('success', true, 'message', 'Sudah logout.');
    END IF;

    DELETE FROM public.sesi_aktif
    WHERE user_id = v_user_id AND device_id = p_device_id;

    RETURN jsonb_build_object('success', true, 'message', 'Sesi berhasil dilepas.');
END;
$$;

