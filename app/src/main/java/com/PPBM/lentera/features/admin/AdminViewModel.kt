package com.PPBM.lentera.features.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.PPBM.lentera.backend.admin.model.AdminDashboardStats
import com.PPBM.lentera.backend.admin.model.AdminLaporan
import com.PPBM.lentera.backend.admin.model.DokumenTempatMagangItem
import com.PPBM.lentera.backend.admin.model.LogTindakanItem
import com.PPBM.lentera.backend.admin.model.LowonganEntity
import com.PPBM.lentera.backend.admin.model.TempatMagangEntity
import com.PPBM.lentera.backend.admin.repository.AdminMagangRepository
import com.PPBM.lentera.backend.admin.repository.AdminMahasiswaRepository
import com.PPBM.lentera.backend.admin.repository.AdminSiagaRepository
import com.PPBM.lentera.backend.auth.model.UserProfile
import com.PPBM.lentera.backend.auth.repository.AuthRepository
import com.PPBM.lentera.backend.core.SessionManager
import com.PPBM.lentera.backend.core.extractShortFileType
import com.PPBM.lentera.backend.core.normalizeLowonganStatus
import com.PPBM.lentera.backend.core.normalizeTempatStatus
import com.PPBM.lentera.backend.mahasiswa.model.BuktiLaporanItem
import com.PPBM.lentera.backend.mahasiswa.model.DokumenHasilItem
import com.PPBM.lentera.backend.mahasiswa.model.DokumenLamaranItem
import com.PPBM.lentera.backend.mahasiswa.model.LamaranSaya
import com.PPBM.lentera.backend.mahasiswa.model.PermintaanInformasiItem
import com.PPBM.lentera.features.admin.magang.PelamarLamaranItem
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
    val selectedLaporanBukti: List<BuktiLaporanItem> = emptyList(),
    val selectedLaporanLogs: List<LogTindakanItem> = emptyList(),
    val selectedLaporanPermintaan: List<PermintaanInformasiItem> = emptyList(),
    val selectedLaporanDokumenHasil: List<DokumenHasilItem> = emptyList(),
    val mahasiswaList: List<UserProfile> = emptyList(),
    val selectedMahasiswa: UserProfile? = null,
    val tempatMagangList: List<TempatMagangEntity> = emptyList(),
    val selectedTempatDokumenList: List<DokumenTempatMagangItem> = emptyList(),
    val lowonganAdminList: List<LowonganEntity> = emptyList(),
    val lamaranMasukList: List<LamaranSaya> = emptyList(),
    val selectedPelamar: PelamarLamaranItem? = null,
    val selectedPelamarDokumenList: List<DokumenLamaranItem> = emptyList(),
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearActionSuccessMessage() {
        _uiState.value = _uiState.value.copy(actionSuccessMessage = null)
    }

    /**
     * Memuat data statistik dashboard, laporan terkini, dan referensi mitra/lowongan
     */
    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val statsResult = adminMhsRepo.getDashboardStats()
            val laporanResult = adminSiagaRepo.getAllLaporan()
            val laporanList = laporanResult.getOrDefault(_uiState.value.semuaLaporanList)
            val lowonganResult = adminMagangRepo.getLowonganList()
            val lowonganList = lowonganResult.getOrDefault(_uiState.value.lowonganAdminList)
            val tempatResult = adminMagangRepo.getTempatMagangList()
            val tempatList = tempatResult.getOrDefault(_uiState.value.tempatMagangList)
            val lamaranResult = adminMagangRepo.getAllLamaranMasuk()
            val lamaranList = lamaranResult.getOrDefault(_uiState.value.lamaranMasukList)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                stats = statsResult.getOrDefault(_uiState.value.stats),
                semuaLaporanList = laporanList,
                laporanTerbaruList = laporanList.take(5),
                lowonganAdminList = lowonganList,
                tempatMagangList = tempatList,
                lamaranMasukList = lamaranList,
                errorMessage = statsResult.exceptionOrNull()?.message
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
     * Memilih salah satu laporan dan memuat detail, bukti, log tindakan, permintaan informasi, & dokumen hasil
     */
    fun selectLaporan(idOrNomor: String) {
        val found = _uiState.value.semuaLaporanList.find {
            it.id == idOrNomor || it.nomorLaporan == idOrNomor
        }
        val currentId = _uiState.value.selectedLaporan?.id
        val isSwitching = found == null || found.id != currentId
        if (found != null) {
            _uiState.value = _uiState.value.copy(
                selectedLaporan = found,
                selectedLaporanBukti = if (isSwitching) emptyList() else _uiState.value.selectedLaporanBukti,
                selectedLaporanLogs = if (isSwitching) emptyList() else _uiState.value.selectedLaporanLogs,
                selectedLaporanPermintaan = if (isSwitching) emptyList() else _uiState.value.selectedLaporanPermintaan,
                selectedLaporanDokumenHasil = if (isSwitching) emptyList() else _uiState.value.selectedLaporanDokumenHasil
            )
        }

        viewModelScope.launch {
            val res = adminSiagaRepo.getDetailLaporan(idOrNomor)
            val detail = res.getOrNull() ?: found ?: return@launch
            val bukti = adminSiagaRepo.getBuktiLaporan(detail.id).getOrDefault(emptyList())
            val logs = adminSiagaRepo.getLogTindakan(detail.id).getOrDefault(emptyList())
            val permintaan = adminSiagaRepo.getPermintaanInformasi(detail.id).getOrDefault(emptyList())
            val dokumenHasil = adminSiagaRepo.getDokumenHasil(detail.id).getOrDefault(emptyList())

            _uiState.value = _uiState.value.copy(
                selectedLaporan = detail,
                selectedLaporanBukti = bukti,
                selectedLaporanLogs = logs,
                selectedLaporanPermintaan = permintaan,
                selectedLaporanDokumenHasil = dokumenHasil
            )
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
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingLaporan = true, errorMessage = null)
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
                loadDashboardData()
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal memperbarui status laporan"
                _uiState.value = _uiState.value.copy(
                    isUpdatingLaporan = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    /**
     * Mengirim permintaan informasi tambahan ke pelapor dan mengubah status menjadi `diperiksa`.
     */
    fun mintaInformasi(
        laporanId: String,
        pesanKePelapor: String,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingLaporan = true, errorMessage = null)
            val res = adminSiagaRepo.mintaInformasi(laporanId, pesanKePelapor)
            res.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isUpdatingLaporan = false,
                    actionSuccessMessage = "Permintaan informasi berhasil dikirim ke pelapor"
                )
                loadSemuaLaporan()
                selectLaporan(laporanId)
                loadDashboardData()
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal mengirim permintaan informasi"
                _uiState.value = _uiState.value.copy(
                    isUpdatingLaporan = false,
                    errorMessage = msg
                )
                onError(msg)
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
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingLaporan = true, errorMessage = null)
            val res = adminSiagaRepo.addLogTindakan(laporanId, judul, deskripsi)
            res.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isUpdatingLaporan = false,
                    actionSuccessMessage = "Catatan tindakan berhasil disimpan"
                )
                selectLaporan(laporanId)
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal menyimpan catatan tindakan"
                _uiState.value = _uiState.value.copy(
                    isUpdatingLaporan = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    /**
     * Mengunggah satu dokumen hasil penyelesaian ke Supabase Storage & `dokumen_hasil`.
     */
    fun uploadDokumenHasil(
        laporanId: String,
        fileName: String,
        fileBytes: ByteArray,
        bolehDibagikan: Boolean,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingLaporan = true, errorMessage = null)
            val res = adminSiagaRepo.uploadDokumenHasil(
                laporanId = laporanId,
                fileName = fileName,
                fileBytes = fileBytes,
                tipeFile = extractShortFileType(fileName),
                bolehDibagikan = bolehDibagikan
            )
            res.onSuccess {
                _uiState.value = _uiState.value.copy(isUpdatingLaporan = false)
                selectLaporan(laporanId)
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal mengunggah dokumen hasil"
                _uiState.value = _uiState.value.copy(
                    isUpdatingLaporan = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    /**
     * Menyelesaikan laporan pengaduan beserta unggah dokumen hasil opsional
     */
    fun selesaikanLaporan(
        laporanId: String,
        hasilPenanganan: String,
        hasilDipublikasi: Boolean,
        attachments: List<Triple<String, ByteArray, Boolean>> = emptyList(),
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingLaporan = true, errorMessage = null)
            for ((fileName, fileBytes, bolehDibagikan) in attachments) {
                val upRes = adminSiagaRepo.uploadDokumenHasil(
                    laporanId = laporanId,
                    fileName = fileName,
                    fileBytes = fileBytes,
                    tipeFile = extractShortFileType(fileName),
                    bolehDibagikan = bolehDibagikan
                )
                if (upRes.isFailure) {
                    val msg = upRes.exceptionOrNull()?.message ?: "Gagal mengunggah dokumen '$fileName'"
                    _uiState.value = _uiState.value.copy(
                        isUpdatingLaporan = false,
                        errorMessage = msg
                    )
                    onError(msg)
                    return@launch
                }
            }

            val res = adminSiagaRepo.selesaikanLaporan(laporanId, hasilPenanganan, hasilDipublikasi)
            res.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isUpdatingLaporan = false,
                    actionSuccessMessage = "Laporan berhasil diselesaikan"
                )
                loadSemuaLaporan()
                selectLaporan(laporanId)
                loadDashboardData()
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal menyelesaikan laporan"
                _uiState.value = _uiState.value.copy(
                    isUpdatingLaporan = false,
                    errorMessage = msg
                )
                onError(msg)
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
     * Menghasilkan password sementara yang aman dan acak untuk aktivasi akun baru
     */
    private fun generateRandomTempPassword(length: Int = 10): String {
        val upper = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        val lower = "abcdefghjkmnpqrstuvwxyz"
        val digits = "23456789"
        val specials = "!@#$%"
        val all = upper + lower + digits + specials
        val chars = mutableListOf<Char>()
        chars.add(upper.random())
        chars.add(lower.random())
        chars.add(digits.random())
        chars.add(specials.random())
        for (i in 4 until length) {
            chars.add(all.random())
        }
        return chars.shuffled().joinToString("")
    }

    /**
     * Membuat akun mahasiswa baru oleh Admin via RPC dengan password acak
     */
    fun buatAkunMahasiswa(
        namaLengkap: String,
        nim: String,
        email: String,
        jurusan: String?,
        programStudi: String?,
        angkatan: String?,
        noTelepon: String? = null,
        passwordCustom: String? = null,
        onError: (String) -> Unit = {},
        onSuccess: (newUserId: String, tempPassword: String, emailSent: Boolean, emailError: String?) -> Unit
    ) {
        val tempPassword = if (!passwordCustom.isNullOrBlank() && passwordCustom != "Password123!") {
            passwordCustom
        } else {
            generateRandomTempPassword()
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingMahasiswa = true, errorMessage = null)
            val result = adminMhsRepo.createMahasiswa(
                email = email,
                password = tempPassword,
                namaLengkap = namaLengkap,
                nim = nim,
                noTelepon = noTelepon,
                perguruanTinggi = "Politeknik Negeri Samarinda",
                jurusan = jurusan,
                programStudi = programStudi,
                angkatan = angkatan
            )

            result.onSuccess { newUserId ->
                val emailRes = adminMhsRepo.resendActivationLink(email)
                val emailSent = emailRes.isSuccess
                val emailError = emailRes.exceptionOrNull()?.message
                _uiState.value = _uiState.value.copy(
                    isCreatingMahasiswa = false,
                    actionSuccessMessage = if (emailSent) {
                        "Akun mahasiswa $namaLengkap berhasil dibuat dan tautan aktivasi telah dikirim ke $email"
                    } else {
                        "Akun mahasiswa $namaLengkap berhasil dibuat, namun tautan aktivasi belum terkirim"
                    }
                )
                loadMahasiswaList()
                loadDashboardData()
                onSuccess(newUserId, tempPassword, emailSent, emailError)
            }.onFailure { err ->
                val msg = err.message ?: "Gagal membuat akun mahasiswa"
                _uiState.value = _uiState.value.copy(
                    isCreatingMahasiswa = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    /**
     * Mengirim ulang tautan pemulihan/aktivasi password ke email mahasiswa
     */
    fun resendActivationLink(
        email: String,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = adminMhsRepo.resendActivationLink(email)
            res.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    actionSuccessMessage = "Tautan aktivasi dikirim ke $email"
                )
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal mengirim ulang tautan aktivasi"
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = msg)
                onError(msg)
            }
        }
    }

    /**
     * Mengubah status aktif / nonaktif mahasiswa
     */
    fun toggleStatusMahasiswa(
        userId: String,
        newStatus: String,
        onError: (String) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = adminMhsRepo.toggleStatusAkun(userId, newStatus)
            result.onSuccess {
                val freshList = adminMhsRepo.getAllMahasiswa().getOrNull()
                    ?: _uiState.value.mahasiswaList.map {
                        if (it.id == userId) it.copy(statusAkun = newStatus) else it
                    }
                val updatedSelected = freshList.find { it.id == userId }
                    ?: _uiState.value.selectedMahasiswa?.takeIf { it.id == userId }?.copy(statusAkun = newStatus)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    mahasiswaList = freshList,
                    selectedMahasiswa = updatedSelected ?: _uiState.value.selectedMahasiswa
                )
                onComplete()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal mengubah status akun mahasiswa"
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = msg)
                onError(msg)
            }
        }
    }

    /**
     * Memperbarui data identitas & akademik profil mahasiswa oleh Admin
     */
    fun editMahasiswa(
        userId: String,
        namaLengkap: String,
        nim: String,
        email: String,
        perguruanTinggi: String?,
        programStudi: String?,
        angkatan: String?,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val existing = _uiState.value.selectedMahasiswa?.takeIf { it.id == userId }
                ?: _uiState.value.mahasiswaList.find { it.id == userId }
            val cleanEmail = email.trim().lowercase()
            val emailChanged = existing == null || existing.email.trim().lowercase() != cleanEmail

            val res = adminMhsRepo.updateMahasiswaProfile(
                userId = userId,
                namaLengkap = namaLengkap,
                nim = nim,
                email = cleanEmail,
                perguruanTinggi = perguruanTinggi,
                programStudi = programStudi,
                angkatan = angkatan,
                emailChanged = emailChanged
            )
            res.onSuccess {
                val cleanNama = namaLengkap.trim()
                val cleanNim = nim.trim()
                val cleanPt = perguruanTinggi?.trim()?.takeIf { it.isNotBlank() }
                val cleanProdi = programStudi?.trim()?.takeIf { it.isNotBlank() }
                val cleanAngkatan = angkatan?.trim()?.takeIf { it.isNotBlank() }

                val localUpdated = _uiState.value.mahasiswaList.map { item ->
                    if (item.id == userId) {
                        item.copy(
                            namaLengkap = cleanNama,
                            nim = cleanNim,
                            email = cleanEmail,
                            perguruanTinggi = cleanPt,
                            programStudi = cleanProdi,
                            angkatan = cleanAngkatan
                        )
                    } else {
                        item
                    }
                }
                val freshList = adminMhsRepo.getAllMahasiswa().getOrNull() ?: localUpdated
                val updatedSelected = freshList.find { it.id == userId }
                    ?: existing?.copy(
                        namaLengkap = cleanNama,
                        nim = cleanNim,
                        email = cleanEmail,
                        perguruanTinggi = cleanPt,
                        programStudi = cleanProdi,
                        angkatan = cleanAngkatan
                    )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    mahasiswaList = freshList,
                    selectedMahasiswa = updatedSelected
                )
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal memperbarui profil mahasiswa"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    /**
     * Menghapus akun mahasiswa oleh Admin
     */
    fun deleteMahasiswa(
        userId: String,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = adminMhsRepo.deleteMahasiswa(userId)
            res.onSuccess {
                val remaining = _uiState.value.mahasiswaList.filterNot { it.id == userId }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    mahasiswaList = remaining,
                    selectedMahasiswa = null,
                    actionSuccessMessage = "Akun mahasiswa berhasil dihapus"
                )
                loadDashboardData()
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal menghapus akun mahasiswa"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = msg
                )
                onError(msg)
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
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = adminMagangRepo.getTempatMagangList()
            res.onSuccess { list ->
                _uiState.value = _uiState.value.copy(isLoading = false, tempatMagangList = list)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Gagal memuat daftar tempat magang"
                )
            }
        }
    }

    /**
     * Memuat seluruh daftar lowongan magang
     */
    fun loadLowonganAdminList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = adminMagangRepo.getLowonganList()
            val tempatRes = adminMagangRepo.getTempatMagangList()
            val updatedTempat = tempatRes.getOrDefault(_uiState.value.tempatMagangList)
            res.onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lowonganAdminList = list,
                    tempatMagangList = updatedTempat
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    tempatMagangList = updatedTempat,
                    errorMessage = err.message ?: "Gagal memuat daftar lowongan"
                )
            }
        }
    }

    /**
     * Memuat seluruh daftar lamaran masuk dari mahasiswa
     */
    fun loadLamaranMasukList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = adminMagangRepo.getAllLamaranMasuk()
            val lowonganRes = adminMagangRepo.getLowonganList()
            val tempatRes = adminMagangRepo.getTempatMagangList()
            res.onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lamaranMasukList = list,
                    lowonganAdminList = lowonganRes.getOrDefault(_uiState.value.lowonganAdminList),
                    tempatMagangList = tempatRes.getOrDefault(_uiState.value.tempatMagangList)
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Gagal memuat lamaran masuk"
                )
            }
        }
    }

    fun selectTempatMagang(tempat: TempatMagangEntity) {
        _uiState.value = _uiState.value.copy(
            selectedTempatMagang = tempat,
            selectedTempatDokumenList = emptyList()
        )
        val tempatId = tempat.id ?: return
        viewModelScope.launch {
            val doks = adminMagangRepo.getDokumenTempatMagang(tempatId).getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(selectedTempatDokumenList = doks)
        }
    }

    fun selectLowongan(lowongan: LowonganEntity) {
        _uiState.value = _uiState.value.copy(selectedLowonganAdmin = lowongan)
    }

    fun clearSelectedLowongan() {
        _uiState.value = _uiState.value.copy(selectedLowonganAdmin = null)
    }

    fun selectPelamar(pelamar: PelamarLamaranItem) {
        _uiState.value = _uiState.value.copy(
            selectedPelamar = pelamar,
            selectedPelamarDokumenList = emptyList()
        )
        viewModelScope.launch {
            val lamaranUuid = pelamar.lamaranUuid.ifBlank { pelamar.id }
            val doks = adminMagangRepo.getDokumenLamaran(lamaranUuid).getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(selectedPelamarDokumenList = doks)
        }
    }

    /**
     * Mengubah status tempat magang (`draf`, `aktif`, `arsip`)
     */
    fun updateStatusTempatMagang(
        tempatId: String,
        status: String,
        catatanInternal: String? = null,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingMagang = true, errorMessage = null)
            val res = adminMagangRepo.updateStatusTempatMagang(tempatId, status, catatanInternal)
            res.onSuccess {
                val normalized = normalizeTempatStatus(status)
                val curSelected = _uiState.value.selectedTempatMagang
                val updatedSelected = if (curSelected != null && curSelected.id == tempatId) {
                    curSelected.copy(
                        status = normalized,
                        catatanInternal = catatanInternal?.trim()?.takeIf { it.isNotBlank() } ?: curSelected.catatanInternal
                    )
                } else {
                    curSelected
                }
                _uiState.value = _uiState.value.copy(
                    isSubmittingMagang = false,
                    selectedTempatMagang = updatedSelected
                )
                loadTempatMagangList()
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal mengubah status tempat magang"
                _uiState.value = _uiState.value.copy(
                    isSubmittingMagang = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    /**
     * Mengubah status lowongan (`draf`, `terbit`, `ditutup`)
     */
    fun updateStatusLowongan(
        lowonganId: String,
        status: String,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingMagang = true, errorMessage = null)
            val res = adminMagangRepo.updateStatusLowongan(lowonganId, status)
            res.onSuccess {
                val normalized = normalizeLowonganStatus(status)
                val curSelected = _uiState.value.selectedLowonganAdmin
                val updatedSelected = if (curSelected != null && curSelected.id == lowonganId) {
                    curSelected.copy(status = normalized)
                } else {
                    curSelected
                }
                _uiState.value = _uiState.value.copy(
                    isSubmittingMagang = false,
                    selectedLowonganAdmin = updatedSelected
                )
                loadLowonganAdminList()
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal mengubah status lowongan"
                _uiState.value = _uiState.value.copy(
                    isSubmittingMagang = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    /**
     * Menambah tempat magang baru beserta logo dan dokumen legalitas opsional
     */
    fun tambahTempatMagang(
        entity: TempatMagangEntity,
        logoFileName: String? = null,
        logoBytes: ByteArray? = null,
        dokumenFileName: String? = null,
        dokumenBytes: ByteArray? = null,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingMagang = true, errorMessage = null)

            var finalEntity = entity
            if (logoBytes != null) {
                val logoRes = adminMagangRepo.uploadLogoPerusahaan(
                    logoFileName ?: "logo.png",
                    logoBytes
                )
                if (logoRes.isFailure) {
                    val msg = logoRes.exceptionOrNull()?.message ?: "Gagal mengunggah logo"
                    _uiState.value = _uiState.value.copy(isSubmittingMagang = false, errorMessage = msg)
                    onError(msg)
                    return@launch
                }
                finalEntity = finalEntity.copy(logoPath = logoRes.getOrNull())
            }

            val res = adminMagangRepo.createTempatMagang(finalEntity)
            res.onSuccess { created ->
                if (dokumenBytes != null && !created.id.isNullOrBlank()) {
                    val docRes = adminMagangRepo.uploadDokumenTempatMagang(
                        tempatMagangId = created.id,
                        fileName = dokumenFileName ?: "dokumen_mitra.pdf",
                        fileBytes = dokumenBytes
                    )
                    if (docRes.isFailure) {
                        val msg = docRes.exceptionOrNull()?.message
                            ?: "Tempat magang dibuat, namun dokumen kemitraan gagal diunggah."
                        _uiState.value = _uiState.value.copy(
                            isSubmittingMagang = false,
                            errorMessage = msg
                        )
                        loadTempatMagangList()
                        loadDashboardData()
                        onError(msg)
                        return@onSuccess
                    }
                }

                _uiState.value = _uiState.value.copy(isSubmittingMagang = false)
                loadTempatMagangList()
                loadDashboardData()
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal menambah tempat magang"
                _uiState.value = _uiState.value.copy(
                    isSubmittingMagang = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    /**
     * Menambah lowongan magang baru
     */
    fun tambahLowongan(
        entity: LowonganEntity,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingMagang = true, errorMessage = null)
            val res = adminMagangRepo.createLowongan(entity)
            res.onSuccess {
                _uiState.value = _uiState.value.copy(isSubmittingMagang = false)
                loadLowonganAdminList()
                loadDashboardData()
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal membuat lowongan magang"
                _uiState.value = _uiState.value.copy(
                    isSubmittingMagang = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    /**
     * Memperbarui lowongan magang yang sudah ada
     */
    fun updateLowongan(
        lowonganId: String,
        entity: LowonganEntity,
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingMagang = true, errorMessage = null)
            val res = adminMagangRepo.updateLowongan(lowonganId, entity)
            res.onSuccess { updated ->
                _uiState.value = _uiState.value.copy(
                    isSubmittingMagang = false,
                    selectedLowonganAdmin = updated
                )
                loadLowonganAdminList()
                loadDashboardData()
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal memperbarui lowongan magang"
                _uiState.value = _uiState.value.copy(
                    isSubmittingMagang = false,
                    errorMessage = msg
                )
                onError(msg)
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
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingMagang = true, errorMessage = null)
            val res = adminMagangRepo.validasiLamaran(lamaranId, status, catatanAdmin)
            res.onSuccess {
                _uiState.value = _uiState.value.copy(isSubmittingMagang = false)
                loadLamaranMasukList()
                onSuccess()
            }.onFailure { err ->
                val msg = err.message ?: "Gagal memvalidasi berkas lamaran"
                _uiState.value = _uiState.value.copy(
                    isSubmittingMagang = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    suspend fun getSignedUrl(bucketName: String, filePath: String): Result<String> {
        return adminSiagaRepo.createSignedUrl(bucketName, filePath)
    }

    /**
     * Memperbarui profil akun admin (nama lengkap, email, no telepon).
     */
    fun updateAdminProfile(
        context: Context,
        namaLengkap: String,
        email: String,
        noTelepon: String,
        onError: (String) -> Unit = {},
        onSuccess: (UserProfile) -> Unit = {}
    ) {
        val session = SessionManager.getInstance(context)
        val profile = session.getProfile() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = adminMhsRepo.updateAdminProfile(
                userId = profile.id,
                namaLengkap = namaLengkap,
                email = email,
                noTelepon = noTelepon
            )
            res.onSuccess { updated ->
                session.saveSession(updated)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    actionSuccessMessage = "Profil admin berhasil diperbarui"
                )
                onSuccess(updated)
            }.onFailure { err ->
                val msg = err.message ?: "Gagal memperbarui profil admin"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = msg
                )
                onError(msg)
            }
        }
    }

    fun logout(context: Context, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            AuthRepository(context).logout()
            onComplete()
        }
    }
}


