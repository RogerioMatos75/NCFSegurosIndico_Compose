package com.ncf.seguros.indico.v2.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ncf.seguros.indico.v2.components.PrimaryButton

@Composable
fun HomeScreen(
        viewModel: HomeViewModel = hiltViewModel(),
        onNavigateToPolicies: () -> Unit,
        onNavigateToProfile: () -> Unit
) {
    Scaffold(
            topBar = {
                SmallTopAppBar(
                        title = { Text("NCF Seguros") },
                        colors =
                                TopAppBarDefaults.smallTopAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                )
            }
    ) { paddingValues ->
        Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PrimaryButton(text = "Minhas Apólices", onClick = onNavigateToPolicies)

            PrimaryButton(text = "Meu Perfil", onClick = onNavigateToProfile)
        }
    }
}
