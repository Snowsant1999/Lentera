# Panduan Lengkap Setup Backend Supabase — Lentera

Dokumen ini merupakan panduan komprehensif penyiapan **backend Supabase** dari nol hingga siap digunakan oleh aplikasi **Lentera (Android / Jetpack Compose)**. Panduan ini mencakup cara membuat project, mengambil `SUPABASE_URL` dan `SUPABASE_KEY`, menghubungkannya ke aplikasi, setup database & storage, konfigurasi autentikasi, serta panduan lengkap pembuatan dan penambahan akun Admin.

---

## 1. Arsitektur & Peran Supabase di Lentera

| Layanan Supabase | Peran & Implementasi di Lentera |
| :--- | :--- |
| **Authentication** | Login berbasis Email & Password (mahasiswa dapat login memakai NIM yang dikonversi ke email melalui RPC `get_email_by_nim`), aktivasi akun baru, dan alur reset password berbasis deep link. |
| **PostgreSQL Database** | Penyimpanan relasional untuk entitas Pengaduan (Lentera Siaga), Magang Hub, Surat-Menyurat, Notifikasi, serta Data Akun Profil. Dilengkapi dengan Views dan Stored Procedures (RPC). |
| **Row Level Security (RLS)** | Membatasi hak akses data secara ketat di level database: mahasiswa hanya dapat mengakses data miliknya sendiri, sedangkan admin kampus memiliki otoritas pengawasan dan penanganan laporan. |
| **Storage (Object Storage)** | Menyimpan dokumen lampiran bukti laporan, dokumen lamaran, CV, transkrip, dan surat. Semua bucket berstatus **Private** dan hanya diakses menggunakan *Signed URL*. |

Klien Supabase diinisialisasi sebagai singleton pada file [`app/src/main/java/com/PPBM/lentera/backend/core/SupabaseConfig.kt`](file:///D:/HAIKAL/Kuliah/Pemrograman%20Perangkat%20Bergerak/AplikasiLentera/LenteraApp/app/src/main/java/com/PPBM/lentera/backend/core/SupabaseConfig.kt) dengan modul `Auth` (FlowType `IMPLICIT`), `Postgrest`, dan `Storage`.

---

## 2. Prasyarat

- Akun aktif di [Supabase Console](https://supabase.com).
- Android Studio Ladybug / Meerkat atau yang lebih baru dengan JDK 11+.
- Proyek Android Lentera (`minSdk 26`, `targetSdk 37`).
- *(Opsional)* [Supabase CLI](https://supabase.com/docs/guides/cli) terpasang di terminal Anda jika ingin mengelola migrasi secara otomatis.

---

## 3. Langkah 1: Membuat Project Supabase Baru

1. Kunjungi [Supabase Dashboard](https://supabase.com/dashboard) dan klik **New project**.
2. Pilih Organization Anda, lalu lengkapi informasi:
   * **Name**: `LenteraApp` (atau nama lain sesuai kebutuhan).
   * **Database Password**: Buat password yang kuat dan simpan di tempat yang aman.
   * **Region**: Pilih wilayah terdekat dari target pengguna (disarankan `Singapore (ap-southeast-1)` untuk Indonesia).
3. Klik **Create new project** dan tunggu proses provisi server selesai (status berubah menjadi *Healthy*, sekitar 1–2 menit).

---

## 4. Langkah 2: Mengambil `SUPABASE_URL` dan `SUPABASE_KEY`

Aplikasi Lentera membutuhkan dua variabel kunci agar dapat berkomunikasi dengan backend:

### 4.1 Mengambil `SUPABASE_URL` (Project URL)
1. Pada menu navigasi sebelah kiri di dashboard project Supabase, klik ikon gerigi **Project Settings**.
2. Pilih submenu **Data API** (pada versi dashboard tertentu bernama **API**).
3. Cari bagian **Project URL**. Salin URL tersebut.
   * Format URL: `https://<project-ref>.supabase.co`
   * Bagian `<project-ref>` adalah kode acak unik project Anda (contoh: `zrzypkduulwtkumqkubu`).

### 4.2 Mengambil `SUPABASE_KEY` (API Key Publik)
1. Masih di halaman **Project Settings**, klik submenu **API Keys** (atau tetap di bagian *API Keys* pada menu API).
2. Perhatikan tab yang tersedia pada dashboard Supabase Anda:
   * **Jika menggunakan tab "Legacy API Keys"**: Salin key dengan label **`anon` `public`**. Key ini berupa token JWT panjang yang diawali dengan `eyJ...`. Key inilah yang saat ini digunakan pada konfigurasi bawaan aplikasi.
   * **Jika menggunakan tab "Publishable and secret API keys"**: Salin **Publishable key** (diawali dengan `sb_publishable_...`). Key ini merupakan pengganti standar baru untuk `anon` key dan aman disematkan pada aplikasi klien.

> [!CAUTION]
> **PENTING: Jangan pernah memasukkan key `service_role` atau `secret key` (`sb_secret_...`) ke dalam aplikasi Android!**
> Key `service_role` memiliki hak bypass penuh terhadap seluruh aturan keamanan (RLS). Karena file APK Android dapat dibongkar (*reverse engineered*), menyematkan service role key akan membuat database Anda rentan dieksploitasi pihak luar. Aplikasi Android hanya boleh menggunakan `anon` / `publishable` key.

---

## 5. Langkah 3: Menghubungkan Key ke Aplikasi Android

### Cara A: Melalui `local.properties` (Sangat Disarankan & Aman untuk Kolaborasi Git)
File `local.properties` sudah masuk ke dalam `.gitignore` sehingga credential project Anda tidak akan bocor ke GitHub repository.

1. Buka file `local.properties` yang terletak di direktori root `LenteraApp/`.
2. Tambahkan dua baris berikut di bagian paling bawah:
   ```properties
   SUPABASE_URL=https://<project-ref>.supabase.co
   SUPABASE_KEY=<ANON_ATAU_PUBLISHABLE_KEY>
   ```
3. Buka file [`app/build.gradle.kts`](file:///D:/HAIKAL/Kuliah/Pemrograman%20Perangkat%20Bergerak/AplikasiLentera/LenteraApp/app/build.gradle.kts) dan pastikan `buildConfigField` membaca dari `local.properties`:
   ```kotlin
   import java.util.Properties

   val localProperties = Properties().apply {
       val localFile = rootProject.file("local.properties")
       if (localFile.exists()) {
           localFile.inputStream().use { load(it) }
       }
   }

   android {
       ...
       defaultConfig {
           ...
           buildConfigField(
               "String",
               "SUPABASE_URL",
               "\"${localProperties.getProperty("SUPABASE_URL") ?: ""}\""
           )
           buildConfigField(
               "String",
               "SUPABASE_KEY",
               "\"${localProperties.getProperty("SUPABASE_KEY") ?: ""}\""
           )
       }
   }
   ```
4. Klik **Sync Now** pada Android Studio.

### Cara B: Langsung di `app/build.gradle.kts` (Pengembangan Cepat Lokal)
Jika hanya dikerjakan di komputer lokal, Anda dapat menuliskan nilai key langsung di dalam `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "SUPABASE_URL", "\"https://<project-ref>.supabase.co\"")
buildConfigField("String", "SUPABASE_KEY", "\"<ANON_ATAU_PUBLISHABLE_KEY>\"")
```
Lalu lakukan **Sync Project with Gradle Files**.

---

## 6. Langkah 4: Setup Skema Basis Data & Migrasi

Skema database Lentera tersusun dari struktur relasional berikut:

* **Tabel Utama (18 Tabel)**:
  `profiles`, `kategori_laporan`, `laporan`, `bukti_laporan`, `log_tindakan`, `permintaan_informasi`, `dokumen_hasil`, `tempat_magang`, `dokumen_tempat_magang`, `lowongan`, `bookmark_lowongan`, `lamaran`, `lamaran_riwayat`, `dokumen_lamaran`, `dokumen_mahasiswa`, `notifikasi`, `surat`, `surat_lampiran`.
* **Views**:
  `v_laporan_saya`, `v_timeline_laporan_saya`, `v_laporan_admin`, `v_lowongan_terbit`.
* **Stored Procedures / RPC**:
  `admin_create_mahasiswa_v2`, `admin_update_mahasiswa`, `admin_delete_mahasiswa`, `admin_update_laporan`, `buat_laporan`, `aktivasi_akun_saya`, `mahasiswa_ajukan_perbaikan_lamaran`, `get_admin_dashboard_stats`, `get_email_by_nim`.

### 6.1 Menerapkan Migrasi
Di dalam folder [`supabase/migrations/`](file:///D:/HAIKAL/Kuliah/Pemrograman%20Perangkat%20Bergerak/AplikasiLentera/LenteraApp/supabase/migrations), terdapat berkas migrasi pembaruan:
* `20261109000002_add_admin_and_notification_triggers.sql`: Menambahkan RPC manajemen mahasiswa admin, perbaikan berkas lamaran, dan trigger otomatis notifikasi.
* `20261109000003_harden_security_and_rls.sql`: Menutup akses RPC administratif dari peran `anon`/publik, menerapkan RLS tabel `laporan`, dan mengatur atribut `security_invoker`.

> [!NOTE]
> Jika Anda menyiapkan database dari project yang benar-benar baru (fresh Supabase project), lakukan ekspor skema dasar terlebih dahulu menggunakan Supabase CLI:
> ```bash
> supabase login
> supabase link --project-ref <project-ref-lama>
> supabase db dump --schema public -f supabase/migrations/20260101000000_baseline_schema.sql
> ```
> Lalu terapkan ke project baru melalui menu **SQL Editor** pada Supabase Dashboard atau via perintah `supabase db push`.

---

## 7. Langkah 5: Setup Storage (Private Buckets)

Aplikasi Lentera membutuhkan **7 Bucket Storage** untuk menampung seluruh berkas digital. Seluruh bucket wajib berstatus **Private**:

1. Masuk ke menu **Storage** di dashboard Supabase.
2. Klik tombol **New bucket**.
3. Buat satu per satu bucket berikut (pastikan toggle **Public bucket** dalam keadaan **OFF / MATI**):
   * `bukti-laporan` (Foto/dokumen bukti saat pelapor membuat pengaduan)
   * `dokumen-hasil` (Dokumen berita acara/hasil penanganan laporan dari admin)
   * `dokumen-lamaran` (CV & transkrip pendaftaran lowongan magang)
   * `dokumen-mahasiswa` (Dokumen arsip pribadi mahasiswa di profil)
   * `dokumen-mitra` (MoU dan profil perusahaan mitra)
   * `lampiran-surat` (Berkas lampiran pada menu korespondensi)
   * `logo-perusahaan` (Logo instansi mitra magang)
4. Simpan masing-masing bucket.

Aplikasi Lentera mengakses dan menampilkan berkas-berkas ini secara aman menggunakan *Signed URL* berbatas waktu kadaluwarsa (`getSignedUrl(...)`), sehingga berkas tidak dapat diunduh oleh publik tanpa otorisasi.

---

## 8. Langkah 6: Konfigurasi Authentication & Deep Linking

1. Buka menu **Authentication** di dashboard Supabase.
2. Di tab **Providers**, pastikan provider **Email** aktif.
   * Nonaktifkan fitur *Confirm email* jika ingin akun mahasiswa yang dibuat admin langsung dapat digunakan.
3. Di tab **URL Configuration**:
   * Masukkan **Redirect URLs**:
     ```
     lentera://reset-password
     ```
   * Pengaturan ini krusial agar saat pengguna mengeklik tautan pemulihan kata sandi di email, sistem Android secara otomatis membuka aplikasi Lentera pada halaman ganti kata sandi.
4. Di tab **Attack Protection**:
   * Aktifkan **Leaked password protection** untuk mencegah penggunaan kata sandi yang telah terkompromi secara global.

### 8.1 Cara Mengatur Supabase Agar Mengirim Kode OTP 6 Digit (Bukan Link URL)
Secara bawaan (*default*), Supabase menyetel template email menggunakan variabel `{{ .ConfirmationURL }}`. Variabel ini membuat Supabase mengirimkan tautan klik (*Magic Link URL*), sehingga pengguna menerima link dan bukan 6 angka kode OTP.

Karena aplikasi Lentera menggunakan layar input 6 digit kotak angka ([`VerifikasiPemulihanScreen.kt`](file:///D:/HAIKAL/Kuliah/Pemrograman%20Perangkat%20Bergerak/AplikasiLentera/LenteraApp/app/src/main/java/com/PPBM/lentera/features/auth/VerifikasiPemulihanScreen.kt)), template email di Supabase **wajib diubah** agar menampilkan variabel **`{{ .Token }}`**:

1. Masuk ke dashboard Supabase > Menu **Authentication** > **Email Templates**.
2. Pilih tab **Magic Link** (karena alur login OTP Auth menggunakan alur ini) dan juga tab **Reset Password**.
3. Pada bagian **Subject**, ubah menjadi: `Kode Verifikasi Pemulihan Kata Sandi Lentera`
4. Pada bagian **Body**, ganti isi HTML bawaan dengan template kode OTP berikut:
   ```html
   <h2>Kode Verifikasi Pemulihan Kata Sandi</h2>
   <p>Halo,</p>
   <p>Kami menerima permintaan untuk mengatur ulang kata sandi akun Lentera Anda.</p>
   <p>Gunakan kode verifikasi (OTP) 6 digit berikut pada aplikasi:</p>
   <div style="background-color: #f1f5f9; padding: 18px; border-radius: 8px; text-align: center; margin: 20px 0;">
     <span style="font-size: 36px; font-weight: bold; letter-spacing: 10px; color: #1e293b; font-family: monospace;">{{ .Token }}</span>
   </div>
   <p>Masukkan 6 digit angka di atas pada aplikasi Lentera. Kode ini berlaku selama 5 menit.</p>
   <p style="color: #64748b; font-size: 12px;">Jika Anda tidak merasa meminta kode ini, silakan abaikan email ini.</p>
   ```
5. Klik **Save changes** pada masing-masing template.
6. Setelah disimpan, saat mahasiswa atau admin menekan *Kirim Kode* di aplikasi, Supabase akan mengirimkan 6 digit angka OTP (contoh: `849201`) yang bisa langsung diketikkan ke dalam 6 kotak verifikasi di aplikasi Lentera.

---

## 9. Langkah 7: Panduan Menambahkan Akun Admin (Admin Pertama & Admin Tambahan)

Untuk menambahkan akun Admin baru (baik akun pertama maupun jika ingin menambah admin ke-2, ke-3, dst.), **semua proses cukup dilakukan di Supabase saja**. Anda tidak perlu mengubah kode program aplikasi Android atau melakukan re-build APK.

### Mengapa Perlu 2 Tempat di Supabase?
Akun admin membutuhkan dua komponen yang saling terhubung melalui **User UID (UUID unik pengguna)**:
1. **`auth.users`**: Menyimpan kredensial login (Email & Password terenkripsi).
2. **`public.profiles`**: Menyimpan data identitas aplikasi dengan kolom **`role = 'admin'`** dan **`status_akun = 'aktif'`**. Kolom `id` di tabel ini harus **sama persis** dengan `id` (UID) di `auth.users`.

---

### Cara Praktis Menambahkan Admin (Paling Disarankan)

#### Langkah 1: Buat Akun di Supabase Auth
1. Buka menu **Authentication** > **Users** pada dashboard Supabase.
2. Klik tombol **Add user** (di pojok kanan atas) > pilih **Create user**.
3. Isi data akun:
   * **Email**: Masukkan email admin baru (misal: `admin.baru@lentera.ac.id`).
   * **Password**: Tentukan kata sandi untuk admin tersebut.
   * **Auto Confirm User?**: **WAJIB DICENTANG** (agar akun langsung aktif tanpa menunggu verifikasi email).
4. Klik **Create user**.
5. Salin kode **User UID** dari user yang baru dibuat (berupa format UUID seperti `088a2b8f-74a1-4223-8d1f-1a6e4b4d3f5e`).

#### Langkah 2: Berikan Hak Akses Admin di SQL Editor
1. Buka menu **SQL Editor** di dashboard Supabase.
2. Klik **New query**, tempel script berikut, lalu sesuaikan nilai UID, Nama, dan Email:

```sql
INSERT INTO public.profiles (
  id,
  nama_lengkap,
  email,
  role,
  status_akun
) VALUES (
  'PASTE_USER_UID_DISINI',        -- Ganti dengan User UID dari Langkah 1
  'Nama Admin Baru',             -- Nama lengkap admin
  'admin.baru@lentera.ac.id',     -- Email admin (harus sama persis dengan email Auth)
  'admin',                        -- Wajib bernilai 'admin'
  'aktif'                         -- Wajib bernilai 'aktif'
)
ON CONFLICT (id) DO UPDATE 
SET role = 'admin', 
    status_akun = 'aktif',
    updated_at = now();
```
3. Klik tombol **Run** (atau tekan `Ctrl + Enter`).

---

### Alternatif: Script SQL All-in-One (Otomatis Sekali Jalan)
Jika Anda tidak ingin repot menyalin UID secara manual, buka **SQL Editor** dan jalankan script berikut (cukup ubah variabel email, password, dan nama):

```sql
DO $$
DECLARE
  v_user_id uuid := gen_random_uuid();
  v_email text := 'admin.baru@lentera.ac.id';  -- Masukkan email admin baru
  v_password text := 'PasswordAdmin123!';      -- Masukkan password admin baru
  v_nama text := 'Admin Baru Lentera';        -- Masukkan nama admin baru
BEGIN
  -- 1. Buat record kredensial login di auth.users
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
    updated_at
  ) VALUES (
    '00000000-0000-0000-0000-000000000000',
    v_user_id,
    'authenticated',
    'authenticated',
    v_email,
    crypt(v_password, gen_salt('bf')),
    now(),
    '{"provider":"email","providers":["email"]}',
    jsonb_build_object('nama_lengkap', v_nama),
    now(),
    now()
  );

  -- 2. Daftarkan identity provider email
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
    v_user_id,
    jsonb_build_object('sub', v_user_id::text, 'email', v_email),
    'email',
    v_user_id::text,
    now(),
    now(),
    now()
  );

  -- 3. Daftarkan ke profiles sebagai Admin Aktif
  INSERT INTO public.profiles (
    id,
    nama_lengkap,
    email,
    role,
    status_akun
  ) VALUES (
    v_user_id,
    v_nama,
    v_email,
    'admin',
    'aktif'
  );

  RAISE NOTICE 'Sukses! Akun admin % telah aktif dengan UID %', v_email, v_user_id;
END $$;
```

---

## 10. Langkah 8: Verifikasi & Uji Coba

1. Buka aplikasi **Lentera** di perangkat Android atau emulator.
2. Di halaman **Login**:
   * Masukkan **Email**: Email admin yang baru dibuat (misal: `admin.baru@lentera.ac.id`).
   * Masukkan **Kata Sandi**: Password admin.
   * Klik tombol **Masuk**.
3. **Hasil yang Diharapkan**:
   * Sistem Supabase memverifikasi kredensial.
   * Aplikasi membaca role dari `public.profiles` dan mendapati nilai `"admin"`.
   * Pengguna **langsung diarahkan ke Dashboard Admin** (dengan menu Beranda, Data Mahasiswa, Siaga/Laporan, Magang Hub, dan Profil Admin).
4. Lakukan pengujian operasional:
   * Buat satu akun mahasiswa melalui menu *Tambah Mahasiswa*.
   * Periksa verifikasi status laporan pengaduan masuk.

---

## 11. Troubleshooting Masalah Umum

| Kendala | Penyebab Umum | Tindakan Solusi |
| :--- | :--- | :--- |
| **`Invalid API key`** | Nilai `SUPABASE_KEY` salah atau terpotong spasi. | Salin ulang dari menu **Project Settings > API Keys** dan pastikan tidak ada karakter terpotong. |
| **Login berhasil tapi diarahkan ke Dashboard Mahasiswa** | Kolom `role` di tabel `profiles` masih bernilai `'mahasiswa'`. | Buka **SQL Editor** dan jalankan: `UPDATE public.profiles SET role = 'admin' WHERE email = 'email_admin';` |
| **Muncul pesan error "Akun tidak ditemukan" saat login** | Record di tabel `public.profiles` belum dibuat atau UID tidak cocok dengan `auth.users`. | Periksa apakah `id` di `profiles` sama persis dengan `User UID` di `auth.users`. |
| **Tautan reset password dari email tidak membuka aplikasi** | Redirect URL belum ditambahkan pada konfigurasi Auth. | Tambahkan `lentera://reset-password` di menu **Authentication > URL Configuration > Redirect URLs**. |
| **Gagal mengunggah foto / berkas bukti (`403 Forbidden`)** | Bucket Storage belum dibuat atau nama bucket salah ketik. | Pastikan ke-7 bucket (seperti `bukti-laporan`) sudah terbuat dengan status **Private**. |
