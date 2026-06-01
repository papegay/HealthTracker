package com.genegebra.healthtracker.presentation.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genegebra.healthtracker.domain.model.SessionManager
import com.genegebra.healthtracker.domain.model.User
import com.genegebra.healthtracker.domain.repository.AuthRepository
import com.genegebra.healthtracker.domain.usecase.LoginUseCase
import com.genegebra.healthtracker.domain.usecase.RegisterUseCase
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegisterMode: Boolean = false,
    val pendingEmailVerification: Boolean = false,
    val loggedInUser: User? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // reCAPTCHA site key — replace with your key from Google Cloud Console
    private val recaptchaSiteKey = "6Ld7sActAAAAAEsq49NVl4JNAxr-lsZFbCK6YebN"

    fun toggleMode() {
        _uiState.update { it.copy(isRegisterMode = !it.isRegisterMode, error = null) }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            loginUseCase(email, password)
                .onSuccess { user ->
                    sessionManager.newSession()
                    if (!user.isEmailVerified) {
                        _uiState.update { it.copy(isLoading = false, pendingEmailVerification = true) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, loggedInUser = user) }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun register(email: String, password: String, activity: Activity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val recaptchaClient = Recaptcha.getTasksClient(activity)
                val token = recaptchaClient.execute(RecaptchaAction.REGISTER).getOrThrow()
                registerUseCase(email, password, token)
                    .onSuccess {
                        _uiState.update {
                            it.copy(isLoading = false, pendingEmailVerification = true, isRegisterMode = false)
                        }
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "reCAPTCHA failed: ${e.message}") }
            }
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            authRepository.sendVerificationEmail()
        }
    }

    fun checkEmailVerified() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.reloadUser()
                .onSuccess { user ->
                    if (user.isEmailVerified) {
                        _uiState.update { it.copy(isLoading = false, pendingEmailVerification = false, loggedInUser = user) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Email not yet verified. Check your inbox.") }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
