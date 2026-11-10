package com.PPBM.lentera.features.auth

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.PPBM.lentera.backend.auth.model.UserProfile
import com.PPBM.lentera.backend.auth.repository.AuthRepository
import com.PPBM.lentera.backend.auth.repository.RecoveryDeepLinkParser
import com.PPBM.lentera.backend.auth.repository.RecoveryDeepLinkResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val loggedInProfile: UserProfile? = null,
    val isProcessingDeepLink: Boolean = false,
    val deepLinkErrorMessage: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    private var deepLinkJob: Job? = null

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(nimOrEmail: String, password: String, onRoleDetermined: (role: String, statusAkun: String) -> Unit) {
        if (_uiState.value.isLoading) return
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
                    onRoleDetermined(profile.role, profile.statusAkun)
                },
                onFailure = { error ->
                    _uiState.value = LoginUiState(errorMessage = error.message ?: "Login gagal.")
                }
            )
        }
    }

    /**
     * Mengirim kode OTP pemulihan ke email
     */
    fun requestPasswordReset(email: String, onSuccess: () -> Unit = {}) {
        if (_uiState.value.isLoading) return
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email wajib diisi.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.requestPasswordReset(cleanEmail)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Kode OTP pemulihan telah dikirim ke email $cleanEmail"
                )
                onSuccess()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Gagal mengirim kode OTP pemulihan."
                )
            }
        }
    }

    /**
     * Memverifikasi kode OTP pemulihan
     */
    fun verifyRecoveryOtp(email: String, token: String, onSuccess: () -> Unit = {}) {
        if (_uiState.value.isLoading) return
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
        if (_uiState.value.isLoading) return
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
                    successMessage = "Password berhasil diperbarui."
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

    fun handleRecoveryDeepLink(
        uri: Uri,
        onAuthenticatedForReset: (email: String) -> Unit
    ) {
        val parsed = RecoveryDeepLinkParser.parse(
            scheme = uri.scheme,
            host = uri.host,
            encodedQuery = uri.encodedQuery,
            encodedFragment = uri.encodedFragment
        ) ?: return

        when (parsed) {
            is RecoveryDeepLinkResult.Error -> {
                deepLinkJob?.cancel()
                _uiState.value = _uiState.value.copy(
                    isProcessingDeepLink = false,
                    deepLinkErrorMessage = parsed.message
                )
            }
            is RecoveryDeepLinkResult.TokenSession -> {
                deepLinkJob?.cancel()
                _uiState.value = _uiState.value.copy(
                    isProcessingDeepLink = true,
                    deepLinkErrorMessage = null
                )
                deepLinkJob = viewModelScope.launch {
                    authRepository.importRecoverySessionFromFragment(parsed.fragment)
                        .onSuccess { email ->
                            _uiState.value = _uiState.value.copy(
                                isProcessingDeepLink = false,
                                deepLinkErrorMessage = null
                            )
                            onAuthenticatedForReset(email)
                        }
                        .onFailure { err ->
                            _uiState.value = _uiState.value.copy(
                                isProcessingDeepLink = false,
                                deepLinkErrorMessage = err.message ?: "Tautan pemulihan tidak dapat diproses."
                            )
                        }
                }
            }
        }
    }

    fun clearDeepLinkError() {
        if (_uiState.value.deepLinkErrorMessage != null) {
            _uiState.value = _uiState.value.copy(deepLinkErrorMessage = null)
        }
    }

    fun logout(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }

    fun clearError() {
        if (_uiState.value.errorMessage != null) {
            _uiState.value = _uiState.value.copy(errorMessage = null)
        }
    }
}
