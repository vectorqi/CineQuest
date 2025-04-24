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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.request.CachePolicy
import com.vector.omdbapp.R
import com.vector.omdbapp.viewmodel.FavoriteViewModel
import com.vector.omdbapp.viewmodel.MovieViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

/**
 * MovieList.kt
 *
 * Composable that displays a scrollable list of MovieItems.
 * Supports infinite pagination via scroll listener and shows loading/no-more-data UI at the bottom.
 */
@Composable
fun MovieList(listState: LazyListState,
              navController: NavController
) {
    val viewModel: MovieViewModel = hiltViewModel()
    val favoriteViewModel: FavoriteViewModel = hiltViewModel()
    val favoriteList by favoriteViewModel.favoriteList.collectAsState()
    val favoriteMap by remember(favoriteList) {
        mutableStateOf(favoriteList.associateBy { it.imdbID })
    }

    val uiState by viewModel.homeUiState.collectAsState()
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .crossfade(true)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    @OptIn(FlowPreview::class)
    LaunchedEffect(Unit) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .debounce(400)
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (
                    !uiState.noMoreData &&
                    !uiState.isLoading &&
                    !uiState.isPaginating &&
                    !uiState.isRefreshing &&
                    uiState.movies.isNotEmpty() &&
                    lastVisibleIndex >= uiState.movies.size - 1 &&
                    viewModel.currentPage > 1 &&
                    listState.firstVisibleItemIndex > 0
                ) {
                    viewModel.loadMoreMovies()
                }
            }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(uiState.movies, key = { movie -> movie.imdbID }) { movie ->
            val isFavorite = favoriteMap.containsKey(movie.imdbID)
            MovieItem(
                movie = movie,
                navController,
                isFavorite = isFavorite,
                onToggleFavorite = { favoriteViewModel.toggleFavorite(movie) },
                imageLoader = imageLoader
            )
        }

        if (uiState.isPaginating) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

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
