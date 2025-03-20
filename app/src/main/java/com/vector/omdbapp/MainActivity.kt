package com.vector.omdbapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.vector.omdbapp.viewmodel.MovieViewModel
import com.vector.omdbapp.viewmodel.MovieViewModelFactory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val application = applicationContext as Application
            val viewModel: MovieViewModel = viewModel(factory = MovieViewModelFactory(application))
            OmdbAppScreen(viewModel = viewModel)
        }
    }
}
/**
 * For UI verification only
 */
//@Preview
//@Composable
//fun OmdbAppScreenPreview() {
//    OmdbAppScreen()
//}
//
//@Preview
//@Composable
//fun SearchBarPreview() {
//    SearchBar(query = "Movie Title", onQueryChange = {}, onSearchClick = {})
//}
//
//@Preview
//@Composable
//fun MovieListPreview() {
//    MovieList(viewModel = viewModel())
//}
//
//@Preview
//@Composable
//fun MovieItemPreview() {
//    val movie = com.vector.omdbapp.data.model.Movie(
//        title = "Movie 1",
//        year = "2021",
//        imdbID = "12345",
//        posterUrl = "https://www.omdbapi.com/src/poster.jpg",
//        type = "movie"
//    )
//    MovieItem(movie = movie, viewModel = viewModel())
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmdbAppScreen(viewModel: MovieViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Observe snackbar messages from the ViewModel
    LaunchedEffect(Unit) {
        viewModel.snackbarFlow.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }
    // Scaffold with top bar and snackbar
    Scaffold(topBar = {
        TopAppBar(title = { Text(context.getString(R.string.app_title)) })
    },snackbarHost = { SnackbarHost(hostState = snackbarHostState) })
    {
        Column(modifier = Modifier.padding(it).fillMaxSize()) {
            SearchBar(
                query = uiState.query,
                onQueryChange = { newQuery -> viewModel.onQueryChange(newQuery) },
                onSearchClick = { viewModel.searchMovies() }
            )
            when {
                uiState.isLoading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = context.getString(R.string.error_label) + uiState.errorMessage,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    MovieList(viewModel)
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val maxCharacters = 30
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = { newText ->
                // Limit the input to 30 characters
                if (newText.length <= maxCharacters) {
                    onQueryChange(newText)
                }
            },
            modifier = Modifier.weight(1f),
            label = { Text(context.getString(R.string.search_label)) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = {
            keyboardController?.hide()
            onSearchClick()
        }
        ) {
            Text(context.getString(R.string.search_button))
        }
    }
}

@Composable
fun MovieList(viewModel: MovieViewModel) {
    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()
    // LazyListState keeps track of the scroll position in LazyColumn
    val listState = rememberLazyListState()
    val context = LocalContext.current
    /**
     * Scroll detection logic:
     * - This effect monitors the scrolling state of LazyColumn.
     * - It uses snapshotFlow to continuously observe the last visible item index.
     * - If the user scrolls to the last item, it triggers the `loadMoreMovies()` function in the ViewModel.
     */
    @OptIn(FlowPreview::class)
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .debounce(200)//200 milliseconds throttle
            .collect { lastVisibleIndex ->
                // Only attempt to load more if we haven't exhausted data
                if (!uiState.noMoreData && lastVisibleIndex != null
                    && lastVisibleIndex >= uiState.movies.size - 1
                ) {
                    viewModel.loadMoreMovies()
                }
            }
    }

    /**
     * LazyColumn displays the list of movies in a scrollable column.
     * - `state = listState` ensures we track scroll position.
     * - `contentPadding` provides space around the edges.
     * - `verticalArrangement.spacedBy(8.dp)` adds spacing between items.
     */
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        /**
         * Display each movie item in the list.
         * - `items(uiState.movies)` iterates over the movie list.
         * - Each movie is displayed using the `MovieItem` composable.
         * - A `Divider()` is added to separate items visually.
         */
        items(uiState.movies, key = { movie -> movie.imdbID }) { movie ->
            MovieItem(movie = movie, viewModel = viewModel)
            HorizontalDivider(modifier = Modifier.fillMaxWidth(),thickness = 1.dp)
        }
        /**
         * Display a loading indicator when paginating (fetching more movies).
         * - This item appears at the bottom when `isPaginating` is `true`.
         * - It provides a visual cue to users that more data is loading.
         */
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
@Composable
fun MovieItem(movie: com.vector.omdbapp.data.model.Movie, viewModel: MovieViewModel) {
    val isLabelVisible = viewModel.labelStates[movie.imdbID] == true
    val buttonText = viewModel.getButtonText(movie.imdbID)
    val context = LocalContext.current
    val customImageLoader = ImageLoader.Builder(context)
        .crossfade(true)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Provide a placeholder image if the poster is loading or fails
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.posterUrl)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .size(128, 192)

                .build(),
            imageLoader = customImageLoader,
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
                text = context.getString(R.string.year_label) + movie.year,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (isLabelVisible) {
                Text(
                    text = context.getString(R.string.label_displayed),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Button(onClick = { viewModel.toggleLabel(movie.imdbID) }) {
            Text(buttonText)
        }
    }
}