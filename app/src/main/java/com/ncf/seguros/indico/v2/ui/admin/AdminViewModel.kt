package com.ncf.seguros.indico.v2.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncf.seguros.indico.v2.data.AdminRepository
import com.ncf.seguros.indico.v2.model.AdminDashboardData
import com.ncf.seguros.indico.v2.model.ReferralData
import com.ncf.seguros.indico.v2.model.ReferralStatus
import com.ncf.seguros.indico.v2.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class AdminViewModel @Inject constructor(private val repository: AdminRepository) : ViewModel() {
    private val _dashboardState = MutableStateFlow<AdminUIState>(AdminUIState.Loading)
    val dashboardState: StateFlow<AdminUIState> = _dashboardState

    private val _referralsState = MutableStateFlow<ReferralsUIState>(ReferralsUIState.Loading)
    val referralsState: StateFlow<ReferralsUIState> = _referralsState

    init {
        loadDashboardData()
        loadPendingReferrals()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            repository.getDashboardData().collect { result ->
                _dashboardState.value =
                        when (result) {
                            is Result.Success -> AdminUIState.Success(result.data)
                            is Result.Error -> AdminUIState.Error(result.exception.message)
                        }
            }
        }
    }

    private fun loadPendingReferrals() {
        viewModelScope.launch {
            repository.getPendingReferrals().collect { result ->
                _referralsState.value =
                        when (result) {
                            is Result.Success -> ReferralsUIState.Success(result.data)
                            is Result.Error -> ReferralsUIState.Error(result.exception.message)
                        }
            }
        }
    }

    fun updateReferralStatus(referralId: String, newStatus: ReferralStatus) {
        viewModelScope.launch {
            when (repository.updateReferralStatus(referralId, newStatus)) {
                is Result.Success -> loadPendingReferrals()
                is Result.Error -> {
                    // Handle error
                }
            }
        }
    }
}

sealed class AdminUIState {
    data object Loading : AdminUIState()
    data class Success(val data: AdminDashboardData) : AdminUIState()
    data class Error(val message: String?) : AdminUIState()
}

sealed class ReferralsUIState {
    data object Loading : ReferralsUIState()
    data class Success(val referrals: List<ReferralData>) : ReferralsUIState()
    data class Error(val message: String?) : ReferralsUIState()
}
