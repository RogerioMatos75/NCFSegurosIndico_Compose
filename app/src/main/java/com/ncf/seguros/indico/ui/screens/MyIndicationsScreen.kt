package com.ncf.seguros.indico.ui.screens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ncf.seguros.indico.model.Indication
import com.ncf.seguros.indico.ui.components.AppTopBar
import com.ncf.seguros.indico.ui.components.ErrorMessage
import com.ncf.seguros.indico.ui.components.LoadingIndicator
import com.ncf.seguros.indico.viewmodel.IndicationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyIndicationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddIndication: () -> Unit,
    viewModel: IndicationViewModel = hiltViewModel()
) {
    val indicationsState by viewModel.indicationsState.collectAsState()
    val userDiscountState by viewModel.userDiscountState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadUserIndications()
        viewModel.loadUserDiscount()
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Minhas Indicações",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddIndication,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nova indicação"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Mostrar desconto acumulado
            when (userDiscountState) {
                is IndicationViewModel.UserDiscountState.Loading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                is IndicationViewModel.UserDiscountState.Success -> {
                    val discount = (userDiscountState as IndicationViewModel.UserDiscountState.Success).discount
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
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
                    Text(
                        text = "Erro ao carregar desconto",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Lista de indicações
            when (indicationsState) {
                is IndicationViewModel.IndicationsState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is IndicationViewModel.IndicationsState.Success -> {
                    val indications = (indicationsState as IndicationViewModel.IndicationsState.Success).indications
                    
                    if (indications.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Você ainda não fez nenhuma indicação",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Indique amigos e ganhe descontos na renovação do seu seguro",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onNavigateToAddIndication) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Nova Indicação")
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Suas indicações",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            
                            items(indications) { indication ->
                                IndicationCard(indication = indication)
                            }
                            
                            item {
                                Spacer(modifier = Modifier.height(80.dp)) // Para o FAB não cobrir o último item
                            }
                        }
                    }
                }
                is IndicationViewModel.IndicationsState.Error -> {
                    val errorMessage = (indicationsState as IndicationViewModel.IndicationsState.Error).message
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorMessage(
                            message = errorMessage,
                            onRetry = { viewModel.loadUserIndications() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IndicationCard(indication: Indication) {
    val statusColor = when (indication.status) {
        "pending" -> Color.Gray
        "contacted" -> Color.Blue
        "converted" -> Color.Green
        "rejected" -> Color.Red
        else -> Color.Gray
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
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
                Text(
                    text = indication.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                Chip(
                    onClick = { },
                    colors = ChipDefaults.chipColors(
                        containerColor = statusColor.copy(alpha = 0.1f),
                        labelColor = statusColor
                    )
                ) {
                    Text(indication.getStatusDisplay())
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = indication.phone,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = indication.email,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${indication.vehicleType} ${indication.vehicleModel} (${indication.vehicleYear})",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Indicado em: ${indication.getFormattedDate()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
} 