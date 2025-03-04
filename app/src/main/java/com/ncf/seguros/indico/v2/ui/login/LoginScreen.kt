package com.ncf.seguros.indico.v2.ui.login

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ncf.seguros.indico.v2.components.LoginTextField
import com.ncf.seguros.indico.v2.components.PrimaryButton

const val TAG_EMAIL_FIELD = "email_field"
const val TAG_PASSWORD_FIELD = "password_field"
const val TAG_LOGIN_BUTTON = "login_button"

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), onNavigateToHome: () -> Unit) {
    var showPassword by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onNavigateToHome()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "Login", style = MaterialTheme.typography.headlineMedium)

                    LoginTextField(
                            value = viewModel.email,
                            onValueChange = viewModel::onEmailChange,
                            label = "Email",
                            modifier = Modifier.fillMaxWidth().testTag(TAG_EMAIL_FIELD),
                            isError = viewModel.emailError != null,
                            errorMessage = viewModel.emailError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    LoginTextField(
                            value = viewModel.password,
                            onValueChange = viewModel::onPasswordChange,
                            label = "Senha",
                            modifier = Modifier.fillMaxWidth().testTag(TAG_PASSWORD_FIELD),
                            isError = viewModel.passwordError != null,
                            errorMessage = viewModel.passwordError,
                            visualTransformation =
                                    if (showPassword) VisualTransformation.None
                                    else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                            imageVector =
                                                    if (showPassword) Icons.Default.VisibilityOff
                                                    else Icons.Default.Visibility,
                                            contentDescription =
                                                    if (showPassword) "Ocultar senha"
                                                    else "Mostrar senha"
                                    )
                                }
                            }
                    )

                    PrimaryButton(
                            text = "Entrar",
                            onClick = viewModel::login,
                            modifier = Modifier.fillMaxWidth().testTag(TAG_LOGIN_BUTTON),
                            enabled = !viewModel.isLoading
                    )
                }
            }
        }

        // Loading indicator
        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // Error snackbar
        if (uiState is LoginUiState.Error) {
            val errorMessage = (uiState as LoginUiState.Error).message
            SnackbarHost(
                    hostState =
                            remember { SnackbarHostState() }.apply {
                                LaunchedEffect(errorMessage) {
                                    showSnackbar(
                                            message = errorMessage,
                                            duration = SnackbarDuration.Short
                                    )
                                }
                            },
                    modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
