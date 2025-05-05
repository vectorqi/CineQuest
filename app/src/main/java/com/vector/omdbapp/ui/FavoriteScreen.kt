package com.vector.omdbapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vector.omdbapp.R
import com.vector.omdbapp.ui.components.MovieItem
import com.vector.omdbapp.ui.components.MovieItemSkeleton
import com.vector.omdbapp.ui.components.NoMoreDataFooter
import com.vector.omdbapp.util.LocalAppImageLoader
import com.vector.omdbapp.viewmodel.FavoriteViewModel

/**
 * Displays the list of favorite movies or an empty state with a "Browse Movies" action.
 *
 * @param navController Navigation controller for movie detail navigation.
 * @param listState Scroll position of the favorite movie list.
 * @param onBrowseClick Callback invoked when user wants to switch to the Home tab to browse movies.
 */
@Composable
fun FavoriteScreen(
    listState: LazyListState,
    navController: NavHostController,
    onBrowseClick: () -> Unit
) {
    val viewModel: FavoriteViewModel = hiltViewModel()
    val favorites by viewModel.favoriteList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favoriteViewModel: FavoriteViewModel = hiltViewModel()
    val imageLoader = LocalAppImageLoader.current

    LaunchedEffect(Unit) {
        viewModel.loadFavoritesIfNeeded()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(6) {
                        MovieItemSkeleton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(128.dp)
                        )
                    }
                }
            }

            favorites.isEmpty() -> {
                FavoriteEmptyView(onBrowseClick = onBrowseClick)
            }

            else -> {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favorites, key = { it.imdbID }) { movie ->
                        MovieItem(
                            movie = movie,
                            navController = navController,
                            imageLoader = imageLoader,
                            isFavorite = true,
                            onToggleFavorite = { favoriteViewModel.toggleFavorite(movie) }
                        )
                    }
                    item {
                        NoMoreDataFooter()
                    }

                }
            }
        }
    }
}

/**
 * Displays an empty state when there are no favorite movies.
 * Provides a button to navigate back to the Home tab for browsing.
 *
 * @param onBrowseClick Callback invoked when user clicks the "Browse Movies" button.
 */
@Composable
fun FavoriteEmptyView(onBrowseClick: () -> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.run { size(64.dp) },
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.no_favorite_movies),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.go_explore_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onBrowseClick() }
        ) {
            Text(text = stringResource(R.string.browse_movies))
        }
    }
}