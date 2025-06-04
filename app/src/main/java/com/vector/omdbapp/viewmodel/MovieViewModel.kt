package com.vector.omdbapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.model.TypeFilter
import com.vector.omdbapp.data.model.YearFilter
import com.vector.omdbapp.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val loadMutex = Mutex()
    // Tracks which page we are currently loading
    private var _currentPage = firstPage
    val currentPage: Int get() = this._currentPage
    private var currentQuery: String = ""

    // Independent state flows to reduce recomposition impact
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _selectedType = MutableStateFlow(TypeFilter.ALL)
    val selectedType: StateFlow<TypeFilter> = _selectedType

    private val _selectedYear = MutableStateFlow(YearFilter.ALL)
    val selectedYear: StateFlow<String> = _selectedYear

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isPaginating = MutableStateFlow(false)
    val isPaginating: StateFlow<Boolean> = _isPaginating

    private val _noMoreData = MutableStateFlow(false)
    val noMoreData: StateFlow<Boolean> = _noMoreData

    private val _totalResults = MutableStateFlow(0)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        Log.d("FlowChange", "🎯 MovieViewModel initialized @${hashCode()}")
    }
    /**
     * Initiates a new search, resets pagination state, and loads the first page.
     */
    fun searchMovies() {
        val query = _query.value.trim()

        // Reset UI state for a fresh search
        _currentPage = firstPage
        currentQuery = query

        _isLoading.value = true
        _errorMessage.value = null
        _noMoreData.value = false
        _totalResults.value = 0

        // Launch a coroutine to load the first page
        viewModelScope.launch (Dispatchers.IO) {
            if (loadMutex.isLocked) return@launch
            loadMutex.withLock {
                val typeParam = if (_selectedType.value == TypeFilter.ALL) "" else _selectedType.value.value
                val yearParam = if (_selectedYear.value == YearFilter.ALL) "" else _selectedYear.value
                val result = repository.searchMovies(currentQuery, typeParam, yearParam, page = _currentPage)

                result.onSuccess { searchResult ->
                    val filteredMovies = filterDuplicateMovies(searchResult.movies)
                    _movies.value = filteredMovies
                    _totalResults.value = searchResult.totalResults
                    _errorMessage.value = null
                    _isLoading.value = false
                    _currentPage = firstPage + 1

                    // If there's only one result or no result, we might be done immediately
                    checkNoMoreData()
                }.onFailure { throwable ->
                    _movies.value = emptyList()
                    _errorMessage.value = throwable.message
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * Loads additional pages for pagination when the user scrolls to the bottom.
     */
    fun loadMoreMovies() {
        if(_noMoreData.value) return
        _isPaginating.value = true
        // Launch a coroutine to load the next page
        viewModelScope.launch(Dispatchers.IO) {
            if (loadMutex.isLocked) return@launch
            loadMutex.withLock {
                val typeParam = if (_selectedType.value == TypeFilter.ALL) "" else _selectedType.value.value
                val yearParam = if (_selectedYear.value == YearFilter.ALL) "" else _selectedYear.value
                val result =
                    repository.searchMovies(currentQuery, typeParam, yearParam, page = _currentPage)
                result.onSuccess { searchResult ->
                    val filteredMovies = filterDuplicateMovies(searchResult.movies)
                    _movies.value = _movies.value + filteredMovies
                    _totalResults.value = searchResult.totalResults
                    _isPaginating.value = false
                    _currentPage++
                    _currentPage++
                    checkNoMoreData()
                }.onFailure {
                    _isPaginating.value = false
                }
            }
        }
    }
    /**
     * Filters out duplicate movies from the new data since the Server may return duplicate results.
     */
    private fun filterDuplicateMovies(newMovies: List<Movie>): List<Movie> {
        val currentIds = _movies.value.map { it.imdbID }.toSet()
        return newMovies.filter { it.imdbID !in currentIds }
    }
    /**
     * Checks if we've already loaded all data based on totalResults.
     * If movies list size matches or exceeds totalResults, set noMoreData = true.
     */
    private fun checkNoMoreData() {
        val loadedCount = _movies.value.size
        val total = _totalResults.value
        if (total in 1..loadedCount) {
            _noMoreData.value = true
        }
    }
    /**
     * Updates the search query in the UI state.
     */
    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onTypeChange(newType: TypeFilter) {
        _selectedType.value = newType
    }
    fun onYearChange(newYear: String) {
        _selectedYear.value = newYear
    }
}