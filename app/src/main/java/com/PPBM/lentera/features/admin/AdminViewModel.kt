package com.PPBM.lentera.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.PPBM.lentera.backend.admin.model.AdminDashboardStats
import com.PPBM.lentera.backend.admin.model.AdminLaporan
import com.PPBM.lentera.backend.admin.repository.AdminMahasiswaRepository
import com.PPBM.lentera.backend.admin.repository.AdminSiagaRepository
import com.PPBM.lentera.backend.auth.model.UserProfile
import com.PPBM.lentera.features.admin.akunmahasiswa.Mahasiswa
import com.PPBM.lentera.features.admin.akunmahasiswa.StatusAkun
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val isLoading: Boolean = false,
    val stats: AdminDashboardStats = AdminDashboardStats(),
    val laporanTerbaruList: List<AdminLaporan> = emptyList(),
    val mahasiswaList: List<UserProfile> = emptyList(),
    val selectedMahasiswa: UserProfile? = null,
    val isCreatingMahasiswa: Boolean = false,
    val errorMessage: String? = null,
    val actionSuccessMessage: String? = null
)

class AdminViewModel(
    private val adminMhsRepo: AdminMahasiswaRepository = AdminMahasiswaRepository(),
    private val adminSiagaRepo: AdminSiagaRepository = AdminSiagaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    /**
     * Memuat data statistik dashboard dan laporan terkini
     */
    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val statsResult = adminMhsRepo.getDashboardStats()
            val laporanResult = adminSiagaRepo.getAllLaporan()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                stats = statsResult.getOrDefault(AdminDashboardStats()),
                laporanTerbaruList = laporanResult.getOrDefault(emptyList()).take(5)
            )
        }
    }

    /**
     * Memuat seluruh daftar mahasiswa terdaftar
     */
    fun loadMahasiswaList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = adminMhsRepo.getAllMahasiswa()
            result.onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    mahasiswaList = list
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Gagal memuat daftar mahasiswa"
                )
            }
        }
    }

    /**
     * Memilih salah satu mahasiswa untuk dilihat detailnya
     */
    fun selectMahasiswa(idOrNim: String) {
        val found = _uiState.value.mahasiswaList.find { it.id == idOrNim || it.nim == idOrNim }
        _uiState.value = _uiState.value.copy(selectedMahasiswa = found)
    }

    /**
     * Membuat akun mahasiswa baru oleh Admin via RPC
     */
    fun buatAkunMahasiswa(
        namaLengkap: String,
        nim: String,
        email: String,
        jurusan: String?,
        programStudi: String?,
        angkatan: String?,
        noTelepon: String? = null,
        passwordDefault: String = "Password123!",
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingMahasiswa = true, errorMessage = null)
            val result = adminMhsRepo.createMahasiswa(
                email = email,
                password = passwordDefault,
                namaLengkap = namaLengkap,
                nim = nim,
                noTelepon = noTelepon,
                perguruanTinggi = "Politeknik Negeri Samarinda",
                jurusan = jurusan,
                programStudi = programStudi,
                angkatan = angkatan
            )

            result.onSuccess { newUserId ->
                _uiState.value = _uiState.value.copy(
                    isCreatingMahasiswa = false,
                    actionSuccessMessage = "Akun mahasiswa $namaLengkap berhasil dibuat"
                )
                loadMahasiswaList()
                loadDashboardData()
                onSuccess(newUserId)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isCreatingMahasiswa = false,
                    errorMessage = err.message ?: "Gagal membuat akun mahasiswa"
                )
            }
        }
    }

    /**
     * Mengubah status aktif / nonaktif mahasiswa
     */
    fun toggleStatusMahasiswa(userId: String, newStatus: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val result = adminMhsRepo.toggleStatusAkun(userId, newStatus)
            result.onSuccess {
                loadMahasiswaList()
                onComplete()
            }
        }
    }
}

