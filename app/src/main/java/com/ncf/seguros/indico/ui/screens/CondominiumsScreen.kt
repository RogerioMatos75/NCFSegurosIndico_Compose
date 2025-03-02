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
import com.ncf.seguros.indico.model.Condominium
import com.ncf.seguros.indico.ui.components.AppTopBar
import com.ncf.seguros.indico.ui.components.ErrorMessage
import com.ncf.seguros.indico.ui.components.LoadingIndicator
import com.ncf.seguros.indico.viewmodel.CondominiumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CondominiumsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddCondominium: () -> Unit,
    onNavigateToEditCondominium: (String) -> Unit,
    onNavigateToInsurances: (String, String) -> Unit,
    viewModel: CondominiumViewModel = hiltViewModel()
) {
    val condominiumsState by viewModel.condominiumsState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var condominiumToDelete by remember { mutableStateOf<Condominium?>(null) }
    
    LaunchedEffect(Unit) {
        viewModel.loadCondominiums()
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Condomínios",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddCondominium,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar condomínio"
                )
            }
        }
    ) { paddingValues ->
        when (condominiumsState) {
            is CondominiumViewModel.CondominiumsState.Loading -> {
                LoadingIndicator()
            }
            is CondominiumViewModel.CondominiumsState.Success -> {
                val condominiums = (condominiumsState as CondominiumViewModel.CondominiumsState.Success).condominiums
                
                if (condominiums.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhum condomínio cadastrado",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Clique no botão + para adicionar um condomínio",
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
                        items(condominiums) { condominium ->
                            CondominiumCard(
                                condominium = condominium,
                                onCardClick = {
                                    onNavigateToInsurances(condominium.id, condominium.name)
                                },
                                onEditClick = {
                                    onNavigateToEditCondominium(condominium.id)
                                },
                                onDeleteClick = {
                                    condominiumToDelete = condominium
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
            is CondominiumViewModel.CondominiumsState.Error -> {
                val errorMessage = (condominiumsState as CondominiumViewModel.CondominiumsState.Error).message
                ErrorMessage(
                    message = errorMessage,
                    onRetry = { viewModel.loadCondominiums() }
                )
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar exclusão") },
            text = { Text("Tem certeza que deseja excluir o condomínio ${condominiumToDelete?.name}? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        condominiumToDelete?.let {
                            viewModel.deleteCondominium(it.id)
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
fun CondominiumCard(
    condominium: Condominium,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
                Text(
                    text = condominium.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
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
            
            Text(
                text = "${condominium.address}, ${condominium.number}",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "${condominium.neighborhood}, ${condominium.city} - ${condominium.state}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "CEP: ${condominium.zipCode}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
} 