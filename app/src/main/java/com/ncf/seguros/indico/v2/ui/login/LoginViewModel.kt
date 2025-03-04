package com.ncf.seguros.indico.v2.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncf.seguros.indico.v2.util.Result
import com.ncf.seguros.indico.v2.util.ValidationResult
import com.ncf.seguros.indico.v2.util.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var emailError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun onEmailChange(value: String) {
        email = value
        emailError =
                when (val result = Validators.validateEmail(value)) {
                    is ValidationResult.Error -> result.message
                    ValidationResult.Success -> null
                }
    }

    fun onPasswordChange(value: String) {
        password = value
        passwordError =
                when (val result = Validators.validatePassword(value)) {
                    is ValidationResult.Error -> result.message
                    ValidationResult.Success -> null
                }
    }

    fun login() {
        val emailValidation = Validators.validateEmail(email)
        val passwordValidation = Validators.validatePassword(password)

        emailError = (emailValidation as? ValidationResult.Error)?.message
        passwordError = (passwordValidation as? ValidationResult.Error)?.message

        if (emailValidation is ValidationResult.Success &&
                        passwordValidation is ValidationResult.Success
        ) {
            performLogin()
        }
    }

    private fun performLogin() {
        viewModelScope.launch {
            isLoading = true
            _uiState.value = LoginUiState.Loading

            when (val result = authRepository.login(email, password)) {
                is Result.Success -> {
                    _uiState.value = LoginUiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value =
                            LoginUiState.Error(
                                    message = result.exception.localizedMessage
                                                    ?: "Erro ao fazer login"
                            )
                }
            }
            isLoading = false
        }
    }
}

/** Estados possíveis da tela de login */
sealed class LoginUiState {
    data object Initial : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val user: User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
