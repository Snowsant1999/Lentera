-- ==============================================================================
-- Migrasi: Perbaikan Trigger trg_notifikasi_permintaan_info
-- Masalah: Fungsi trigger sebelumnya membaca NEW.catatan_admin yang tidak ada pada tabel
--          permintaan_informasi (kolom yang benar adalah pesan_ke_pelapor).
-- ==============================================================================

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
      'Admin kampus meminta informasi tambahan untuk laporan ' || coalesce(v_nomor_laporan, '') || ': ' || coalesce(NEW.pesan_ke_pelapor, ''),
      'laporan'::notif_kategori,
      false,
      now()
    );
  end if;
  return NEW;
end;
$$;

DROP TRIGGER IF EXISTS trg_permintaan_info_notifikasi ON public.permintaan_informasi;

CREATE TRIGGER trg_permintaan_info_notifikasi
AFTER INSERT ON public.permintaan_informasi
FOR EACH ROW
EXECUTE FUNCTION public.trg_notifikasi_permintaan_info();

