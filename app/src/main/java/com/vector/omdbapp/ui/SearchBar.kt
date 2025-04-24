package com.vector.omdbapp.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.vector.omdbapp.R
import com.vector.omdbapp.data.model.TypeFilter
import com.vector.omdbapp.data.model.YearFilter

/**
 * Composable for the search input field and button.
 * Handles user input with character limit and triggers search actions.
 * Automatically hides keyboard upon search submission.
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    selectedYear: String,
    onYearChange: (String) -> Unit,
    selectedType: TypeFilter,
    onTypeChange: (TypeFilter) -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val maxCharacters = 30
    val onQueryChangeState = rememberUpdatedState(onQueryChange)
    val onSearchClickState = rememberUpdatedState(onSearchClick)
    val onYearChangeState = rememberUpdatedState(onYearChange)
    val onTypeChangeState = rememberUpdatedState(onTypeChange)

    Column {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = query,
                onValueChange = { newText ->
                    if (newText.length <= maxCharacters) {
                        onQueryChangeState.value(newText)
                    }
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text(context.getString(R.string.search_label)) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                keyboardController?.hide()
                if (query.trim().isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.empty_query_message), Toast.LENGTH_SHORT).show()
                } else {
                    onSearchClickState.value()
                }
            }) {
                Text(context.getString(R.string.search_button))
            }
        }

        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            val typeOptions = TypeFilter.displayNames()
            val yearOptions = YearFilter.generateYearOptions()

            GridDropdownSelector(
                label = context.getString(R.string.filter_type),
                options = typeOptions,
                selectedOption = selectedType.displayName,
                onOptionSelected = {
                    onTypeChangeState.value(TypeFilter.fromDisplayName(it))
                },
                modifier = Modifier.weight(1f)
            )

            GridDropdownSelector(
                label = context.getString(R.string.filter_year),
                options = yearOptions,
                selectedOption = selectedYear,
                onOptionSelected = onYearChangeState.value,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
