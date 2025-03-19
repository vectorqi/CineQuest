package com.vector.omdbapp.data.model

/**
 * Represents the combined result of a movie search API call,
 * including the list of movies and the total number of results
 * (as reported by the OMDB API).
 */
data class MovieSearchResult(
    val movies: List<Movie>,
    val totalResults: Int
)
