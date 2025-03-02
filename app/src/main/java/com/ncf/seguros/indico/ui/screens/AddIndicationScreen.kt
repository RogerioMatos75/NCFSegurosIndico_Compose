package com.ncf.seguros.indico.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ncf.seguros.indico.model.Indication
import com.ncf.seguros.indico.ui.components.AppTopBar
import com.ncf.seguros.indico.viewmodel.IndicationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIndicationScreen(
    onNavigateBack: () -> Unit,
    onIndicationAdded: () -> Unit,
    viewModel: IndicationViewModel = hiltViewModel()
) {
    val indicationState by viewModel.indicationState.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var vehicleYear by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(indicationState) {
        when (indicationState) {
            is IndicationViewModel.IndicationState.Added -> {
                onIndicationAdded()
            }
            is IndicationViewModel.IndicationState.Error -> {
                errorMessage = (indicationState as IndicationViewModel.IndicationState.Error).message
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Nova Indicação",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome da Pessoa Indicada") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Telefone") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            OutlinedTextField(
                value = vehicleType,
                onValueChange = { vehicleType = it },
                label = { Text("Tipo de Veículo") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            OutlinedTextField(
                value = vehicleModel,
                onValueChange = { vehicleModel = it },
                label = { Text("Modelo do Veículo") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            OutlinedTextField(
                value = vehicleYear,
                onValueChange = { vehicleYear = it },
                label = { Text("Ano do Veículo") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Observações (opcional)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Note,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
            
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            Button(
                onClick = {
                    errorMessage = null
                    if (name.isBlank() || phone.isBlank() || email.isBlank() || 
                        vehicleType.isBlank() || vehicleModel.isBlank() || vehicleYear.isBlank()) {
                        errorMessage = "Preencha todos os campos obrigatórios"
                    } else {
                        val indication = Indication(
                            name = name,
                            phone = phone,
                            email = email,
                            vehicleType = vehicleType,
                            vehicleModel = vehicleModel,
                            vehicleYear = vehicleYear,
                            notes = notes
                        )
                        viewModel.addIndication(indication)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = indicationState !is IndicationViewModel.IndicationState.Loading
            ) {
                if (indicationState is IndicationViewModel.IndicationState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Enviar Indicação")
                }
            }
        }
    }
} 