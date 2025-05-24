package com.vector.omdbapp.ui.components

import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext

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

    val movies by viewModel.movies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isPaginating by viewModel.isPaginating.collectAsState()
    val noMoreData by viewModel.noMoreData.collectAsState()

    val showSearchHint = remember(movies, isLoading) {
        movies.isEmpty() && !isLoading
    }
    val imageLoader = LocalAppImageLoader.current

    //Handle list scrolling event
    @OptIn(FlowPreview::class)
    LaunchedEffect(Unit) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .debounce(400)
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (
                    !noMoreData &&
                    !isLoading &&
                    !isPaginating &&
                    movies.isNotEmpty() &&
                    lastVisibleIndex >= movies.size - 1 &&
                    viewModel.currentPage > 1 &&
                    listState.firstVisibleItemIndex > 0
                ) {
                    withContext(Dispatchers.IO){
                    //Todo: to be deleted after demo
                    delay(500)
                    viewModel.loadMoreMovies()
                }
                }
            }
    }

    if (showSearchHint) {
        Log.d("MovieList", "SearchHintState 显示了")
        SearchHintState()
    }else {

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            if (movies.isEmpty() && isLoading) {
                items(6) {
                    MovieItemSkeleton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                    )
                }
            } else {
                items(movies, key = { movie -> movie.imdbID }) { movie ->
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

            if (isPaginating) {
                items(2) {
                    MovieItemSkeleton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                    )
                }
            }

            if (noMoreData) {
                item {
                    NoMoreDataFooter()
                }
            }
        }
    }
}
