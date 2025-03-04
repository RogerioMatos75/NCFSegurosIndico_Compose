package com.ncf.seguros.indico.v2.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ncf.seguros.indico.v2.model.ReferralStatus

@Composable
fun AdminScreen(viewModel: AdminViewModel = hiltViewModel(), onNavigateToUsers: () -> Unit) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    val referralsState by viewModel.referralsState.collectAsState()

    Scaffold(
            topBar = {
                SmallTopAppBar(
                        title = { Text("Painel Administrativo") },
                        colors =
                                TopAppBarDefaults.smallTopAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                )
            }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            // Dashboard Stats
            when (dashboardState) {
                is AdminUIState.Success -> {
                    val data = (dashboardState as AdminUIState.Success).data
                    DashboardStats(data)
                }
                is AdminUIState.Loading -> {
                    CircularProgressIndicator()
                }
                is AdminUIState.Error -> {
                    Text(
                            text = (dashboardState as AdminUIState.Error).message
                                            ?: "Erro ao carregar dados",
                            color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pending Referrals
            Text(text = "Indicações Pendentes", style = MaterialTheme.typography.headlineSmall)

            when (referralsState) {
                is ReferralsUIState.Success -> {
                    val referrals = (referralsState as ReferralsUIState.Success).referrals
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(referrals) { referral ->
                            ReferralCard(
                                    referral = referral,
                                    onApprove = {
                                        viewModel.updateReferralStatus(
                                                referral.id,
                                                ReferralStatus.APPROVED
                                        )
                                    },
                                    onReject = {
                                        viewModel.updateReferralStatus(
                                                referral.id,
                                                ReferralStatus.REJECTED
                                        )
                                    }
                            )
                        }
                    }
                }
                is ReferralsUIState.Loading -> {
                    CircularProgressIndicator()
                }
                is ReferralsUIState.Error -> {
                    Text(
                            text = (referralsState as ReferralsUIState.Error).message
                                            ?: "Erro ao carregar indicações",
                            color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
