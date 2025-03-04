package com.ncf.seguros.indico.v2.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncf.seguros.indico.v2.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            when (val result = repository.getUserData()) {
                is Result.Success -> _uiState.value = HomeUiState.Success(result.data)
                is Result.Error ->
                        _uiState.value =
                                HomeUiState.Error(
                                        result.exception.localizedMessage
                                                ?: "Erro ao carregar dados"
                                )
            }
        }
    }
}

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val userData: UserData) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

data class UserData(val name: String, val policyCount: Int)
