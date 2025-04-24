package com.vector.omdbapp.data.remote.response

import com.vector.omdbapp.data.model.Movie

/**
 * Represents the combined result of a movie search API call,
 * including the list of movies and the total number of results
 * (as reported by the OMDB API).
 */
data class MovieSearchResponse(
    val movies: List<Movie>,
    val totalResults: Int
)
