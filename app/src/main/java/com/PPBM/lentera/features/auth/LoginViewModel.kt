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

    fun clearError() {
        if (_uiState.value.errorMessage != null) {
            _uiState.value = _uiState.value.copy(errorMessage = null)
        }
    }
}
