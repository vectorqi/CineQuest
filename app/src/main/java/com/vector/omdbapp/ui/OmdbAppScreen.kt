package com.vector.omdbapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vector.omdbapp.R
import com.vector.omdbapp.viewmodel.MovieViewModel
import kotlinx.coroutines.launch

/**
 * OmdbAppScreen.kt
 *
 * Main composable screen for the OMDB App.
 * Displays the top bar, search bar, movie list, and handles loading/error states.
 * Also listens to snackbar events from the ViewModel and displays them via SnackbarHost.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun OmdbAppScreen() {
    val viewModel: MovieViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Observe snackbar messages from the ViewModel
    LaunchedEffect(Unit) {
        viewModel.snackbarFlow.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }
    // Scaffold with top bar and snackbar
    Scaffold(topBar = {
        TopAppBar(title = { Text(context.getString(R.string.app_title)) })
    },snackbarHost = { SnackbarHost(hostState = snackbarHostState) })
    {
        Column(modifier = Modifier.padding(it).fillMaxSize()) {
            SearchBar(
                query = uiState.query,
                onQueryChange = { newQuery -> viewModel.onQueryChange(newQuery) },
                onSearchClick = { viewModel.searchMovies(context.getString(R.string.empty_query_message)) },
                selectedYear = uiState.selectedYear,
                onYearChange = viewModel::onYearChange,
                selectedType = uiState.selectedType,
                onTypeChange = viewModel::onTypeChange
            )
            when {
                uiState.isLoading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = context.getString(R.string.error_label) + uiState.errorMessage,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    val pullRefreshState = rememberPullRefreshState(
                        refreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.refreshMovies() }
                    )

                    Box(modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                    ) {
                        MovieList()

                        PullRefreshIndicator(
                            refreshing = uiState.isRefreshing,
                            state = pullRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                }
            }
        }
    }
}