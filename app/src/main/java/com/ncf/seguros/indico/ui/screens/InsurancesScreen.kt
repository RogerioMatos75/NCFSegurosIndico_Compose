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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ncf.seguros.indico.model.Insurance
import com.ncf.seguros.indico.ui.components.AppTopBar
import com.ncf.seguros.indico.ui.components.ErrorMessage
import com.ncf.seguros.indico.ui.components.LoadingIndicator
import com.ncf.seguros.indico.viewmodel.InsuranceViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsurancesScreen(
    condominiumId: String,
    condominiumName: String,
    onNavigateBack: () -> Unit,
    onNavigateToAddInsurance: (String) -> Unit,
    onNavigateToEditInsurance: (String, String) -> Unit,
    viewModel: InsuranceViewModel = hiltViewModel()
) {
    val insurancesState by viewModel.insurancesState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var insuranceToDelete by remember { mutableStateOf<Insurance?>(null) }
    
    LaunchedEffect(condominiumId) {
        viewModel.loadInsurancesForCondominium(condominiumId)
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Seguros - $condominiumName",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddInsurance(condominiumId) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar seguro"
                )
            }
        }
    ) { paddingValues ->
        when (insurancesState) {
            is InsuranceViewModel.InsurancesState.Loading -> {
                LoadingIndicator()
            }
            is InsuranceViewModel.InsurancesState.Success -> {
                val insurances = (insurancesState as InsuranceViewModel.InsurancesState.Success).insurances
                
                if (insurances.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhum seguro cadastrado",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Clique no botão + para adicionar um seguro",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(insurances) { insurance ->
                            InsuranceCard(
                                insurance = insurance,
                                onCardClick = {
                                    onNavigateToEditInsurance(insurance.id, condominiumId)
                                },
                                onEditClick = {
                                    onNavigateToEditInsurance(insurance.id, condominiumId)
                                },
                                onDeleteClick = {
                                    insuranceToDelete = insurance
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
            is InsuranceViewModel.InsurancesState.Error -> {
                val errorMessage = (insurancesState as InsuranceViewModel.InsurancesState.Error).message
                ErrorMessage(
                    message = errorMessage,
                    onRetry = { viewModel.loadInsurancesForCondominium(condominiumId) }
                )
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar exclusão") },
            text = { Text("Tem certeza que deseja excluir o seguro ${insuranceToDelete?.policyNumber}? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        insuranceToDelete?.let {
                            viewModel.deleteInsurance(it.id, condominiumId)
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun InsuranceCard(
    insurance: Insurance,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    
    val isActive = insurance.isActive()
    val remainingDays = insurance.getRemainingDays()
    
    val statusColor = when {
        !isActive -> Color.Red
        remainingDays < 30 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFF4CAF50) // Green
    }
    
    val statusText = when {
        !isActive -> "Vencido"
        remainingDays < 30 -> "Vence em $remainingDays dias"
        else -> "Ativo"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = insurance.insuranceCompany,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = "Apólice: ${insurance.policyNumber}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar"
                        )
                    }
                    
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Cobertura",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = insurance.coverageType,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Valor",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = currencyFormat.format(insurance.coverageAmount),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Início",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = dateFormat.format(Date(insurance.startDate)),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Vencimento",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = dateFormat.format(Date(insurance.endDate)),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "Prêmio: ${currencyFormat.format(insurance.premium)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
} 