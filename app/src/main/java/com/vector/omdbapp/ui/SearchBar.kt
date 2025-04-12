package com.vector.omdbapp.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.vector.omdbapp.R

/**
 * SearchBar.kt
 *
 * Composable for the search input field and button.
 * Handles user input with character limit and triggers search actions.
 * Automatically hides keyboard upon search submission.
 */

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    // Limit the input to 30 characters
    val maxCharacters = 30
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TextField for user input
        TextField(
            value = query,
            onValueChange = { newText ->
                // Limit the input to 30 characters
                if (newText.length <= maxCharacters) {
                    onQueryChange(newText)
                }
            },
            modifier = Modifier.weight(1f),
            label = { Text(context.getString(R.string.search_label)) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Button to trigger the search
        Button(onClick = {
            // Hide the keyboard
            keyboardController?.hide()
            onSearchClick()
        }
        ) {
            Text(context.getString(R.string.search_button))
        }
    }
}