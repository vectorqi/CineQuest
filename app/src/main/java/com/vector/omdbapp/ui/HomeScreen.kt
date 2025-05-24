package com.vector.omdbapp.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vector.omdbapp.R
import com.vector.omdbapp.ui.components.ErrorState
import com.vector.omdbapp.ui.components.MovieList
import com.vector.omdbapp.ui.components.SearchBar
import com.vector.omdbapp.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    listState: LazyListState,
    navController: NavHostController,
) {
    val viewModel: MovieViewModel = hiltViewModel()
    val query by viewModel.query.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SearchBar(
                    query = query,
                    onQueryChange = viewModel::onQueryChange,
                    onSearchClick = { viewModel.searchMovies() },
                    selectedYear = selectedYear,
                    onYearChange = viewModel::onYearChange,
                    selectedType = selectedType,
                    onTypeChange = viewModel::onTypeChange
                )
            }

            when {
                errorMessage != null -> {
                    ErrorState(
                        message = errorMessage.toString(),
                        onRetry = {
                            if (query.trim().isEmpty()) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.empty_query_message),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                viewModel.searchMovies()
                            }
                        }
                    )
                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        MovieList(listState, navController)
                    }
                }
            }
        }
    }
}