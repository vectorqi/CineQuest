package com.vector.omdbapp.ui.components

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vector.omdbapp.R
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.ui.navigation.Screen

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
fun MovieItem(
    movie: Movie,
    navController: NavController,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    imageLoader: ImageLoader
) {
    val context = LocalContext.current
    val posterUrl = if (movie.posterUrl == "N/A") R.drawable.error else movie.posterUrl

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable {
                Log.d("navController", "navController="+navController)
                Log.d("movie", "movie="+movie)
                navController.navigate(Screen.MovieDetail.createRoute(movie.imdbID))
            }
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(posterUrl)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = stringResource(R.string.poster_desc, movie.title),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 96.dp, height = 128.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.year_label) + " " + movie.year,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }

            IconToggleButton(
                checked = isFavorite,
                onCheckedChange = { onToggleFavorite() }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(
                        if (isFavorite) R.string.icon_unfavor_desc else R.string.icon_favor_desc
                    ),
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}