package com.vector.omdbapp.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the UI state, including movies, query text, and
 * status flags for loading, pagination, etc.
 *
 * @param query Current search keyword.
 * @param movies The current list of displayed movies.
 * @param errorMessage Error message for any failed requests.
 * @param isLoading Indicates the initial (main) loading is in progress.
 * @param isPaginating Indicates if a subsequent "load more" request is in progress.
 * @param noMoreData True when we've loaded all possible data.
 * @param totalResults Number of total hits reported by OMDB.
 */


data class MovieUiState(
    val query: String = "",
    val movies: List<Movie> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val noMoreData: Boolean = false,  // Prevents further loading after final page
    val totalResults: Int = 0
)

/**
 * ViewModel manages the search, pagination, and label states.
 */
@HiltViewModel
class MovieViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel()  {

    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState

    private val _labelStates = mutableStateMapOf<String, Boolean>()
    val labelStates: Map<String, Boolean> get() = _labelStates

    private var currentPage = 1        // Tracks which page we are currently loading
    private var isLoadingPage = false  // Prevents duplicate requests
    private val _snackbarChannel = Channel<String>(Channel.BUFFERED)  // Channel for snackbar messages
    val snackbarFlow = _snackbarChannel.receiveAsFlow()  // Convert the channel to a flow
    private var currentQuery: String = ""

    /**
     * Toggles a movie's label visibility.
     */
    fun toggleLabel(movieId: String) {
        _labelStates[movieId] = !_labelStates.getOrDefault(movieId, false)
    }

    /**
     * Determines the button text for showing/hiding labels.
     */
    fun getButtonText(movieId: String, showText: String, hideText:String): String {
        return if (_labelStates.getOrDefault(movieId, false)) hideText else showText
    }

    /**
     * Updates the search query in the UI state.
     */
    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    /**
     * Initiates a new search, resets pagination state, and loads the first page.
     */
    fun searchMovies(emptyQueryMessage: String) {
        val query = _uiState.value.query.trim()
        // Avoid empty queries
        if (query.isEmpty()) {
            viewModelScope.launch {
                _snackbarChannel.send(emptyQueryMessage)
            }
            return
        }
        // Avoid simultaneous requests
        if (isLoadingPage) return
        isLoadingPage = true

        // Reset UI state for a fresh search
        currentPage = 1
        currentQuery = query  // Update the current query
        _uiState.update {
            it.copy(
                movies = emptyList(),
                noMoreData = false,
                totalResults = 0,
                errorMessage = null,
                isLoading = true
            )
        }
        // Launch a coroutine to load the first page
        viewModelScope.launch {
            val currentQuery = _uiState.value.query
            val result = repository.searchMovies(currentQuery, page = currentPage)
            // Handle the result
            result.onSuccess { searchResult ->
                val filteredMovies = filterDuplicateMovies(searchResult.movies)
                _uiState.update {
                    it.copy(
                        movies = filteredMovies,
                        isLoading = false,
                        totalResults = searchResult.totalResults,
                        errorMessage = null
                    )
                }
                currentPage = 2

                // If there's only one result or no result, we might be done immediately
                checkNoMoreData()
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
     * Loads additional pages for pagination when the user scrolls to the bottom.
     */
    fun loadMoreMovies() {
        // Block further loading if we're already loading a page or no more data is available
        if (isLoadingPage || _uiState.value.noMoreData) return
        isLoadingPage = true
        // Set the loading state
        _uiState.update { it.copy(isPaginating = true) }
        // Launch a coroutine to load the next page
        viewModelScope.launch {
            val result = repository.searchMovies(currentQuery, page = currentPage)
            result.onSuccess { searchResult ->
                val filteredMovies = filterDuplicateMovies(searchResult.movies)
                _uiState.update {
                    it.copy(
                        movies = it.movies + filteredMovies, // Append filtered new items
                        isPaginating = false,
                        totalResults = searchResult.totalResults // Might be the same each time
                    )
                }
                currentPage++
                checkNoMoreData()
            }.onFailure {
                _uiState.update { it.copy(isPaginating = false) }
            }
            isLoadingPage = false
        }
    }
    /**
     * Filters out duplicate movies from the new data since the Server may return duplicate results.
     */
    private fun filterDuplicateMovies(newMovies: List<Movie>): List<Movie> {
        val currentIds = _uiState.value.movies.map { it.imdbID }.toSet()
        return newMovies.filter { it.imdbID !in currentIds }
    }
    /**
     * Checks if we've already loaded all data based on totalResults.
     * If movies list size matches or exceeds totalResults, set noMoreData = true.
     */
    private fun checkNoMoreData() {
        val state = _uiState.value
        val loadedCount = state.movies.size
        val total = state.totalResults

        // If we've reached or exceeded the total movie count, no more data to load
        if (total in 1..loadedCount) {
            _uiState.update { it.copy(noMoreData = true) }
        }
    }
}