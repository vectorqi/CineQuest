package com.vector.omdbapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vector.omdbapp.R
import com.vector.omdbapp.viewmodel.MovieViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

/**
 * MovieList.kt
 *
 * Composable that displays a scrollable list of MovieItems.
 * Supports infinite pagination via scroll listener and shows loading/no-more-data UI at the bottom.
 */

@Composable
fun MovieList(listState: LazyListState) {
    val viewModel: MovieViewModel = hiltViewModel()
    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    /**
     * Scroll detection logic:
     * - This effect monitors the scrolling state of LazyColumn.
     * - It uses snapshotFlow to continuously observe the last visible item index.
     * - If the user scrolls to the last item, it triggers the `loadMoreMovies()` function in the ViewModel.
     */
    @OptIn(FlowPreview::class)
    LaunchedEffect(Unit) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .debounce(200)
            .collect { lastVisibleIndex ->
                if (
                    !uiState.noMoreData &&
                    !uiState.isLoading &&
                    !uiState.isPaginating &&
                    !uiState.isRefreshing &&
                    uiState.movies.isNotEmpty() &&
                    lastVisibleIndex != null &&
                    lastVisibleIndex >= uiState.movies.size - 1 &&
                    viewModel.currentPage > 1 &&
                    listState.firstVisibleItemIndex > 0
                ) {
                    viewModel.loadMoreMovies()
                }
            }
    }

    /**
     * LazyColumn displays the list of movies in a scrollable column.
     * - `state = listState` ensures we track scroll position.
     */
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        //Display each movie item in the list.
        items(uiState.movies, key = { movie -> movie.imdbID }) { movie ->
            MovieItem(movie = movie, viewModel = viewModel)
            HorizontalDivider(modifier = Modifier.fillMaxWidth(),thickness = 1.dp)
        }
        if (uiState.isPaginating) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator() // Shows a spinning loader
                }
            }
        }
        // Display a "No more data" message if all results have been loaded
        if (uiState.noMoreData) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = context.getString(R.string.no_more_data))
                }
            }
        }
    }
}