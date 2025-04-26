package com.vector.omdbapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vector.omdbapp.util.LocalAppImageLoader
import com.vector.omdbapp.viewmodel.FavoriteViewModel
import com.vector.omdbapp.viewmodel.MovieViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun MovieList(
    listState: LazyListState,
    navController: NavController
) {
    val viewModel: MovieViewModel = hiltViewModel()
    val favoriteViewModel: FavoriteViewModel = hiltViewModel()
    val favoriteList by favoriteViewModel.favoriteList.collectAsState()
    val favoriteMap by remember(favoriteList) {
        mutableStateOf(favoriteList.associateBy { it.imdbID })
    }

    val uiState by viewModel.homeUiState.collectAsState()
    val imageLoader = LocalAppImageLoader.current

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
                    uiState.movies.isNotEmpty() &&
                    lastVisibleIndex >= uiState.movies.size - 1 &&
                    viewModel.currentPage > 1 &&
                    listState.firstVisibleItemIndex > 0
                ) {
                    //Todo: to be deleted after demo
                    delay(500)
                    viewModel.loadMoreMovies()
                }
            }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        if (uiState.movies.isEmpty() && uiState.isLoading) {
            items(6) {
                MovieItemSkeleton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                )
            }
        } else {
            items(uiState.movies, key = { movie -> movie.imdbID }) { movie ->
                val isFavorite = favoriteMap.containsKey(movie.imdbID)
                MovieItem(
                    movie = movie,
                    navController = navController,
                    isFavorite = isFavorite,
                    onToggleFavorite = { favoriteViewModel.toggleFavorite(movie) },
                    imageLoader = imageLoader
                )
            }
        }

        if (uiState.isPaginating) {
            items(2) {
                MovieItemSkeleton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                )
            }
        }

        if (uiState.noMoreData) {
            item {
                NoMoreDataFooter()
            }
        }
    }
}
