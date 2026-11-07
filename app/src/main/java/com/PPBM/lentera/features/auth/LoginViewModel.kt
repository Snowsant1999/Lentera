package com.PPBM.lentera.features.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.PPBM.lentera.backend.auth.model.UserProfile
import com.PPBM.lentera.backend.auth.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val loggedInProfile: UserProfile? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(nimOrEmail: String, password: String, onRoleDetermined: (role: String) -> Unit) {
        if (nimOrEmail.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState(errorMessage = "NIM/Email dan Password wajib diisi.")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            val result = authRepository.login(nimOrEmail, password)
            result.fold(
                onSuccess = { profile ->
                    _uiState.value = LoginUiState(loggedInProfile = profile)
                    onRoleDetermined(profile.role)
                },
                onFailure = { error ->
                    _uiState.value = LoginUiState(errorMessage = error.message ?: "Login gagal.")
                }
            )
        }
    }

    /**
     * Mengirim instruksi reset password / kode OTP ke email
     */
    fun requestPasswordReset(email: String, onSuccess: () -> Unit = {}) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email wajib diisi.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.requestPasswordReset(email)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Tautan pemulihan telah dikirim ke email $email"
                )
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Gagal mengirim tautan reset password."
                )
            }
        }
    }

    /**
     * Memverifikasi kode OTP pemulihan
     */
    fun verifyRecoveryOtp(email: String, token: String, onSuccess: () -> Unit = {}) {
        if (token.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Kode verifikasi wajib diisi.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.verifyRecoveryOtp(email, token)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Kode verifikasi salah atau kedaluwarsa."
                )
            }
        }
    }

    /**
     * Memperbarui password baru pengguna
     */
    fun updatePassword(newPassword: String, onSuccess: () -> Unit = {}) {
        if (newPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password baru tidak boleh kosong.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.updatePassword(newPassword)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Password berhasil diperbarui. Silakan login kembali."
                )
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Gagal memperbarui password."
                )
            }
        }
    }

    fun clearError() {
        if (_uiState.value.errorMessage != null) {
            _uiState.value = _uiState.value.copy(errorMessage = null)
        }
    }
}
