package com.PPBM.lentera.features.mahasiswa.magang

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.PPBM.lentera.backend.mahasiswa.model.*
import com.PPBM.lentera.backend.mahasiswa.repository.MagangRepository
import com.PPBM.lentera.backend.mahasiswa.repository.MahasiswaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MagangUiState(
    val isLoading: Boolean = false,
    val lowonganList: List<LowonganTerbit> = emptyList(),
    val lamaranSayaList: List<LamaranSaya> = emptyList(),
    val bookmarkedIds: Set<String> = emptySet(),
    val selectedLowongan: LowonganTerbit? = null,
    val selectedLamaran: LamaranSaya? = null,
    val riwayatStatusList: List<LamaranRiwayatItem> = emptyList(),
    val dokumenLamaranList: List<DokumenLamaranItem> = emptyList(),
    val isSubmitting: Boolean = false,
    val submitSuccessNomorLamaran: String? = null,
    val errorMessage: String? = null
)

class MagangViewModel(
    private val magangRepo: MagangRepository = MagangRepository(),
    private val mahasiswaRepo: MahasiswaRepository = MahasiswaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MagangUiState())
    val uiState: StateFlow<MagangUiState> = _uiState.asStateFlow()

    init {
        loadMagangData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Memuat daftar lowongan terbit, lamaran mahasiswa, dan daftar bookmark
     */
    fun loadMagangData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val lowonganRes = magangRepo.getLowonganTerbit()
            val lamaranRes = magangRepo.getLamaranSaya()
            val bookmarkRes = magangRepo.getBookmarkedLowonganIds()

            val firstError = lowonganRes.exceptionOrNull()?.message
                ?: lamaranRes.exceptionOrNull()?.message

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                lowonganList = lowonganRes.getOrDefault(_uiState.value.lowonganList),
                lamaranSayaList = lamaranRes.getOrDefault(_uiState.value.lamaranSayaList),
                bookmarkedIds = bookmarkRes.getOrDefault(_uiState.value.bookmarkedIds),
                errorMessage = firstError
            )
        }
    }

    /**
     * Menyimpan atau membatalkan bookmark lowongan
     */
    fun toggleBookmark(lowonganId: String) {
        viewModelScope.launch {
            val result = magangRepo.toggleBookmark(lowonganId)
            result.onSuccess { isBookmarked ->
                val current = _uiState.value.bookmarkedIds.toMutableSet()
                if (isBookmarked) {
                    current.add(lowonganId)
                } else {
                    current.remove(lowonganId)
                }
                _uiState.value = _uiState.value.copy(bookmarkedIds = current)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = err.message ?: "Gagal memperbarui bookmark."
                )
            }
        }
    }

    /**
     * Memuat rincian satu lowongan
     */
    fun loadDetailLowongan(lowonganId: String) {
        viewModelScope.launch {
            val fromCache = _uiState.value.lowonganList.find { it.id == lowonganId }
            if (fromCache != null) {
                _uiState.value = _uiState.value.copy(selectedLowongan = fromCache, errorMessage = null)
            } else {
                val res = magangRepo.getDetailLowongan(lowonganId)
                res.onSuccess {
                    _uiState.value = _uiState.value.copy(selectedLowongan = it, errorMessage = null)
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = err.message ?: "Gagal memuat detail lowongan."
                    )
                }
            }
        }
    }

    /**
     * Memilih lamaran untuk dilihat detailnya
     */
    fun selectLamaran(lamaran: LamaranSaya) {
        _uiState.value = _uiState.value.copy(selectedLamaran = lamaran, errorMessage = null)
        loadDetailLamaran(lamaran.id)
    }

    /**
     * Memuat rincian satu lamaran magang beserta riwayat status & berkas
     */
    fun loadDetailLamaran(lamaranId: String) {
        if (lamaranId.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val lamaranRes = magangRepo.getDetailLamaran(lamaranId)
            val riwayatRes = magangRepo.getRiwayatLamaran(lamaranId)
            val dokumenRes = magangRepo.getDokumenLamaran(lamaranId)

            val loadedLamaran = lamaranRes.getOrNull()
                ?: _uiState.value.selectedLamaran?.takeIf { it.id == lamaranId }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                selectedLamaran = loadedLamaran,
                riwayatStatusList = riwayatRes.getOrDefault(emptyList()),
                dokumenLamaranList = dokumenRes.getOrDefault(emptyList()),
                errorMessage = lamaranRes.exceptionOrNull()?.message
                    ?: riwayatRes.exceptionOrNull()?.message
                    ?: dokumenRes.exceptionOrNull()?.message
            )
        }
    }

    /**
     * Mengajukan pendaftaran lamaran magang baru beserta upload dokumen lokal & dokumen arsip profil.
     */
    fun submitLamaran(
        payload: SubmitLamaranPayload,
        dokumenFiles: List<Triple<String, String, ByteArray>>, // jenis, fileName, bytes
        profileDocPaths: List<Triple<String, String, String>> = emptyList(), // jenis, fileName, storagePath
        onError: (String) -> Unit = {},
        onSuccess: (nomorLamaran: String) -> Unit
    ) {
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)

            // Unduh lebih dulu dokumen yang dipilih dari arsip profil agar gagal sebelum baris lamaran tercipta
            val allFiles = dokumenFiles.toMutableList()
            for ((jenis, fileName, storagePath) in profileDocPaths) {
                val downloadRes = mahasiswaRepo.downloadDokumenBytes(storagePath)
                if (downloadRes.isFailure) {
                    val msg = downloadRes.exceptionOrNull()?.message
                        ?: "Gagal menyalin dokumen arsip ($fileName)."
                    _uiState.value = _uiState.value.copy(isSubmitting = false, errorMessage = msg)
                    onError(msg)
                    return@launch
                }
                allFiles.add(Triple(jenis, fileName, downloadRes.getOrThrow()))
            }

            val result = magangRepo.submitLamaran(payload)
            result.onSuccess { lamaran ->
                val uploadFailures = mutableListOf<String>()
                for ((jenis, fileName, bytes) in allFiles) {
                    val uploadRes = magangRepo.uploadDokumenLamaran(
                        lamaranId = lamaran.id,
                        jenis = jenis,
                        fileName = fileName,
                        fileBytes = bytes
                    )
                    if (uploadRes.isFailure) {
                        uploadFailures.add("$jenis (${uploadRes.exceptionOrNull()?.message ?: "gagal"})")
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    submitSuccessNomorLamaran = lamaran.nomorLamaran
                )
                loadMagangData()

                if (uploadFailures.isNotEmpty()) {
                    val warn = "Lamaran ${lamaran.nomorLamaran} tersimpan, namun sebagian berkas gagal diunggah: ${uploadFailures.joinToString("; ")}"
                    _uiState.value = _uiState.value.copy(errorMessage = warn)
                    onError(warn)
                } else {
                    onSuccess(lamaran.nomorLamaran)
                }
            }.onFailure { err ->
                val msg = err.message ?: "Gagal mengajukan lamaran magang."
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    /**
     * Mengunggah perbaikan/revisi berkas dokumen lamaran
     */
    fun uploadPerbaikanDokumen(
        lamaranId: String,
        jenis: String,
        fileName: String,
        fileBytes: ByteArray,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit
    ) {
        if (_uiState.value.isSubmitting) return
        if (lamaranId.isBlank()) {
            val msg = "ID lamaran tidak ditemukan."
            _uiState.value = _uiState.value.copy(errorMessage = msg)
            onError(msg)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)
            val result = magangRepo.uploadDokumenLamaran(lamaranId, jenis, fileName, fileBytes)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isSubmitting = false, errorMessage = null)
                loadDetailLamaran(lamaranId)
                loadMagangData()
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal mengunggah berkas perbaikan."
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    suspend fun getSignedUrl(bucketName: String, filePath: String): Result<String> {
        return magangRepo.createSignedUrl(bucketName, filePath)
    }
}

