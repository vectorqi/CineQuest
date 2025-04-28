package com.vector.omdbapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vector.omdbapp.R

/**
 * ErrorState composable to show a unified empty/error screen.
 * Displays an icon, a message, and an optional retry button.
 */
@Composable
fun ErrorState(
    message: String,
    icon: ImageVector = Icons.Filled.Warning,
    onRetry:  (() -> Unit)? = null
) {
    val isNetworkError = remember(message) {
    message.contains("unable to resolve host", ignoreCase = true)
            || message.contains("failed to connect", ignoreCase = true)
            || message.contains("timeout", ignoreCase = true)
}

    val displayedMessage = if (isNetworkError) {
        stringResource(id = R.string.error_network_unavailable)
    } else {
        message
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = displayedMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text(text = "Retry")
            }
        }
    }
}
