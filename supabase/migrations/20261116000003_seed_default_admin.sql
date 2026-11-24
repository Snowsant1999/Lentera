-- Seed: 1 Akun Admin Awal untuk Instansi Kampus Baru
-- Dijalankan otomatis pada database kosongan: hanya membuat 1 user admin jika belum ada admin di public.profiles.
-- Kredensial default (SEGERA UBAH PASSWORD setelah login pertama kali):
--   Email    : admin@lentera.ac.id
--   Password : AdminLentera2026!

DO $$
DECLARE
    v_admin_id uuid := gen_random_uuid();
    v_email text := 'admin@lentera.ac.id';
    v_password text := 'AdminLentera2026!';
BEGIN
    IF EXISTS (SELECT 1 FROM public.profiles WHERE role = 'admin'::user_role) THEN
        RETURN;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM auth.users WHERE lower(email) = v_email) THEN
        INSERT INTO auth.users (
            instance_id,
            id,
            aud,
            role,
            email,
            encrypted_password,
            email_confirmed_at,
            raw_app_meta_data,
            raw_user_meta_data,
            created_at,
            updated_at,
            confirmation_token,
            email_change,
            email_change_token_new,
            recovery_token
        ) VALUES (
            '00000000-0000-0000-0000-000000000000',
            v_admin_id,
            'authenticated',
            'authenticated',
            v_email,
            crypt(v_password, gen_salt('bf')),
            now(),
            '{"provider":"email","providers":["email"]}'::jsonb,
            '{"nama_lengkap":"Admin Kampus LENTERA","role":"admin"}'::jsonb,
            now(),
            now(),
            '',
            '',
            '',
            ''
        );

        INSERT INTO auth.identities (
            id,
            user_id,
            identity_data,
            provider,
            provider_id,
            last_sign_in_at,
            created_at,
            updated_at
        ) VALUES (
            gen_random_uuid(),
            v_admin_id,
            jsonb_build_object('sub', v_admin_id::text, 'email', v_email),
            'email',
            v_admin_id::text,
            now(),
            now(),
            now()
        );
    ELSE
        SELECT id INTO v_admin_id FROM auth.users WHERE lower(email) = v_email LIMIT 1;
    END IF;

    INSERT INTO public.profiles (
        id,
        role,
        nama_lengkap,
        email,
        status_akun,
        created_at,
        updated_at
    ) VALUES (
        v_admin_id,
        'admin'::user_role,
        'Admin Kampus LENTERA',
        v_email,
        'aktif'::status_akun,
        now(),
        now()
    )
    ON CONFLICT (id) DO NOTHING;
END $$;
