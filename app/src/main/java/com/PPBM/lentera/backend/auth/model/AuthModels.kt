package com.PPBM.lentera.backend.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representasi profil pengguna dari tabel `public.profiles`.
 */
@Serializable
data class UserProfile(
    val id: String,
    val role: String, // "admin" atau "mahasiswa"
    @SerialName("nama_lengkap")
    val namaLengkap: String,
    val nim: String? = null,
    val email: String,
    @SerialName("no_telepon")
    val noTelepon: String? = null,
    @SerialName("perguruan_tinggi")
    val perguruanTinggi: String? = null,
    val jurusan: String? = null,
    @SerialName("program_studi")
    val programStudi: String? = null,
    val angkatan: String? = null,
    @SerialName("status_akun")
    val statusAkun: String = "aktif",
    @SerialName("foto_profil_url")
    val fotoProfilUrl: String? = null
)

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val profile: UserProfile) : AuthState()
    data class Error(val message: String) : AuthState()
}

