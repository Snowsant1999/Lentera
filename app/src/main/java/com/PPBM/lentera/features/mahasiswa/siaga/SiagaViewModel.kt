package com.PPBM.lentera.features.mahasiswa.siaga

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.PPBM.lentera.backend.mahasiswa.model.BuktiLaporanItem
import com.PPBM.lentera.backend.mahasiswa.model.DokumenHasilItem
import com.PPBM.lentera.backend.mahasiswa.model.LaporanSaya
import com.PPBM.lentera.backend.mahasiswa.model.PermintaanInformasiItem
import com.PPBM.lentera.backend.mahasiswa.model.TimelineLaporanSaya
import com.PPBM.lentera.backend.mahasiswa.repository.SiagaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SelectedBukti(
    val uri: Uri? = null,
    val fileName: String,
    val fileType: String,
    val fileSizeFormatted: String,
    val fileBytes: ByteArray? = null,
    val isExcluded: Boolean = false
)

data class SiagaUiState(
    val isLoading: Boolean = false,
    val laporanList: List<LaporanSaya> = emptyList(),
    val errorMessage: String? = null,

    // Form pembuatan laporan
    val selectedKategoriId: Long = 2L, // Default Pelecehan Verbal
    val selectedKategoriNama: String = "Pelecehan Verbal",
    val formJudul: String = "",
    val formTanggalWaktu: String = "",
    val formLokasi: String = "",
    val formKronologi: String = "",
    val formIsAnonim: Boolean = false,
    val uploadedBuktiList: List<SelectedBukti> = emptyList(),

    // Submit state
    val isSubmitting: Boolean = false,
    val submitSuccessNomorLaporan: String? = null,

    // Detail & Tracking
    val detailLaporan: LaporanSaya? = null,
    val timelineList: List<TimelineLaporanSaya> = emptyList(),
    val buktiList: List<BuktiLaporanItem> = emptyList(),
    val permintaanInfoList: List<PermintaanInformasiItem> = emptyList(),
    val dokumenHasilList: List<DokumenHasilItem> = emptyList()
)

class SiagaViewModel(
    private val siagaRepo: SiagaRepository = SiagaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SiagaUiState())
    val uiState: StateFlow<SiagaUiState> = _uiState.asStateFlow()

    init {
        loadLaporanSaya()
    }

    /**
     * Memuat daftar laporan mahasiswa dari view `v_laporan_saya`
     */
    fun loadLaporanSaya() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = siagaRepo.getLaporanSaya()
            result.onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    laporanList = list
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Gagal memuat laporan"
                )
            }
        }
    }

    /**
     * Memilih kategori laporan
     */
    fun setKategori(kategoriId: Long, kategoriNama: String) {
        _uiState.value = _uiState.value.copy(
            selectedKategoriId = kategoriId,
            selectedKategoriNama = kategoriNama,
            formJudul = if (_uiState.value.formJudul.isBlank()) kategoriNama else _uiState.value.formJudul
        )
    }

    /**
     * Mengisi formulir detail kejadian
     */
    fun updateFormData(
        judul: String,
        tanggalWaktu: String,
        lokasi: String,
        kronologi: String,
        isAnonim: Boolean
    ) {
        _uiState.value = _uiState.value.copy(
            formJudul = judul,
            formTanggalWaktu = tanggalWaktu,
            formLokasi = lokasi,
            formKronologi = kronologi,
            formIsAnonim = isAnonim
        )
    }

    /**
     * Menambahkan bukti ke antrean upload
     */
    fun addBuktiFile(
        fileName: String,
        fileType: String,
        fileBytes: ByteArray,
        uri: Uri? = null
    ) {
        val sizeFormatted = formatFileSize(fileBytes.size.toLong())
        val newBukti = SelectedBukti(
            uri = uri,
            fileName = fileName,
            fileType = fileType.uppercase(),
            fileSizeFormatted = sizeFormatted,
            fileBytes = fileBytes,
            isExcluded = false
        )
        val current = _uiState.value.uploadedBuktiList.toMutableList()
        current.add(newBukti)
        _uiState.value = _uiState.value.copy(uploadedBuktiList = current)
    }

    /**
     * Menandai bukti dihapus/disertakan kembali
     */
    fun toggleExcludeBukti(index: Int) {
        val current = _uiState.value.uploadedBuktiList.toMutableList()
        if (index in current.indices) {
            val item = current[index]
            current[index] = item.copy(isExcluded = !item.isExcluded)
            _uiState.value = _uiState.value.copy(uploadedBuktiList = current)
        }
    }

    /**
     * Mengirimkan laporan lengkap ke backend Supabase beserta berkas buktinya
     */
    fun submitLaporan(onSuccess: (nomorLaporan: String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)

            val state = _uiState.value
            val createResult = siagaRepo.createLaporan(
                kategoriId = state.selectedKategoriId,
                judul = state.formJudul.ifBlank { state.selectedKategoriNama },
                kronologi = state.formKronologi,
                lokasi = state.formLokasi,
                tanggalKejadian = state.formTanggalWaktu,
                isAnonim = state.formIsAnonim
            )

            createResult.onSuccess { laporan ->
                // Upload setiap bukti yang tidak di-exclude
                val activeBukti = state.uploadedBuktiList.filter { !it.isExcluded && it.fileBytes != null }
                for (bukti in activeBukti) {
                    siagaRepo.uploadBukti(
                        laporanId = laporan.id,
                        fileName = bukti.fileName,
                        fileBytes = bukti.fileBytes!!,
                        tipeFile = bukti.fileType
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    submitSuccessNomorLaporan = laporan.nomorLaporan
                )

                // Refresh riwayat
                loadLaporanSaya()
                onSuccess(laporan.nomorLaporan)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = err.message ?: "Gagal mengirim laporan. Silakan coba lagi."
                )
            }
        }
    }

    /**
     * Memuat detail laporan beserta timeline penanganan dari backend
     */
    fun loadDetailLaporan(laporanId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val detailResult = siagaRepo.getDetailLaporan(laporanId)
            val timelineResult = siagaRepo.getTimelineLaporan(laporanId)
            val buktiResult = siagaRepo.getBuktiLaporan(laporanId)
            val permintaanResult = siagaRepo.getPermintaanInformasi(laporanId)
            val hasilResult = siagaRepo.getDokumenHasil(laporanId)

            val detail = detailResult.getOrNull()
            val timeline = timelineResult.getOrDefault(emptyList())
            val bukti = buktiResult.getOrDefault(emptyList())
            val permintaans = permintaanResult.getOrDefault(emptyList())
            val hasils = hasilResult.getOrDefault(emptyList())

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                detailLaporan = detail,
                timelineList = timeline,
                buktiList = bukti,
                permintaanInfoList = permintaans,
                dokumenHasilList = hasils
            )
        }
    }

    /**
     * Mengirimkan balasan informasi tambahan dari mahasiswa kepada admin penanganan
     */
    fun kirimBalasanInformasi(
        permintaanId: String,
        balasan: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)
            val result = siagaRepo.balasPermintaanInformasi(permintaanId, balasan)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isSubmitting = false)
                // Muat ulang detail laporan
                _uiState.value.detailLaporan?.let { loadDetailLaporan(it.id) }
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = err.message ?: "Gagal mengirim balasan informasi."
                )
            }
        }
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> String.format("%.1f MB", bytes.toDouble() / (1024 * 1024))
            bytes >= 1024 -> String.format("%d KB", bytes / 1024)
            else -> "$bytes B"
        }
    }
}

