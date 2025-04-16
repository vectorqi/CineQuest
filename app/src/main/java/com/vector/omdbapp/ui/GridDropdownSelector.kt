package com.vector.omdbapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun GridDropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 3
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Box {
            Button(onClick = { expanded = true }) {
                Text(selectedOption.ifBlank { "Select" })
            }

            //Custom Popup dropdown menu
            if (expanded) {
                Popup(
                    alignment = Alignment.TopStart,
                    onDismissRequest = { expanded = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .background(MaterialTheme.colorScheme.surface)
                            .width(240.dp)
                            .heightIn(max = 240.dp),
                        tonalElevation = 4.dp,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            items(options) { option ->
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            onOptionSelected(option)
                                            expanded = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(option, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
