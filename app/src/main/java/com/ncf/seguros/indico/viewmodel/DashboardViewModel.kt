package com.ncf.seguros.indico.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncf.seguros.indico.model.Condominium
import com.ncf.seguros.indico.model.Insurance
import com.ncf.seguros.indico.repository.CondominiumRepository
import com.ncf.seguros.indico.repository.InsuranceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val condominiumRepository: CondominiumRepository,
    private val insuranceRepository: InsuranceRepository
) : ViewModel() {

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState

    fun loadDashboardData() {
        viewModelScope.launch {
            _dashboardState.value = DashboardState.Loading
            try {
                // Carregar condomínios
                val condominiums = condominiumRepository.getCondominiumsForUser().first()
                
                // Carregar seguros para cada condomínio
                val allInsurances = mutableListOf<Insurance>()
                for (condominium in condominiums) {
                    val insurances = insuranceRepository.getInsurancesForCondominium(condominium.id).first()
                    allInsurances.addAll(insurances)
                }
                
                // Calcular estatísticas
                val expiringInsurances = allInsurances.filter { 
                    it.isActive() && it.getRemainingDays() < 30 
                }.sortedBy { it.getRemainingDays() }
                
                val totalCoverageAmount = allInsurances
                    .filter { it.isActive() }
                    .sumOf { it.coverageAmount }
                
                val dashboardData = DashboardData(
                    condominiumCount = condominiums.size,
                    insuranceCount = allInsurances.size,
                    expiringInsuranceCount = expiringInsurances.size,
                    totalCoverageAmount = totalCoverageAmount,
                    expiringInsurances = expiringInsurances,
                    recentCondominiums = condominiums.sortedByDescending { it.createdAt }.take(5)
                )
                
                _dashboardState.value = DashboardState.Success(dashboardData)
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error(e.message ?: "Erro ao carregar dados do dashboard")
            }
        }
    }

    data class DashboardData(
        val condominiumCount: Int,
        val insuranceCount: Int,
        val expiringInsuranceCount: Int,
        val totalCoverageAmount: Double,
        val expiringInsurances: List<Insurance>,
        val recentCondominiums: List<Condominium>
    )

    sealed class DashboardState {
        object Loading : DashboardState()
        data class Success(val data: DashboardData) : DashboardState()
        data class Error(val message: String) : DashboardState()
    }
} 