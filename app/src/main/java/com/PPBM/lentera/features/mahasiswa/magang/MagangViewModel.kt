package com.PPBM.lentera.features.mahasiswa.magang

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.PPBM.lentera.backend.mahasiswa.model.*
import com.PPBM.lentera.backend.mahasiswa.repository.MagangRepository
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
    private val magangRepo: MagangRepository = MagangRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MagangUiState())
    val uiState: StateFlow<MagangUiState> = _uiState.asStateFlow()

    init {
        loadMagangData()
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

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                lowonganList = lowonganRes.getOrDefault(emptyList()),
                lamaranSayaList = lamaranRes.getOrDefault(emptyList()),
                bookmarkedIds = bookmarkRes.getOrDefault(emptySet())
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
                _uiState.value = _uiState.value.copy(selectedLowongan = fromCache)
            } else {
                val res = magangRepo.getDetailLowongan(lowonganId)
                res.onSuccess {
                    _uiState.value = _uiState.value.copy(selectedLowongan = it)
                }
            }
        }
    }

    /**
     * Memuat rincian satu lamaran magang beserta riwayat status & berkas
     */
    fun loadDetailLamaran(lamaranId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val lamaranRes = magangRepo.getDetailLamaran(lamaranId)
            val riwayatRes = magangRepo.getRiwayatLamaran(lamaranId)
            val dokumenRes = magangRepo.getDokumenLamaran(lamaranId)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                selectedLamaran = lamaranRes.getOrNull(),
                riwayatStatusList = riwayatRes.getOrDefault(emptyList()),
                dokumenLamaranList = dokumenRes.getOrDefault(emptyList())
            )
        }
    }

    /**
     * Mengajukan pendaftaran lamaran magang baru beserta upload dokumen
     */
    fun submitLamaran(
        payload: SubmitLamaranPayload,
        dokumenFiles: List<Triple<String, String, ByteArray>>, // jenis, fileName, bytes
        onSuccess: (nomorLamaran: String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)

            val result = magangRepo.submitLamaran(payload)
            result.onSuccess { lamaran ->
                // Upload berkas-berkas lamaran ke Storage bucket dokumen-magang
                for ((jenis, fileName, bytes) in dokumenFiles) {
                    magangRepo.uploadDokumenLamaran(
                        lamaranId = lamaran.id,
                        jenis = jenis,
                        fileName = fileName,
                        fileBytes = bytes
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    submitSuccessNomorLamaran = lamaran.nomorLamaran
                )

                loadMagangData()
                onSuccess(lamaran.nomorLamaran)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = err.message ?: "Gagal mengajukan lamaran magang"
                )
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
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)
            val result = magangRepo.uploadDokumenLamaran(lamaranId, jenis, fileName, fileBytes)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isSubmitting = false)
                loadDetailLamaran(lamaranId)
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = err.message ?: "Gagal mengunggah berkas perbaikan"
                )
            }
        }
    }
}

