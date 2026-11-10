package com.PPBM.lentera.features.mahasiswa.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.PPBM.lentera.backend.core.normalizeLamaranStatus
import com.PPBM.lentera.backend.core.normalizeLaporanStatus
import com.PPBM.lentera.backend.mahasiswa.model.LaporanSaya
import com.PPBM.lentera.backend.mahasiswa.model.LowonganTerbit
import com.PPBM.lentera.backend.mahasiswa.repository.MagangRepository
import com.PPBM.lentera.backend.mahasiswa.repository.MahasiswaRepository
import com.PPBM.lentera.backend.mahasiswa.repository.SiagaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MahasiswaDashboardUiState(
    val isLoading: Boolean = false,
    val totalLaporan: Int = 0,
    val laporanDiperiksa: Int = 0,
    val laporanSelesai: Int = 0,
    val totalLamaran: Int = 0,
    val lamaranStatusSummary: String = "Tidak ada update",
    val aktivitasTerbaru: List<LaporanSaya> = emptyList(),
    val rekomendasiLowongan: List<LowonganTerbit> = emptyList(),
    val hasUnreadNotification: Boolean = false,
    val errorMessage: String? = null
)

class MahasiswaDashboardViewModel(
    private val siagaRepo: SiagaRepository = SiagaRepository(),
    private val magangRepo: MagangRepository = MagangRepository(),
    private val mahasiswaRepo: MahasiswaRepository = MahasiswaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MahasiswaDashboardUiState())
    val uiState: StateFlow<MahasiswaDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // 1. Ambil Laporan Saya
            val laporanResult = siagaRepo.getLaporanSaya()
            val laporanList = laporanResult.getOrDefault(emptyList())

            val totalLap = laporanList.size
            val lapDiperiksa = laporanList.count {
                val st = normalizeLaporanStatus(it.status)
                st == "baru" || st == "diperiksa" || st == "diproses"
            }
            val lapSelesai = laporanList.count { normalizeLaporanStatus(it.status) == "selesai" }

            // 2. Ambil Lamaran Saya
            val lamaranResult = magangRepo.getLamaranSaya()
            val lamaranList = lamaranResult.getOrDefault(emptyList())
            val totalLam = lamaranList.size
            val normalizedLamaranStatuses = lamaranList.map { normalizeLamaranStatus(it.status) }
            val lamaranSummary = when {
                totalLam == 0 -> "Belum ada lamaran"
                normalizedLamaranStatuses.any { it == "diterima" } -> "Lamaran Diterima!"
                normalizedLamaranStatuses.any { it == "perlu_perbaikan" } -> "Perlu perbaikan berkas"
                normalizedLamaranStatuses.any { it == "seleksi" } -> "Tahap seleksi"
                normalizedLamaranStatuses.any { it == "pemeriksaan" } -> "Dalam pemeriksaan"
                else -> "$totalLam lamaran aktif"
            }

            // 3. Ambil Lowongan Terbit
            val lowonganResult = magangRepo.getLowonganTerbit()
            val lowonganList = lowonganResult.getOrDefault(emptyList()).take(5)

            // 4. Periksa Notifikasi Baru (Unread)
            val notifResult = mahasiswaRepo.getNotifikasi()
            val hasUnread = notifResult.getOrDefault(emptyList()).any { !it.isDibaca }

            _uiState.value = MahasiswaDashboardUiState(
                isLoading = false,
                totalLaporan = totalLap,
                laporanDiperiksa = lapDiperiksa,
                laporanSelesai = lapSelesai,
                totalLamaran = totalLam,
                lamaranStatusSummary = lamaranSummary,
                aktivitasTerbaru = laporanList.take(3),
                rekomendasiLowongan = lowonganList,
                hasUnreadNotification = hasUnread,
                errorMessage = null
            )
        }
    }
}

