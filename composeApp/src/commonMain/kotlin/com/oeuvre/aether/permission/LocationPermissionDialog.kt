package com.oeuvre.aether.permission

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun LocationPermissionDialog(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onAction) { Text(actionLabel) }
        },
    )
}
