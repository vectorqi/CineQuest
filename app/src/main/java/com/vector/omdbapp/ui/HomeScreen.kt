package com.vector.omdbapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vector.omdbapp.R
import com.vector.omdbapp.viewmodel.MovieViewModel

/**
 * The main screen displaying a list of movies based on user search criteria.
 * Features include a search bar with year and type filters, loading and error states,
 * pull-to-refresh functionality, and navigation to movie details.
 *
 * @param viewModel The [MovieViewModel] responsible for managing the UI state and fetching movie data.
 * @param listState The [LazyListState] to control the scrolling position of the movie list.
 * @param navController The [NavHostController] for navigating to other screens, such as movie details.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: MovieViewModel = hiltViewModel(),
    listState: LazyListState,
    navController: NavHostController,
    ) {
    val context = LocalContext.current
    val uiState by viewModel.homeUiState.collectAsState()

    /*
     *Automatically sets query to "Hero" and triggers searchMovies()，
     *Ensures initial movie list is populated without user input
     */
    LaunchedEffect(Unit) {
        if (uiState.movies.isEmpty() && !uiState.isLoading) {
            viewModel.onQueryChange("Hero")
            viewModel.searchMovies()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChange,
                onSearchClick = { viewModel.searchMovies() },
                selectedYear = uiState.selectedYear,
                onYearChange = viewModel::onYearChange,
                selectedType = uiState.selectedType,
                onTypeChange = viewModel::onTypeChange,
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

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(pullRefreshState)
                    ) {
                        MovieList(listState, navController)

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
