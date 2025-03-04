package com.ncf.seguros.indico.v2.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel(), onNavigateBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
            topBar = {
                SmallTopAppBar(
                        title = { Text("Meu Perfil") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, "Voltar")
                            }
                        }
                )
            }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            when (uiState) {
                is ProfileUiState.Success -> {
                    val userData = (uiState as ProfileUiState.Success).userData
                    UserProfileContent(userData)
                }
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is ProfileUiState.Error -> {
                    Text(
                            text = (uiState as ProfileUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun UserProfileContent(userData: UserData) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ProfileSection("Informações Pessoais") {
            ProfileField("Nome", userData.name)
            ProfileField("Email", userData.email)
            ProfileField("Telefone", userData.phone)
        }

        ProfileSection("Estatísticas") {
            ProfileField("Total de Indicações", "${userData.totalReferrals}")
            ProfileField("Indicações Aprovadas", "${userData.approvedReferrals}")
        }
    }
}
