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
import com.ncf.seguros.indico.model.Insurance
import com.ncf.seguros.indico.ui.components.AppTopBar
import com.ncf.seguros.indico.viewmodel.InsuranceViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInsuranceScreen(
    condominiumId: String,
    onNavigateBack: () -> Unit,
    onInsuranceAdded: () -> Unit,
    viewModel: InsuranceViewModel = hiltViewModel()
) {
    val insuranceState by viewModel.insuranceState.collectAsState()
    
    var policyNumber by remember { mutableStateOf("") }
    var insuranceCompany by remember { mutableStateOf("") }
    var coverageType by remember { mutableStateOf("") }
    var coverageAmount by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var premium by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    
    LaunchedEffect(insuranceState) {
        when (insuranceState) {
            is InsuranceViewModel.InsuranceState.Added -> {
                onInsuranceAdded()
            }
            is InsuranceViewModel.InsuranceState.Error -> {
                errorMessage = (insuranceState as InsuranceViewModel.InsuranceState.Error).message
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Adicionar Seguro",
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
                value = policyNumber,
                onValueChange = { policyNumber = it },
                label = { Text("Número da Apólice") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Numbers,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            OutlinedTextField(
                value = insuranceCompany,
                onValueChange = { insuranceCompany = it },
                label = { Text("Seguradora") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Business,
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
                value = coverageType,
                onValueChange = { coverageType = it },
                label = { Text("Tipo de Cobertura") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Security,
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
                value = coverageAmount,
                onValueChange = { coverageAmount = it },
                label = { Text("Valor da Cobertura (R$)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
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
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Data de Início (DD/MM/AAAA)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
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
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("Data de Término (DD/MM/AAAA)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
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
                value = premium,
                onValueChange = { premium = it },
                label = { Text("Prêmio (R$)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Payments,
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
                    if (policyNumber.isBlank() || insuranceCompany.isBlank() || coverageType.isBlank() || 
                        coverageAmount.isBlank() || startDate.isBlank() || endDate.isBlank() || premium.isBlank()) {
                        errorMessage = "Preencha todos os campos obrigatórios"
                    } else {
                        try {
                            val coverageAmountValue = coverageAmount.replace(",", ".").toDouble()
                            val premiumValue = premium.replace(",", ".").toDouble()
                            
                            val startDateValue = dateFormat.parse(startDate)?.time
                            val endDateValue = dateFormat.parse(endDate)?.time
                            
                            if (startDateValue == null || endDateValue == null) {
                                errorMessage = "Formato de data inválido. Use DD/MM/AAAA"
                                return@Button
                            }
                            
                            if (startDateValue > endDateValue) {
                                errorMessage = "A data de início não pode ser posterior à data de término"
                                return@Button
                            }
                            
                            val insurance = Insurance(
                                condominiumId = condominiumId,
                                policyNumber = policyNumber,
                                insuranceCompany = insuranceCompany,
                                coverageType = coverageType,
                                coverageAmount = coverageAmountValue,
                                startDate = startDateValue,
                                endDate = endDateValue,
                                premium = premiumValue,
                                status = "active"
                            )
                            viewModel.addInsurance(insurance)
                        } catch (e: Exception) {
                            errorMessage = "Erro ao processar os valores. Verifique os campos numéricos e datas."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = insuranceState !is InsuranceViewModel.InsuranceState.Loading
            ) {
                if (insuranceState is InsuranceViewModel.InsuranceState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Adicionar Seguro")
                }
            }
        }
    }
} 