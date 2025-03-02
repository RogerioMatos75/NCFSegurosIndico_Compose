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
import com.ncf.seguros.indico.model.Condominium
import com.ncf.seguros.indico.ui.components.AppTopBar
import com.ncf.seguros.indico.viewmodel.CondominiumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCondominiumScreen(
    onNavigateBack: () -> Unit,
    onCondominiumAdded: () -> Unit,
    viewModel: CondominiumViewModel = hiltViewModel()
) {
    val condominiumState by viewModel.condominiumState.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var complement by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(condominiumState) {
        when (condominiumState) {
            is CondominiumViewModel.CondominiumState.Added -> {
                onCondominiumAdded()
            }
            is CondominiumViewModel.CondominiumState.Error -> {
                errorMessage = (condominiumState as CondominiumViewModel.CondominiumState.Error).message
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Adicionar Condomínio",
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
                label = { Text("Nome do Condomínio") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Apartment,
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
                value = address,
                onValueChange = { address = it },
                label = { Text("Endereço") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
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
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Número") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = complement,
                    onValueChange = { complement = it },
                    label = { Text("Complemento") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .weight(2f)
                        .padding(bottom = 16.dp)
                )
            }
            
            OutlinedTextField(
                value = neighborhood,
                onValueChange = { neighborhood = it },
                label = { Text("Bairro") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Cidade") },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .weight(2f)
                        .padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = { Text("UF") },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                )
            }
            
            OutlinedTextField(
                value = zipCode,
                onValueChange = { zipCode = it },
                label = { Text("CEP") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
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
                    if (name.isBlank() || address.isBlank() || number.isBlank() || 
                        neighborhood.isBlank() || city.isBlank() || state.isBlank() || zipCode.isBlank()) {
                        errorMessage = "Preencha todos os campos obrigatórios"
                    } else {
                        val condominium = Condominium(
                            name = name,
                            address = address,
                            number = number,
                            complement = complement,
                            neighborhood = neighborhood,
                            city = city,
                            state = state,
                            zipCode = zipCode
                        )
                        viewModel.addCondominium(condominium)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = condominiumState !is CondominiumViewModel.CondominiumState.Loading
            ) {
                if (condominiumState is CondominiumViewModel.CondominiumState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Adicionar Condomínio")
                }
            }
        }
    }
} 