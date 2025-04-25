package com.vector.omdbapp.ui.state

import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.model.TypeFilter
import com.vector.omdbapp.data.model.YearFilter

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
data class HomeUiState(
    val query: String = "",
    val selectedYear: String = YearFilter.ALL,
    val selectedType: TypeFilter = TypeFilter.ALL,// "all" or "" means no filter
    val movies: List<Movie> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val noMoreData: Boolean = false,  // Prevents further loading after final page
    val totalResults: Int = 0
)
