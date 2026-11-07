package com.PPBM.lentera.features.mahasiswa.profil

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.PPBM.lentera.backend.auth.model.UserProfile
import com.PPBM.lentera.backend.auth.repository.AuthRepository
import com.PPBM.lentera.backend.core.SessionManager
import com.PPBM.lentera.backend.mahasiswa.model.DokumenMahasiswaItem
import com.PPBM.lentera.backend.mahasiswa.model.NotifikasiItem
import com.PPBM.lentera.backend.mahasiswa.repository.MahasiswaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfilUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val dokumenList: List<DokumenMahasiswaItem> = emptyList(),
    val notifikasiList: List<NotifikasiItem> = emptyList(),
    val errorMessage: String? = null,
    val isUpdateSuccess: Boolean = false
)

class ProfilViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager.getInstance(application)
    private val mahasiswaRepo = MahasiswaRepository()
    private val authRepo = AuthRepository(application)

    private val _uiState = MutableStateFlow(
        ProfilUiState(profile = sessionManager.getProfile())
    )
    val uiState: StateFlow<ProfilUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Ambil profile terbaru dari server
            val profileResult = mahasiswaRepo.getProfilSaya()
            profileResult.onSuccess { updatedProfile ->
                sessionManager.saveSession(updatedProfile)
                _uiState.value = _uiState.value.copy(profile = updatedProfile)
            }

            // Ambil daftar dokumen
            val docResult = mahasiswaRepo.getDokumenMahasiswa()
            val docs = docResult.getOrDefault(emptyList())

            // Ambil daftar notifikasi
            val notifResult = mahasiswaRepo.getNotifikasi()
            val notifs = notifResult.getOrDefault(emptyList())

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                dokumenList = docs,
                notifikasiList = notifs
            )
        }
    }

    fun updateProfil(
        namaLengkap: String,
        noTelepon: String?,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isUpdateSuccess = false)
            val current = _uiState.value.profile
            val result = mahasiswaRepo.updateProfil(
                namaLengkap = namaLengkap,
                noTelepon = noTelepon,
                perguruanTinggi = current?.perguruanTinggi,
                jurusan = current?.jurusan,
                programStudi = current?.programStudi,
                angkatan = current?.angkatan
            )

            result.onSuccess { newProfile ->
                sessionManager.saveSession(newProfile)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = newProfile,
                    isUpdateSuccess = true
                )
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Gagal memperbarui profil"
                )
            }
        }
    }

    fun uploadDokumen(
        jenis: String,
        fileName: String,
        fileBytes: ByteArray,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = mahasiswaRepo.uploadDokumenMahasiswa(
                jenis = jenis,
                fileName = fileName,
                fileBytes = fileBytes
            )
            result.onSuccess { newDoc ->
                val currentDocs = _uiState.value.dokumenList.toMutableList()
                currentDocs.add(0, newDoc)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    dokumenList = currentDocs
                )
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Gagal mengunggah dokumen"
                )
            }
        }
    }

    fun deleteDokumen(docId: String, filePath: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = mahasiswaRepo.deleteDokumenMahasiswa(docId, filePath)
            result.onSuccess {
                val updated = _uiState.value.dokumenList.filter { it.id != docId }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    dokumenList = updated
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Gagal menghapus dokumen"
                )
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepo.logout()
            onSuccess()
        }
    }
}

