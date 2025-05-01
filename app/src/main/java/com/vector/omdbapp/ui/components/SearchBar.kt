package com.vector.omdbapp.ui.components

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.vector.omdbapp.R
import com.vector.omdbapp.data.model.TypeFilter
import com.vector.omdbapp.data.model.YearFilter


/**
 * Composable for the search input field.
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
    onTypeChange: (TypeFilter) -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val maxCharacters = 30
    var localQuery by rememberSaveable { mutableStateOf(query) }

    Column {
        OutlinedTextField(
            value = localQuery,
            onValueChange = {
                if (it.length <= maxCharacters) {
                    localQuery = it
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search_label)
                )
            },
            trailingIcon = {
                if (localQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.clear_text_desc)
                        )
                    }
                }
            },
            placeholder = { Text(text = context.getString(R.string.search_label)) },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    if (localQuery.trim().isEmpty()) {
                        Toast.makeText(context, context.getString(R.string.empty_query_message), Toast.LENGTH_SHORT).show()
                    } else {
                        onQueryChange(localQuery.trim())
                        onSearchClick()
                    }
                }
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                selected = false,
                onClick = {
                    onYearChange(YearFilter.ALL)
                    onTypeChange(TypeFilter.ALL)
                },
                label = { Text(stringResource(R.string.reset_filters)) },
                shape = MaterialTheme.shapes.medium,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
            SelectableFilterChip(
                label = stringResource(R.string.filter_year),
                options = YearFilter.yearList,
                selectedOption = selectedYear,
                onOptionSelected = onYearChange
            )
            SelectableFilterChip(
                label = stringResource(R.string.filter_type),
                options = TypeFilter.displayNames(),
                selectedOption = selectedType.displayName,
                onOptionSelected = {
                    onTypeChange(TypeFilter.fromDisplayName(it))
                }
            )
        }
    }
}
