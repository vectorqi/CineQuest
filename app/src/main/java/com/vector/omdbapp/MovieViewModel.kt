package com.vector.omdbapp

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Data class representing the UI state for the movie list.
 *
 * @param query The search query entered by the user.
 * @param movies The list of movies retrieved from the API.
 * @param errorMessage Error message if the API request fails.
 * @param isLoading Indicates whether the initial movie search is in progress.
 * @param isPaginating Indicates whether additional movies are being loaded for pagination.
 */
data class MovieUiState(
    val query: String = "",
    val movies: List<Movie> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false
)
/**
 * ViewModel responsible for managing movie-related UI state and business logic.
 * It handles searching movies, maintaining label visibility, and implementing pagination.
 */
class MovieViewModel : ViewModel() {
    private val repository = MovieRepository()
    // StateFlow to hold the UI state, ensuring UI updates reactively.
    private val _uiState = MutableStateFlow(MovieUiState())
    // Publicly exposed immutable StateFlow for UI observation.
    val uiState: StateFlow<MovieUiState> = _uiState
    // MutableStateMap to maintain label visibility states for each movie.
    private val _labelStates = mutableStateMapOf<String, Boolean>()
    // Publicly exposed immutable map for UI observation.
    val labelStates: Map<String, Boolean> get() = _labelStates
    // Pagination state variables
    private var currentPage = 1  // Tracks the current page number for pagination
    private var isLoadingPage = false // Prevents duplicate requests when loading more movies

    /**
     * Toggles the visibility of the label for a given movie.
     *
     * @param movieId The unique identifier of the movie.
     */
    fun toggleLabel(movieId: String) {
        _labelStates[movieId] = !_labelStates.getOrDefault(movieId, false)
    }
    /**
     * Determines the text to display on the toggle label button.
     *
     * @param movieId The unique identifier of the movie.
     * @return "Hide Label" if the label is currently visible, otherwise "Show Label".
     */
    fun getButtonText(movieId: String): String {
        return if (_labelStates.getOrDefault(movieId, false)) "Hide Label" else "Show Label"
    }

    /**
     * Updates the query state and triggers a movie search.
     *
     * @param newQuery The new search term entered by the user.
     */
    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }
    /**
     * Initiates a movie search based on the current query.
     * Resets the movie list and pagination state.
     */
    fun searchMovies() {
        if (isLoadingPage) return // Prevent duplicate loading
        isLoadingPage = true

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
                currentPage = 2 // Reset pagination to page 2 after a new search
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        movies = emptyList(),
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                }
            }
            isLoadingPage = false
        }
    }
    /**
     * Loads additional movies for pagination when the user scrolls to the bottom.
     * Prevents duplicate requests and updates the state accordingly.
     */
    fun loadMoreMovies() {
        if (isLoadingPage) return // Prevents multiple simultaneous requests
        isLoadingPage = true

        viewModelScope.launch {
            _uiState.update { it.copy(isPaginating = true) }
            val currentQuery = _uiState.value.query

            val result = repository.searchMovies(currentQuery, page = currentPage)
            result.onSuccess { movieList ->
                _uiState.update {
                    it.copy(
                        movies = it.movies + movieList, // Append new movies to the existing list
                        isPaginating = false
                    )
                }
                currentPage++ // Increment the page number for next request
            }.onFailure {
                _uiState.update { it.copy(isPaginating = false) }
            }
            isLoadingPage = false
        }
    }
}