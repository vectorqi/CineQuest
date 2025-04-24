package com.vector.omdbapp.data.remote.response

import com.google.gson.annotations.SerializedName
import com.vector.omdbapp.data.model.MovieDetail

/**
 * Raw response for a movie's detail from OMDb API.
 */
data class MovieDetailResponse(
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: String,
    @SerializedName("Rated") val rated: String,
    @SerializedName("Released") val released: String,
    @SerializedName("Runtime") val runtime: String,
    @SerializedName("Genre") val genre: String,
    @SerializedName("Director") val director: String,
    @SerializedName("Writer") val writer: String,
    @SerializedName("Actors") val actors: String,
    @SerializedName("Plot") val plot: String,
    @SerializedName("Language") val language: String,
    @SerializedName("Country") val country: String,
    @SerializedName("Awards") val awards: String,
    @SerializedName("Poster") val posterUrl: String,
    @SerializedName("imdbRating") val imdbRating: String,
    @SerializedName("BoxOffice") val boxOffice: String,
    @SerializedName("Production") val production: String,
    @SerializedName("imdbID") val imdbID: String,
    @SerializedName("totalSeasons") val totalSeasons: String,
    @SerializedName("Type") val type: String,
    @SerializedName("Response") val response: String,
    @SerializedName("Error") val error: String? = null
) {
    fun toDomain(): MovieDetail = MovieDetail(
        title = title,
        year = year,
        rated = rated,
        released = released,
        runtime = runtime,
        genre = genre,
        director = director,
        writer = writer,
        actors = actors,
        plot = plot,
        language = language,
        country = country,
        awards = awards,
        posterUrl = posterUrl,
        imdbRating = imdbRating,
        boxOffice = boxOffice,
        production = production,
        imdbID = imdbID,
        totalSeasons = totalSeasons,
        type = type
    )
}
