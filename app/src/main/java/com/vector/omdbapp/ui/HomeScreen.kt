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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vector.omdbapp.R
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.model.TypeFilter
import com.vector.omdbapp.data.model.YearFilter
import com.vector.omdbapp.ui.components.ErrorState
import com.vector.omdbapp.ui.components.MovieList
import com.vector.omdbapp.ui.components.SearchBar
import com.vector.omdbapp.viewmodel.MovieViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Represents the UI state, including movies, query text, and
 * status flags for loading, pagination, etc.
 *
 * @param query Current search keyword.
 * @param movies The current list of displayed movies.
 * @param errorMessage Error message for any failed requests.
 * @param isLoading Indicates the initial (main) loading is in progress.
 * @param isPaginating Indicates if a subsequent "load more" request is in progress.
 * @param noMoreData True when we've loaded all possible data.
 * @param totalResults Number of total hits reported by OMDB.
 */
data class HomeUiState(
    val query: String = "",
    val selectedYear: String = YearFilter.ALL,
    val selectedType: TypeFilter = TypeFilter.ALL,// "all" or "" means no filter
    val movies: List<Movie> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val noMoreData: Boolean = false,  // Prevents further loading after final page
    val totalResults: Int = 0
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: MovieViewModel = hiltViewModel(),
    listState: LazyListState,
    navController: NavHostController,
) {
    val uiState by viewModel.homeUiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (uiState.movies.isEmpty() && !uiState.isLoading) {
            withContext(Dispatchers.IO) {
                viewModel.onQueryChange("Hero")
                viewModel.searchMovies()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SearchBar(
                    query = uiState.query,
                    onQueryChange = viewModel::onQueryChange,
                    onSearchClick = { viewModel.searchMovies() },
                    selectedYear = uiState.selectedYear,
                    onYearChange = viewModel::onYearChange,
                    selectedType = uiState.selectedType,
                    onTypeChange = viewModel::onTypeChange
                )
            }

            when {
                uiState.errorMessage != null -> {
                    ErrorState(
                        message = uiState.errorMessage.toString(),
                        onRetry = {
                            if (uiState.query.trim().isEmpty()) {
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