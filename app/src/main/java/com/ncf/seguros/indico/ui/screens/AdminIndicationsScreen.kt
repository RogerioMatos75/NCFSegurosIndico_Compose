package com.ncf.seguros.indico.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ncf.seguros.indico.model.Indication
import com.ncf.seguros.indico.ui.components.AppTopBar
import com.ncf.seguros.indico.ui.components.ErrorMessage
import com.ncf.seguros.indico.ui.components.LoadingIndicator
import com.ncf.seguros.indico.viewmodel.IndicationViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminIndicationsScreen(
    onNavigateBack: () -> Unit,
    viewModel: IndicationViewModel = hiltViewModel()
) {
    val indicationsState by viewModel.indicationsState.collectAsState()
    val indicationState by viewModel.indicationState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var selectedIndication by remember { mutableStateOf<Indication?>(null) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var statusNotes by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        viewModel.loadAllIndications()
    }
    
    LaunchedEffect(indicationState) {
        if (indicationState is IndicationViewModel.IndicationState.Updated ||
            indicationState is IndicationViewModel.IndicationState.Deleted) {
            viewModel.loadAllIndications()
        }
    }
    
    // Observar o evento de envio de link
    LaunchedEffect(Unit) {
        viewModel.sendLinkEvent.collect { event ->
            try {
                context.startActivity(event.intent)
                // Atualizar o status da indicação para mostrar que o link foi enviado
                viewModel.updateIndicationStatus(
                    event.indicationId, 
                    "contacted", 
                    "Link de interesse enviado em ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date())}"
                )
            } catch (e: Exception) {
                // Mostrar mensagem de erro
                snackbarHostState.showSnackbar(
                    "Erro ao enviar link: ${e.message}"
                )
            }
        }
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Administração de Indicações",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        when (indicationsState) {
            is IndicationViewModel.IndicationsState.Loading -> {
                LoadingIndicator()
            }
            is IndicationViewModel.IndicationsState.Success -> {
                val indications = (indicationsState as IndicationViewModel.IndicationsState.Success).indications
                
                if (indications.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
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
                            text = "Nenhuma indicação encontrada",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                    ) {
                        item {
                            Text(
                                text = "Todas as indicações",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(indications) { indication ->
                            AdminIndicationCard(
                                indication = indication,
                                onStatusChange = {
                                    selectedIndication = indication
                                    showStatusDialog = true
                                },
                                onDelete = {
                                    selectedIndication = indication
                                    showDeleteDialog = true
                                },
                                onSendLink = {
                                    selectedIndication = indication
                                    viewModel.sendInterestLink(indication)
                                }
                            )
                        }
                    }
                }
            }
            is IndicationViewModel.IndicationsState.Error -> {
                val errorMessage = (indicationsState as IndicationViewModel.IndicationsState.Error).message
                ErrorMessage(
                    message = errorMessage,
                    onRetry = { viewModel.loadAllIndications() }
                )
            }
        }
    }
    
    // Diálogo para alterar o status
    if (showStatusDialog && selectedIndication != null) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Alterar Status") },
            text = {
                Column {
                    Text("Indicação de ${selectedIndication?.name}")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Selecione o novo status:")
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedStatus == "pending",
                            onClick = { selectedStatus = "pending" }
                        )
                        Text("Pendente", modifier = Modifier.padding(start = 8.dp))
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedStatus == "contacted",
                            onClick = { selectedStatus = "contacted" }
                        )
                        Text("Contatado", modifier = Modifier.padding(start = 8.dp))
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedStatus == "converted",
                            onClick = { selectedStatus = "converted" }
                        )
                        Text("Convertido", modifier = Modifier.padding(start = 8.dp))
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedStatus == "rejected",
                            onClick = { selectedStatus = "rejected" }
                        )
                        Text("Rejeitado", modifier = Modifier.padding(start = 8.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = statusNotes,
                        onValueChange = { statusNotes = it },
                        label = { Text("Observações") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Adicionamos uma checkbox para enviar o link
                    if (selectedStatus == "contacted") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var sendLink by remember { mutableStateOf(false) }
                            Checkbox(
                                checked = sendLink,
                                onCheckedChange = { sendLink = it }
                            )
                            Text(
                                text = "Enviar link de interesse para o indicado",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedIndication?.id?.let { id ->
                            viewModel.updateIndicationStatus(id, selectedStatus, statusNotes)
                            
                            // Se o status for "contacted" e a opção de enviar link estiver marcada
                            if (selectedStatus == "contacted" && sendLink) {
                                selectedIndication?.let { indication ->
                                    viewModel.sendInterestLink(indication)
                                }
                            }
                        }
                        showStatusDialog = false
                    },
                    enabled = selectedStatus.isNotEmpty()
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo para confirmar exclusão
    if (showDeleteDialog && selectedIndication != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja excluir a indicação de ${selectedIndication?.name}?") },
            confirmButton = {
                Button(
                    onClick = {
                        selectedIndication?.id?.let { id ->
                            viewModel.deleteIndication(id)
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun AdminIndicationCard(
    indication: Indication,
    onStatusChange: () -> Unit,
    onDelete: () -> Unit,
    onSendLink: () -> Unit
) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = indication.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = "Indicado por: ${indication.referrerName}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Row {
                    IconButton(onClick = onStatusChange) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Alterar status"
                        )
                    }
                    
                    IconButton(onClick = onSendLink) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar link de interesse"
                        )
                    }
                    
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Chip(
                onClick = { },
                colors = ChipDefaults.chipColors(
                    containerColor = statusColor.copy(alpha = 0.1f),
                    labelColor = statusColor
                )
            ) {
                Text(indication.getStatusDisplay())
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
            
            if (indication.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Note,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = indication.notes,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
} 