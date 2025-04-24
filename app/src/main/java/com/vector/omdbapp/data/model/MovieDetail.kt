// File: data/model/MovieDetail.kt
package com.vector.omdbapp.data.model

/**
 * Data class representing detailed information about a movie from OMDb API.
 */
data class MovieDetail(
    val title: String,
    val year: String?,
    val rated: String?,
    val released: String?,
    val runtime: String?,
    val genre: String?,
    val director: String?,
    val writer: String?,
    val actors: String?,
    val plot: String,
    val language: String?,
    val country: String?,
    val awards: String?,
    val posterUrl: String?,
    val imdbRating: String?,
    val imdbID: String,
    val boxOffice: String?,
    val production: String?,
    val totalSeasons: String?,
    val type: String?
)
/**
 * Converts a MovieDetail into a simplified Movie used for search and favorites.
 */
fun MovieDetail.toMovie(): Movie {
    return Movie(
        imdbID = this.imdbID,
        title = this.title,
        year = this.year.toString(),
        posterUrl = this.posterUrl.toString(),
        type = this.type.toString()
    )
}
