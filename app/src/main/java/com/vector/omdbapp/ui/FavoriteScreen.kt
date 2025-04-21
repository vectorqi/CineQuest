package com.vector.omdbapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vector.omdbapp.R
import com.vector.omdbapp.viewmodel.MovieViewModel

/**
 * This composable displays a list of all favorite movies added by the user.
 * It listens to the favoriteList state from the ViewModel and updates automatically.
 * If no movies are favorited, it shows a placeholder message.
 */
@Composable
fun FavoriteScreen(viewModel: MovieViewModel = hiltViewModel()) {
    val favoriteMovies by viewModel.favoriteList.collectAsState()

    // If the list is empty, show a placeholder message
    if (favoriteMovies.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.no_favorite_movies),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        // Display favorite movies in a scrollable list
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favoriteMovies) { movie ->
                FavoriteMovieItem(movie = movie, viewModel)
            }
        }
    }
}