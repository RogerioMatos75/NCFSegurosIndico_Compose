package com.ncf.seguros.indico.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ncf.seguros.indico.ui.components.ErrorMessage
import com.ncf.seguros.indico.ui.components.InfoCard
import com.ncf.seguros.indico.ui.components.LoadingIndicator
import com.ncf.seguros.indico.viewmodel.AuthViewModel
import com.ncf.seguros.indico.viewmodel.DashboardViewModel
import com.ncf.seguros.indico.viewmodel.IndicationViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToCondominiums: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToInsurances: (String, String) -> Unit,
    onNavigateToInsurance: (String, String) -> Unit,
    onLogout: () -> Unit,
    onNavigateToMyIndications: () -> Unit,
    onNavigateToAddIndication: () -> Unit,
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    indicationViewModel: IndicationViewModel = hiltViewModel()
) {
    val dashboardState by dashboardViewModel.dashboardState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val userDiscountState by indicationViewModel.userDiscountState.collectAsState()
    
    LaunchedEffect(Unit) {
        dashboardViewModel.loadDashboardData()
        indicationViewModel.loadUserDiscount()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil"
                        )
                    }
                    IconButton(onClick = {
                        authViewModel.logout()
                        onLogout()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sair"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCondominiums,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar condomínio"
                )
            }
        }
    ) { paddingValues ->
        when (dashboardState) {
            is DashboardViewModel.DashboardState.Loading -> {
                LoadingIndicator()
            }
            is DashboardViewModel.DashboardState.Success -> {
                val data = (dashboardState as DashboardViewModel.DashboardState.Success).data
                val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Olá, ${currentUser?.name?.split(" ")?.firstOrNull() ?: ""}",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoCard(
                                title = "Condomínios",
                                value = data.condominiumCount.toString(),
                                icon = Icons.Default.Apartment,
                                modifier = Modifier.weight(1f)
                            )
                            InfoCard(
                                title = "Seguros",
                                value = data.insuranceCount.toString(),
                                icon = Icons.Default.Security,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoCard(
                                title = "Seguros a vencer",
                                value = data.expiringInsuranceCount.toString(),
                                icon = Icons.Default.Warning,
                                modifier = Modifier.weight(1f)
                            )
                            InfoCard(
                                title = "Valor total",
                                value = currencyFormat.format(data.totalCoverageAmount),
                                icon = Icons.Default.AttachMoney,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    if (data.expiringInsurances.isNotEmpty()) {
                        item {
                            Text(
                                text = "Seguros prestes a vencer",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        
                        items(data.expiringInsurances) { insurance ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onNavigateToInsurance(insurance.id, insurance.condominiumId)
                                    }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Apólice: ${insurance.policyNumber}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Seguradora: ${insurance.insuranceCompany}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Vence em: ${insurance.getRemainingDays()} dias",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                    
                    if (data.recentCondominiums.isNotEmpty()) {
                        item {
                            Text(
                                text = "Condomínios recentes",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        
                        items(data.recentCondominiums) { condominium ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onNavigateToInsurances(condominium.id, condominium.name)
                                    }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = condominium.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${condominium.address}, ${condominium.number} - ${condominium.neighborhood}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${condominium.city} - ${condominium.state}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is DashboardViewModel.DashboardState.Error -> {
                val errorMessage = (dashboardState as DashboardViewModel.DashboardState.Error).message
                ErrorMessage(
                    message = errorMessage,
                    onRetry = { dashboardViewModel.loadDashboardData() }
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Desconto acumulado
            when (userDiscountState) {
                is IndicationViewModel.UserDiscountState.Loading -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is IndicationViewModel.UserDiscountState.Success -> {
                    val discount = (userDiscountState as IndicationViewModel.UserDiscountState.Success).discount
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Seu desconto acumulado",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$discount%",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "na renovação do seu seguro",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                is IndicationViewModel.UserDiscountState.Error -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Erro ao carregar desconto",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(
                                onClick = { indicationViewModel.loadUserDiscount() },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Tentar novamente")
                            }
                        }
                    }
                }
            }
            
            // Ações principais
            Text(
                text = "O que você deseja fazer?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionCard(
                    icon = Icons.Default.PersonAdd,
                    title = "Indicar Amigo",
                    onClick = onNavigateToAddIndication,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                
                ActionCard(
                    icon = Icons.Default.List,
                    title = "Minhas Indicações",
                    onClick = onNavigateToMyIndications,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Informações sobre o programa
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Como funciona o programa de indicação",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    BulletPoint(text = "Indique amigos e familiares interessados em seguros de automóveis")
                    BulletPoint(text = "Ganhe 1% de desconto por cada indicação")
                    BulletPoint(text = "Ganhe mais 1% se a indicação se converter em cliente")
                    BulletPoint(text = "Acumule até 10% de desconto na renovação do seu seguro")
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = onNavigateToAddIndication,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Indicar agora")
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 8.dp, top = 2.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 