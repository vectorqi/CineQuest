package com.vector.omdbapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieUiState(
    val query: String = "",
    val movies: List<Movie> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

class MovieViewModel : ViewModel() {

    private val repository = MovieRepository()

    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun searchMovies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val currentQuery = _uiState.value.query
            val result = repository.searchMovies(currentQuery)
            result.onSuccess { movieList ->
                _uiState.update {
                    it.copy(
                        movies = movieList,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        movies = emptyList(),
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                }
            }
        }
    }
}