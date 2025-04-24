package com.vector.omdbapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vector.omdbapp.R
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.navigation.Screen

/**
 * MovieItem.kt
 *
 * Composable representing a single movie item in the list.
 * Displays poster, title, year, and a toggleable favorite icon.
 * All states are passed in from the parent to reduce recomposition and decouple logic.
 *
 * @param movie The movie object to be displayed.
 * @param isFavorite Whether the movie is currently in the favorites list.
 * @param onToggleFavorite Callback function to toggle favorite status.
 */
@Composable
fun MovieItem(movie: Movie,
              navController: NavController,
              isFavorite: Boolean,
              onToggleFavorite: () -> Unit,
              imageLoader: ImageLoader
              ) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .clickable {
                navController.navigate(Screen.MovieDetail.createRoute(movie.imdbID))
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(movie.posterUrl)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .size(128, 192)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = movie.title,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.year_label) + movie.year,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavorite)
                        stringResource(R.string.icon_unfavor_desc)
                    else
                        stringResource(R.string.icon_favor_desc),
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}