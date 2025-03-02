package com.ncf.seguros.indico.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncf.seguros.indico.model.Condominium
import com.ncf.seguros.indico.repository.CondominiumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CondominiumViewModel @Inject constructor(
    private val condominiumRepository: CondominiumRepository
) : ViewModel() {

    private val _condominiumsState = MutableStateFlow<CondominiumsState>(CondominiumsState.Loading)
    val condominiumsState: StateFlow<CondominiumsState> = _condominiumsState

    private val _condominiumState = MutableStateFlow<CondominiumState>(CondominiumState.Initial)
    val condominiumState: StateFlow<CondominiumState> = _condominiumState

    fun loadCondominiums() {
        viewModelScope.launch {
            _condominiumsState.value = CondominiumsState.Loading
            try {
                condominiumRepository.getCondominiumsForUser().collect { condominiums ->
                    _condominiumsState.value = CondominiumsState.Success(condominiums)
                }
            } catch (e: Exception) {
                _condominiumsState.value = CondominiumsState.Error(e.message ?: "Erro ao carregar condomínios")
            }
        }
    }

    fun getCondominiumById(condominiumId: String) {
        viewModelScope.launch {
            _condominiumState.value = CondominiumState.Loading
            val result = condominiumRepository.getCondominiumById(condominiumId)
            result.fold(
                onSuccess = { condominium ->
                    _condominiumState.value = CondominiumState.Success(condominium)
                },
                onFailure = { e ->
                    _condominiumState.value = CondominiumState.Error(e.message ?: "Erro ao carregar condomínio")
                }
            )
        }
    }

    fun addCondominium(condominium: Condominium) {
        viewModelScope.launch {
            _condominiumState.value = CondominiumState.Loading
            val result = condominiumRepository.addCondominium(condominium)
            result.fold(
                onSuccess = { id ->
                    _condominiumState.value = CondominiumState.Added(id)
                    loadCondominiums()
                },
                onFailure = { e ->
                    _condominiumState.value = CondominiumState.Error(e.message ?: "Erro ao adicionar condomínio")
                }
            )
        }
    }

    fun updateCondominium(condominium: Condominium) {
        viewModelScope.launch {
            _condominiumState.value = CondominiumState.Loading
            val result = condominiumRepository.updateCondominium(condominium)
            result.fold(
                onSuccess = {
                    _condominiumState.value = CondominiumState.Updated
                    loadCondominiums()
                },
                onFailure = { e ->
                    _condominiumState.value = CondominiumState.Error(e.message ?: "Erro ao atualizar condomínio")
                }
            )
        }
    }

    fun deleteCondominium(condominiumId: String) {
        viewModelScope.launch {
            _condominiumState.value = CondominiumState.Loading
            val result = condominiumRepository.deleteCondominium(condominiumId)
            result.fold(
                onSuccess = {
                    _condominiumState.value = CondominiumState.Deleted
                    loadCondominiums()
                },
                onFailure = { e ->
                    _condominiumState.value = CondominiumState.Error(e.message ?: "Erro ao excluir condomínio")
                }
            )
        }
    }

    sealed class CondominiumsState {
        object Loading : CondominiumsState()
        data class Success(val condominiums: List<Condominium>) : CondominiumsState()
        data class Error(val message: String) : CondominiumsState()
    }

    sealed class CondominiumState {
        object Initial : CondominiumState()
        object Loading : CondominiumState()
        data class Success(val condominium: Condominium) : CondominiumState()
        data class Added(val id: String) : CondominiumState()
        object Updated : CondominiumState()
        object Deleted : CondominiumState()
        data class Error(val message: String) : CondominiumState()
    }
} 