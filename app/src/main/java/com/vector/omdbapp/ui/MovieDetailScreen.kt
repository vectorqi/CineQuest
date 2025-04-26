package com.vector.omdbapp.ui

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vector.omdbapp.R
import com.vector.omdbapp.navigation.Screen
import com.vector.omdbapp.util.ErrorState
import com.vector.omdbapp.viewmodel.DetailViewModel

/**
 * A detailed movie screen displaying full information about the movie.
 * The screen supports back navigation, toggling favorites, and viewing the poster in full.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    imdbId: String,
    navController: NavController,
    viewModel: DetailViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.detailUiState.collectAsState()
    val context = LocalContext.current

    // Trigger detail loading when component is first composed
    LaunchedEffect(imdbId) {
        if (viewModel.detailUiState.value.movie == null) {
            viewModel.loadMovieDetail(imdbId)
        }
    }

    if (uiState.isLoading) {
        MovieDetailSkeleton()
    } else {
        uiState.movie?.let { movie ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(300)) + slideInVertically(initialOffsetY = { it / 4 }, animationSpec = tween(300)),
                exit = fadeOut()
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    stringResource(R.string.movie_detail_title),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.back_button_desc)
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { viewModel.toggleFavorite(movie) }) {
                                    Icon(
                                        imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = stringResource(R.string.icon_favor_desc)
                                    )
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp)
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Poster image with clickable navigation to zoom view
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(movie.posterUrl)
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.error)
                                .crossfade(true)
                                .build(),
                            contentDescription = stringResource(R.string.poster_desc),
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val encodedUrl = Uri.encode(movie.posterUrl)
                                    navController.navigate(Screen.Poster.createRoute(encodedUrl))
                                }
                        )
                        Text(
                            text = movie.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(16.dp, 16.dp, 16.dp, 0.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        // Grouped movie detail info
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Section: Basic Info
                            Text(
                                stringResource(R.string.section_basic_info),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            DetailItem(stringResource(R.string.label_year), movie.year.toString())
                            DetailItem(stringResource(R.string.label_rated), movie.rated.toString())
                            DetailItem(
                                stringResource(R.string.label_released),
                                movie.released.toString()
                            )
                            DetailItem(
                                stringResource(R.string.label_runtime),
                                movie.runtime.toString()
                            )
                            DetailItem(stringResource(R.string.label_genre), movie.genre.toString())

                            Spacer(modifier = Modifier.height(8.dp))
                            // Section: Credits
                            Text(
                                stringResource(R.string.section_credits),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            DetailItem(
                                stringResource(R.string.label_director),
                                movie.director.toString()
                            )
                            DetailItem(
                                stringResource(R.string.label_writer),
                                movie.writer.toString()
                            )
                            DetailItem(
                                stringResource(R.string.label_actors),
                                movie.actors.toString()
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            // Section: Description
                            Text(
                                stringResource(R.string.section_description),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            DetailItem(stringResource(R.string.label_plot), movie.plot)

                            Spacer(modifier = Modifier.height(8.dp))
                            // Section: More Details
                            Text(
                                stringResource(R.string.section_details),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            DetailItem(
                                stringResource(R.string.label_language),
                                movie.language.toString()
                            )
                            DetailItem(
                                stringResource(R.string.label_country),
                                movie.country.toString()
                            )
                            DetailItem(
                                stringResource(R.string.label_awards),
                                movie.awards.toString()
                            )
                            DetailItem(
                                stringResource(R.string.label_rating),
                                movie.imdbRating.toString()
                            )
                            DetailItem(
                                stringResource(R.string.label_boxoffice),
                                movie.boxOffice.toString()
                            )
                            DetailItem(
                                stringResource(R.string.label_production),
                                movie.production.toString()
                            )
                        }
                    }
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center

        ) {
            ErrorState(
                message = uiState.errorMessage.toString(),
                onRetry = { viewModel.loadMovieDetail(imdbId) }
            )
        }
    }
}

/**
 * A single labeled detail item with consistent formatting.
 * Used in grouped sections on the detail screen.
 */
@Composable
private fun DetailItem(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.width(120.dp)
        )
        Text(text = if (value.isBlank() || value == "N/A"|| value == "null") stringResource(R.string.unknown_field) else value,
            fontSize = 16.sp)
    }
}
