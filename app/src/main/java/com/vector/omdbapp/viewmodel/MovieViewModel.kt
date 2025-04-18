package com.vector.omdbapp.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.model.TypeFilter
import com.vector.omdbapp.data.model.YearFilter
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
    val selectedYear: String = YearFilter.ALL,
    val selectedType: TypeFilter  = TypeFilter.ALL,// "all" or "" means no filter
    val movies: List<Movie> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val isRefreshing: Boolean = false,
    val noMoreData: Boolean = false,  // Prevents further loading after final page
    val totalResults: Int = 0
)

/**
 * ViewModel manages the search, pagination, and label states.
 */
@HiltViewModel
class MovieViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel()  {
    private val firstPage = 1
    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState

    private val _favorStates = mutableStateMapOf<String, Boolean>()
    val favorStates: Map<String, Boolean> get() = _favorStates

    private var _currentPage = firstPage        // Tracks which page we are currently loading
    private var isLoadingPage = false  // Prevents duplicate requests
    private val _snackbarChannel = Channel<String>(Channel.BUFFERED)  // Channel for snackbar messages
    val currentPage: Int get() = this._currentPage
    val snackbarFlow = _snackbarChannel.receiveAsFlow()  // Convert the channel to a flow
    private var currentQuery: String = ""

    /**
     * Toggles a movie's favorite icon.
     */
    fun toggleFavorite(movieId: String) {
        _favorStates[movieId] = !_favorStates.getOrDefault(movieId, false)
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
        _currentPage = firstPage
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
            val typeParam = if (_uiState.value.selectedType == TypeFilter.ALL) "" else _uiState.value.selectedType.value
            val yearParam = if (_uiState.value.selectedYear == YearFilter.ALL) "" else _uiState.value.selectedYear

            val result = repository.searchMovies(currentQuery,typeParam,yearParam, page = _currentPage)
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
                _currentPage = firstPage+1

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

    fun refreshMovies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            val query = _uiState.value.query.trim()
            val typeParam = if (_uiState.value.selectedType == TypeFilter.ALL) "" else _uiState.value.selectedType.value
            val yearParam = if (_uiState.value.selectedYear == YearFilter.ALL) "" else _uiState.value.selectedYear

            val result = repository.searchMovies(query,typeParam, yearParam,firstPage)
            result.onSuccess { searchResult ->
                _uiState.update {
                    it.copy(
                        movies = searchResult.movies,
                        totalResults = searchResult.totalResults,
                        isRefreshing = false,
                        isPaginating = false,
                        errorMessage = null,
                        noMoreData = false,
                    )
                }
                _currentPage = firstPage+1
                checkNoMoreData()
            }.onFailure {throwable ->
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        isPaginating = false,
                        errorMessage = throwable.message
                    )
                }
            }
        }
    }

    fun onTypeChange(newType: TypeFilter) {
        _uiState.update { it.copy(selectedType = newType) }
    }
    fun onYearChange(newYear: String) {
        _uiState.update { it.copy(selectedYear = newYear) }
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
            val typeParam = if (_uiState.value.selectedType == TypeFilter.ALL) "" else _uiState.value.selectedType.value
            val yearParam = if (_uiState.value.selectedYear == YearFilter.ALL) "" else _uiState.value.selectedYear
            val result = repository.searchMovies(currentQuery,typeParam,yearParam, page = _currentPage)
            result.onSuccess { searchResult ->
                val filteredMovies = filterDuplicateMovies(searchResult.movies)
                _uiState.update {
                    it.copy(
                        movies = it.movies + filteredMovies, // Append filtered new items
                        isPaginating = false,
                        totalResults = searchResult.totalResults // Might be the same each time
                    )
                }
                _currentPage++
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