package com.ncf.seguros.indico.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncf.seguros.indico.model.Insurance
import com.ncf.seguros.indico.repository.InsuranceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsuranceViewModel @Inject constructor(
    private val insuranceRepository: InsuranceRepository
) : ViewModel() {

    private val _insurancesState = MutableStateFlow<InsurancesState>(InsurancesState.Loading)
    val insurancesState: StateFlow<InsurancesState> = _insurancesState

    private val _insuranceState = MutableStateFlow<InsuranceState>(InsuranceState.Initial)
    val insuranceState: StateFlow<InsuranceState> = _insuranceState

    fun loadInsurancesForCondominium(condominiumId: String) {
        viewModelScope.launch {
            _insurancesState.value = InsurancesState.Loading
            try {
                insuranceRepository.getInsurancesForCondominium(condominiumId).collect { insurances ->
                    _insurancesState.value = InsurancesState.Success(insurances)
                }
            } catch (e: Exception) {
                _insurancesState.value = InsurancesState.Error(e.message ?: "Erro ao carregar seguros")
            }
        }
    }

    fun addInsurance(insurance: Insurance) {
        viewModelScope.launch {
            _insuranceState.value = InsuranceState.Loading
            val result = insuranceRepository.addInsurance(insurance)
            result.fold(
                onSuccess = { id ->
                    _insuranceState.value = InsuranceState.Added(id)
                    loadInsurancesForCondominium(insurance.condominiumId)
                },
                onFailure = { e ->
                    _insuranceState.value = InsuranceState.Error(e.message ?: "Erro ao adicionar seguro")
                }
            )
        }
    }

    fun updateInsurance(insurance: Insurance) {
        viewModelScope.launch {
            _insuranceState.value = InsuranceState.Loading
            val result = insuranceRepository.updateInsurance(insurance)
            result.fold(
                onSuccess = {
                    _insuranceState.value = InsuranceState.Updated
                    loadInsurancesForCondominium(insurance.condominiumId)
                },
                onFailure = { e ->
                    _insuranceState.value = InsuranceState.Error(e.message ?: "Erro ao atualizar seguro")
                }
            )
        }
    }

    fun deleteInsurance(insuranceId: String, condominiumId: String) {
        viewModelScope.launch {
            _insuranceState.value = InsuranceState.Loading
            val result = insuranceRepository.deleteInsurance(insuranceId)
            result.fold(
                onSuccess = {
                    _insuranceState.value = InsuranceState.Deleted
                    loadInsurancesForCondominium(condominiumId)
                },
                onFailure = { e ->
                    _insuranceState.value = InsuranceState.Error(e.message ?: "Erro ao excluir seguro")
                }
            )
        }
    }

    sealed class InsurancesState {
        object Loading : InsurancesState()
        data class Success(val insurances: List<Insurance>) : InsurancesState()
        data class Error(val message: String) : InsurancesState()
    }

    sealed class InsuranceState {
        object Initial : InsuranceState()
        object Loading : InsuranceState()
        data class Added(val id: String) : InsuranceState()
        object Updated : InsuranceState()
        object Deleted : InsuranceState()
        data class Error(val message: String) : InsuranceState()
    }
} 