package com.PPBM.lentera.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.PPBM.lentera.backend.admin.model.AdminDashboardStats
import com.PPBM.lentera.backend.admin.model.AdminLaporan
import com.PPBM.lentera.backend.admin.model.LowonganEntity
import com.PPBM.lentera.backend.admin.model.TempatMagangEntity
import com.PPBM.lentera.backend.admin.repository.AdminMagangRepository
import com.PPBM.lentera.backend.admin.repository.AdminMahasiswaRepository
import com.PPBM.lentera.backend.admin.repository.AdminSiagaRepository
import com.PPBM.lentera.backend.auth.model.UserProfile
import com.PPBM.lentera.backend.mahasiswa.model.LamaranSaya
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
    val semuaLaporanList: List<AdminLaporan> = emptyList(),
    val selectedLaporan: AdminLaporan? = null,
    val mahasiswaList: List<UserProfile> = emptyList(),
    val selectedMahasiswa: UserProfile? = null,
    val tempatMagangList: List<TempatMagangEntity> = emptyList(),
    val lowonganAdminList: List<LowonganEntity> = emptyList(),
    val lamaranMasukList: List<LamaranSaya> = emptyList(),
    val selectedTempatMagang: TempatMagangEntity? = null,
    val selectedLowonganAdmin: LowonganEntity? = null,
    val isCreatingMahasiswa: Boolean = false,
    val isUpdatingLaporan: Boolean = false,
    val isSubmittingMagang: Boolean = false,
    val errorMessage: String? = null,
    val actionSuccessMessage: String? = null
)

class AdminViewModel(
    private val adminMhsRepo: AdminMahasiswaRepository = AdminMahasiswaRepository(),
    private val adminSiagaRepo: AdminSiagaRepository = AdminSiagaRepository(),
    private val adminMagangRepo: AdminMagangRepository = AdminMagangRepository()
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
            val laporanList = laporanResult.getOrDefault(emptyList())

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                stats = statsResult.getOrDefault(AdminDashboardStats()),
                semuaLaporanList = laporanList,
                laporanTerbaruList = laporanList.take(5)
            )
        }
    }

    /**
     * Memuat seluruh daftar laporan pengaduan admin
     */
    fun loadSemuaLaporan() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = adminSiagaRepo.getAllLaporan()
            result.onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    semuaLaporanList = list,
                    laporanTerbaruList = list.take(5)
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Gagal memuat daftar laporan"
                )
            }
        }
    }

    /**
     * Memilih salah satu laporan untuk dilihat detailnya
     */
    fun selectLaporan(idOrNomor: String) {
        val found = _uiState.value.semuaLaporanList.find { it.id == idOrNomor || it.nomorLaporan == idOrNomor }
        if (found != null) {
            _uiState.value = _uiState.value.copy(selectedLaporan = found)
        } else {
            viewModelScope.launch {
                val res = adminSiagaRepo.getDetailLaporan(idOrNomor)
                res.onSuccess {
                    _uiState.value = _uiState.value.copy(selectedLaporan = it)
                }
            }
        }
    }

    /**
     * Memperbarui status / verifikasi laporan (misal: penugasan atau penolakan)
     */
    fun updateStatusLaporan(
        laporanId: String,
        status: String,
        unitPenanganan: String? = null,
        jalurTindakLanjut: String? = null,
        catatanPenugasan: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingLaporan = true)
            val res = adminSiagaRepo.updateStatusLaporan(
                laporanId = laporanId,
                status = status,
                unitPenanganan = unitPenanganan,
                jalurTindakLanjut = jalurTindakLanjut,
                catatanPenugasan = catatanPenugasan
            )
            res.onSuccess {
                _uiState.value = _uiState.value.copy(isUpdatingLaporan = false)
                loadSemuaLaporan()
                selectLaporan(laporanId)
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isUpdatingLaporan = false,
                    errorMessage = err.message ?: "Gagal memperbarui status laporan"
                )
            }
        }
    }

    /**
     * Menambahkan catatan tindakan penanganan kasus
     */
    fun addCatatanTindakan(
        laporanId: String,
        judul: String,
        deskripsi: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingLaporan = true)
            val res = adminSiagaRepo.addLogTindakan(laporanId, judul, deskripsi)
            res.onSuccess {
                _uiState.value = _uiState.value.copy(isUpdatingLaporan = false)
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isUpdatingLaporan = false,
                    errorMessage = err.message ?: "Gagal menyimpan catatan tindakan"
                )
            }
        }
    }

    /**
     * Menyelesaikan laporan pengaduan
     */
    fun selesaikanLaporan(
        laporanId: String,
        hasilPenanganan: String,
        hasilDipublikasi: Boolean,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingLaporan = true)
            val res = adminSiagaRepo.selesaikanLaporan(laporanId, hasilPenanganan, hasilDipublikasi)
            res.onSuccess {
                _uiState.value = _uiState.value.copy(isUpdatingLaporan = false)
                loadSemuaLaporan()
                loadDashboardData()
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isUpdatingLaporan = false,
                    errorMessage = err.message ?: "Gagal menyelesaikan laporan"
                )
            }
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

    // ========================================================================
    // Operasi Admin Magang
    // ========================================================================

    /**
     * Memuat seluruh daftar tempat magang / mitra
     */
    fun loadTempatMagangList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val res = adminMagangRepo.getTempatMagangList()
            res.onSuccess { list ->
                _uiState.value = _uiState.value.copy(isLoading = false, tempatMagangList = list)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Memuat seluruh daftar lowongan magang
     */
    fun loadLowonganAdminList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val res = adminMagangRepo.getLowonganList()
            res.onSuccess { list ->
                _uiState.value = _uiState.value.copy(isLoading = false, lowonganAdminList = list)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Memuat seluruh daftar lamaran masuk dari mahasiswa
     */
    fun loadLamaranMasukList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val res = adminMagangRepo.getAllLamaranMasuk()
            res.onSuccess { list ->
                _uiState.value = _uiState.value.copy(isLoading = false, lamaranMasukList = list)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Menambah tempat magang baru
     */
    fun tambahTempatMagang(entity: TempatMagangEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingMagang = true)
            val res = adminMagangRepo.createTempatMagang(entity)
            res.onSuccess {
                _uiState.value = _uiState.value.copy(isSubmittingMagang = false)
                loadTempatMagangList()
                loadDashboardData()
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isSubmittingMagang = false,
                    errorMessage = err.message ?: "Gagal menambah tempat magang"
                )
            }
        }
    }

    /**
     * Menambah lowongan magang baru
     */
    fun tambahLowongan(entity: LowonganEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingMagang = true)
            val res = adminMagangRepo.createLowongan(entity)
            res.onSuccess {
                _uiState.value = _uiState.value.copy(isSubmittingMagang = false)
                loadLowonganAdminList()
                loadDashboardData()
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isSubmittingMagang = false,
                    errorMessage = err.message ?: "Gagal membuat lowongan magang"
                )
            }
        }
    }

    /**
     * Memvalidasi berkas lamaran mahasiswa
     */
    fun validasiLamaranMahasiswa(
        lamaranId: String,
        status: String,
        catatanAdmin: String? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingMagang = true)
            val res = adminMagangRepo.validasiLamaran(lamaranId, status, catatanAdmin)
            res.onSuccess {
                _uiState.value = _uiState.value.copy(isSubmittingMagang = false)
                loadLamaranMasukList()
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isSubmittingMagang = false,
                    errorMessage = err.message ?: "Gagal memvalidasi berkas lamaran"
                )
            }
        }
    }
}

