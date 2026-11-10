package com.PPBM.lentera.features.mahasiswa.siaga

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.PPBM.lentera.backend.core.extractShortFileType
import com.PPBM.lentera.backend.mahasiswa.model.BuktiLaporanItem
import com.PPBM.lentera.backend.mahasiswa.model.DokumenHasilItem
import com.PPBM.lentera.backend.mahasiswa.model.KategoriLaporan
import com.PPBM.lentera.backend.mahasiswa.model.LaporanSaya
import com.PPBM.lentera.backend.mahasiswa.model.PermintaanInformasiItem
import com.PPBM.lentera.backend.mahasiswa.model.TimelineLaporanSaya
import com.PPBM.lentera.backend.mahasiswa.repository.SiagaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

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
    val kategoriList: List<KategoriLaporan> = emptyList(),
    val errorMessage: String? = null,

    // Form pembuatan laporan
    val selectedKategoriId: String = "",
    val selectedKategoriNama: String = "",
    val formJudul: String = "",
    val formTanggalWaktu: String = "",
    val formLokasi: String = "",
    val formKronologi: String = "",
    val formIsAnonim: Boolean = false,
    val uploadedBuktiList: List<SelectedBukti> = emptyList(),

    // Submit state
    val isSubmitting: Boolean = false,
    val submitSuccessNomorLaporan: String? = null,
    val submitSuccessLaporanId: String? = null,

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
        loadKategoriList()
        loadLaporanSaya()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun loadKategoriList() {
        viewModelScope.launch {
            siagaRepo.getKategoriList().onSuccess { list ->
                val currentNama = _uiState.value.selectedKategoriNama
                val matched = if (currentNama.isNotBlank()) {
                    list.find { it.nama.equals(currentNama, ignoreCase = true) }
                } else null
                _uiState.value = _uiState.value.copy(
                    kategoriList = list,
                    selectedKategoriId = matched?.id ?: _uiState.value.selectedKategoriId,
                    selectedKategoriNama = matched?.nama ?: currentNama
                )
            }
        }
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
     * Memilih kategori laporan berdasarkan ID dan nama.
     */
    fun setKategori(kategoriId: String, kategoriNama: String) {
        _uiState.value = _uiState.value.copy(
            selectedKategoriId = kategoriId,
            selectedKategoriNama = kategoriNama,
            formJudul = if (_uiState.value.formJudul.isBlank()) kategoriNama else _uiState.value.formJudul
        )
    }

    /**
     * Memilih kategori berdasarkan nama tampilan enum UI lalu mencocokkannya dengan UUID database.
     */
    fun setKategoriByName(kategoriNama: String) {
        val clean = kategoriNama.trim()
        if (clean.isBlank()) {
            _uiState.value = _uiState.value.copy(
                selectedKategoriId = "",
                selectedKategoriNama = ""
            )
            return
        }
        val list = _uiState.value.kategoriList
        val matched = list.find { it.nama.equals(clean, ignoreCase = true) }
            ?: list.find { it.nama.contains(clean, ignoreCase = true) }
        _uiState.value = _uiState.value.copy(
            selectedKategoriId = matched?.id.orEmpty(),
            selectedKategoriNama = matched?.nama ?: clean,
            formJudul = if (_uiState.value.formJudul.isBlank()) (matched?.nama ?: clean) else _uiState.value.formJudul
        )
        if (list.isEmpty()) {
            loadKategoriList()
        }
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
     * Menambahkan bukti ke antrean upload dengan validasi ukuran (maks 20 MB).
     */
    fun addBuktiFile(
        fileName: String,
        fileType: String,
        fileBytes: ByteArray,
        uri: Uri? = null,
        onError: (String) -> Unit = {}
    ) {
        if (fileBytes.isEmpty()) {
            onError("Berkas '$fileName' kosong atau gagal dibaca.")
            return
        }
        if (fileBytes.size.toLong() > 20L * 1024L * 1024L) {
            onError("Ukuran berkas '$fileName' melebihi batas maksimal 20 MB.")
            return
        }
        val sizeFormatted = formatFileSize(fileBytes.size.toLong())
        val shortType = extractShortFileType(fileName, fileType)
        val newBukti = SelectedBukti(
            uri = uri,
            fileName = fileName,
            fileType = shortType,
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

    fun resetDraftForm() {
        _uiState.value = _uiState.value.copy(
            selectedKategoriId = "",
            selectedKategoriNama = "",
            formJudul = "",
            formTanggalWaktu = "",
            formLokasi = "",
            formKronologi = "",
            formIsAnonim = false,
            uploadedBuktiList = emptyList()
        )
    }

    /**
     * Mengirimkan laporan lengkap ke backend Supabase beserta berkas buktinya
     */
    fun submitLaporan(
        onError: (String) -> Unit = {},
        onUploadWarning: (String) -> Unit = {},
        onSuccess: (nomorLaporan: String) -> Unit
    ) {
        if (_uiState.value.isSubmitting) return

        viewModelScope.launch {
            val state = _uiState.value
            if (state.selectedKategoriNama.isBlank()) {
                val msg = "Kategori laporan wajib dipilih terlebih dahulu."
                _uiState.value = state.copy(errorMessage = msg)
                onError(msg)
                return@launch
            }
            if (state.formTanggalWaktu.isBlank() || state.formLokasi.isBlank() || state.formKronologi.isBlank()) {
                val msg = "Tanggal kejadian, lokasi, dan kronologi wajib diisi."
                _uiState.value = state.copy(errorMessage = msg)
                onError(msg)
                return@launch
            }

            _uiState.value = state.copy(isSubmitting = true, errorMessage = null)

            var resolvedKategoriId = state.selectedKategoriId
            var resolvedKategoriNama = state.selectedKategoriNama
            if (resolvedKategoriId.isBlank()) {
                val katList = siagaRepo.getKategoriList().getOrDefault(emptyList())
                val matched = katList.find { it.nama.equals(resolvedKategoriNama, ignoreCase = true) }
                    ?: katList.find { it.nama.contains(resolvedKategoriNama, ignoreCase = true) }
                if (matched != null) {
                    resolvedKategoriId = matched.id
                    resolvedKategoriNama = matched.nama
                    _uiState.value = _uiState.value.copy(
                        kategoriList = katList,
                        selectedKategoriId = resolvedKategoriId,
                        selectedKategoriNama = resolvedKategoriNama
                    )
                }
            }

            if (resolvedKategoriId.isBlank()) {
                val msg = "Kategori laporan tidak valid atau gagal dimuat."
                _uiState.value = _uiState.value.copy(isSubmitting = false, errorMessage = msg)
                onError(msg)
                return@launch
            }

            val createResult = siagaRepo.createLaporan(
                kategoriId = resolvedKategoriId,
                judul = state.formJudul.ifBlank { resolvedKategoriNama },
                kronologi = state.formKronologi,
                lokasi = state.formLokasi,
                tanggalKejadian = state.formTanggalWaktu,
                isAnonim = state.formIsAnonim,
                kategoriNama = resolvedKategoriNama
            )

            createResult.onSuccess { laporan ->
                val activeBukti = state.uploadedBuktiList.filter { !it.isExcluded && it.fileBytes != null }
                val failedUploads = mutableListOf<String>()
                for (bukti in activeBukti) {
                    val uploadRes = siagaRepo.uploadBukti(
                        laporanId = laporan.id,
                        fileName = bukti.fileName,
                        fileBytes = bukti.fileBytes!!,
                        tipeFile = bukti.fileType
                    )
                    uploadRes.onFailure {
                        failedUploads.add("${bukti.fileName} (${it.message ?: "gagal"})")
                    }
                }

                val warningMsg = if (failedUploads.isNotEmpty()) {
                    "Laporan terkirim (${laporan.nomorLaporan}), namun sebagian bukti gagal diunggah: ${failedUploads.joinToString()}"
                } else {
                    null
                }

                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    submitSuccessNomorLaporan = laporan.nomorLaporan,
                    submitSuccessLaporanId = laporan.id,
                    detailLaporan = laporan,
                    errorMessage = warningMsg
                )

                resetDraftForm()
                loadLaporanSaya()
                if (warningMsg != null) {
                    onUploadWarning(warningMsg)
                }
                onSuccess(laporan.nomorLaporan)
            }.onFailure { err ->
                val msg = err.message ?: "Gagal mengirim laporan. Silakan coba lagi."
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    /**
     * Memuat detail laporan beserta timeline penanganan dari backend
     */
    fun loadDetailLaporan(laporanId: String) {
        if (laporanId.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val detailResult = siagaRepo.getDetailLaporan(laporanId)
            val timelineResult = siagaRepo.getTimelineLaporan(laporanId)
            val buktiResult = siagaRepo.getBuktiLaporan(laporanId)
            val permintaanResult = siagaRepo.getPermintaanInformasi(laporanId)
            val hasilResult = siagaRepo.getDokumenHasil(laporanId)

            val detail = detailResult.getOrNull()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                detailLaporan = detail,
                timelineList = timelineResult.getOrDefault(emptyList()),
                buktiList = buktiResult.getOrDefault(emptyList()),
                permintaanInfoList = permintaanResult.getOrDefault(emptyList()),
                dokumenHasilList = hasilResult.getOrDefault(emptyList()),
                errorMessage = detailResult.exceptionOrNull()?.message
            )
        }
    }

    /**
     * Mengirimkan balasan informasi tambahan dari mahasiswa kepada admin penanganan
     */
    fun kirimBalasanInformasi(
        permintaanId: String,
        balasan: String,
        attachmentFileName: String? = null,
        attachmentBytes: ByteArray? = null,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit
    ) {
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)
            val result = siagaRepo.balasPermintaanInformasi(
                permintaanId = permintaanId,
                balasan = balasan,
                laporanId = _uiState.value.detailLaporan?.id,
                attachmentFileName = attachmentFileName,
                attachmentBytes = attachmentBytes
            )
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isSubmitting = false)
                _uiState.value.detailLaporan?.let { loadDetailLaporan(it.id) }
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal mengirim balasan informasi."
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    suspend fun getBuktiSignedUrl(filePath: String): Result<String> {
        return siagaRepo.createSignedUrl("bukti-laporan", filePath)
    }

    suspend fun getDokumenHasilSignedUrl(filePath: String): Result<String> {
        return siagaRepo.createSignedUrl("dokumen-hasil", filePath)
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB", bytes.toDouble() / (1024 * 1024))
            bytes >= 1024 -> String.format(Locale.US, "%d KB", bytes / 1024)
            else -> "$bytes B"
        }
    }
}

