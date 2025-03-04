package com.ncf.seguros.indico.v2.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Botão primário padrão do aplicativo */
@Composable
fun PrimaryButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled) { Text(text = text) }
}
