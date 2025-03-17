package com.vector.omdbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OmdbAppScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmdbAppScreen(viewModel: MovieViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("OMDB Search Demo") })
    }) {
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
                        text = "Error: ${uiState.errorMessage}",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    MovieList(movies = uiState.movies)
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
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            label = { Text("Search movies") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onSearchClick) {
            Text("Search")
        }
    }
}

@Composable
fun MovieList(movies: List<com.vector.omdbapp.data.model.Movie>) {
    LazyColumn(contentPadding = PaddingValues(8.dp)) {
        items(movies) { movie ->
            MovieItem(movie)
            HorizontalDivider(modifier = Modifier.fillMaxWidth(),thickness = 1.dp)
        }
    }
}

@Composable
fun MovieItem(movie: com.vector.omdbapp.data.model.Movie) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = movie.title, style = MaterialTheme.typography.titleSmall)
            Text(text = "Year: ${movie.year}", style = MaterialTheme.typography.bodyMedium)
            // 在这里可以添加一个 Button/CheckBox 模拟点击功能
            // 例如:
            // Button(onClick = { }) { Text("Select") }
        }
    }
}