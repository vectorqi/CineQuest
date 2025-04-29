package com.vector.omdbapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.model.TypeFilter
import com.vector.omdbapp.data.model.YearFilter
import com.vector.omdbapp.data.repository.MovieRepository
import com.vector.omdbapp.ui.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Responsible for managing state, search, pagination,
 * and favorite movie logic. Utilizes Hilt for dependency injection.
 */
@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel()  {
    private val firstPage = 1
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState
    private var _currentPage = firstPage        // Tracks which page we are currently loading
    private var isLoadingPage = false  // Prevents duplicate requests
    val currentPage: Int get() = this._currentPage
    private var currentQuery: String = ""

    /**
     * Initiates a new search, resets pagination state, and loads the first page.
     */
    fun searchMovies() {
        val query = _homeUiState.value.query.trim()
        // Avoid simultaneous requests
        if (isLoadingPage) return
        isLoadingPage = true

        // Reset UI state for a fresh search
        _currentPage = firstPage
        currentQuery = query  // Update the current query
        _homeUiState.update {
            it.copy(
                movies = emptyList(),
                noMoreData = false,
                totalResults = 0,
                errorMessage = null,
                isLoading = true
            )
        }
        // Launch a coroutine to load the first page
        viewModelScope.launch (Dispatchers.IO){
            //Todo: to be deleted after demo
//            delay(500)
            val typeParam = if (_homeUiState.value.selectedType == TypeFilter.ALL) "" else _homeUiState.value.selectedType.value
            val yearParam = if (_homeUiState.value.selectedYear == YearFilter.ALL) "" else _homeUiState.value.selectedYear
            val result = repository.searchMovies(currentQuery,typeParam,yearParam, page = _currentPage)
            // Handle the result
            result.onSuccess { searchResult ->
                val filteredMovies = filterDuplicateMovies(searchResult.movies)
                _homeUiState.update {
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
                _homeUiState.update {
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
        if (isLoadingPage || _homeUiState.value.noMoreData) return
        isLoadingPage = true
        // Set the loading state
        _homeUiState.update { it.copy(isPaginating = true) }
        // Launch a coroutine to load the next page
        viewModelScope.launch(Dispatchers.IO) {
            //Todo: to be deleted after demo
//            delay(500)
            val typeParam = if (_homeUiState.value.selectedType == TypeFilter.ALL) "" else _homeUiState.value.selectedType.value
            val yearParam = if (_homeUiState.value.selectedYear == YearFilter.ALL) "" else _homeUiState.value.selectedYear
            val result = repository.searchMovies(currentQuery,typeParam,yearParam, page = _currentPage)
            result.onSuccess { searchResult ->
                val filteredMovies = filterDuplicateMovies(searchResult.movies)
                _homeUiState.update {
                    it.copy(
                        movies = it.movies + filteredMovies, // Append filtered new items
                        isPaginating = false,
                        totalResults = searchResult.totalResults // Might be the same each time
                    )
                }
                _currentPage++
                checkNoMoreData()
            }.onFailure {
                _homeUiState.update { it.copy(isPaginating = false) }
            }
            isLoadingPage = false
        }
    }
    /**
     * Filters out duplicate movies from the new data since the Server may return duplicate results.
     */
    private fun filterDuplicateMovies(newMovies: List<Movie>): List<Movie> {
        val currentIds = _homeUiState.value.movies.map { it.imdbID }.toSet()
        return newMovies.filter { it.imdbID !in currentIds }
    }
    /**
     * Checks if we've already loaded all data based on totalResults.
     * If movies list size matches or exceeds totalResults, set noMoreData = true.
     */
    private fun checkNoMoreData() {
        val state = _homeUiState.value
        val loadedCount = state.movies.size
        val total = state.totalResults

        // If we've reached or exceeded the total movie count, no more data to load
        if (total in 1..loadedCount) {
            _homeUiState.update { it.copy(noMoreData = true) }
        }
    }
    /**
     * Updates the search query in the UI state.
     */
    fun onQueryChange(newQuery: String) {
        _homeUiState.update { it.copy(query = newQuery) }
    }

    fun onTypeChange(newType: TypeFilter) {
        _homeUiState.update { it.copy(selectedType = newType) }
    }
    fun onYearChange(newYear: String) {
        _homeUiState.update { it.copy(selectedYear = newYear) }
    }
}