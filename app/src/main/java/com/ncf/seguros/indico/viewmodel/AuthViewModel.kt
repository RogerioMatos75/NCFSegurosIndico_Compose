package com.ncf.seguros.indico.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.ncf.seguros.indico.model.User
import com.ncf.seguros.indico.repository.AuthRepository
import com.ncf.seguros.indico.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _profileUpdateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Initial)
    val profileUpdateState: StateFlow<ProfileUpdateState> = _profileUpdateState

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val firebaseUser = authRepository.currentUser
        if (firebaseUser != null) {
            _authState.value = AuthState.Authenticated(firebaseUser)
            loadUserData(firebaseUser.uid)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { user ->
                    _authState.value = AuthState.Authenticated(user)
                    loadUserData(user.uid)
                },
                onFailure = { e ->
                    _authState.value = AuthState.Error(e.message ?: "Erro ao fazer login")
                }
            )
        }
    }

    fun register(name: String, email: String, password: String, phone: String, cpf: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.register(name, email, password, phone, cpf)
            result.fold(
                onSuccess = { user ->
                    _authState.value = AuthState.Authenticated(user)
                    loadUserData(user.uid)
                },
                onFailure = { e ->
                    _authState.value = AuthState.Error(e.message ?: "Erro ao registrar")
                }
            )
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Unauthenticated
        _currentUser.value = null
    }

    private fun loadUserData(userId: String) {
        viewModelScope.launch {
            val result = authRepository.getUserData(userId)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                },
                onFailure = { e ->
                    _authState.value = AuthState.Error(e.message ?: "Erro ao carregar dados do usuário")
                }
            )
        }
    }

    fun updateProfile(name: String, phone: String) {
        _profileUpdateState.value = ProfileUpdateState.Loading
        viewModelScope.launch {
            val result = userRepository.updateUserProfile(name, phone)
            result.fold(
                onSuccess = {
                    // Recarregar os dados do usuário
                    authRepository.currentUser?.uid?.let { userId ->
                        loadUserData(userId)
                    }
                    _profileUpdateState.value = ProfileUpdateState.Success
                },
                onFailure = { e ->
                    _profileUpdateState.value = ProfileUpdateState.Error(e.message ?: "Erro ao atualizar perfil")
                }
            )
        }
    }

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        data class Authenticated(val user: FirebaseUser) : AuthState()
        object Unauthenticated : AuthState()
        data class Error(val message: String) : AuthState()
    }

    sealed class ProfileUpdateState {
        object Initial : ProfileUpdateState()
        object Loading : ProfileUpdateState()
        object Success : ProfileUpdateState()
        data class Error(val message: String) : ProfileUpdateState()
    }
} 