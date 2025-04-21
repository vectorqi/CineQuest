package com.vector.omdbapp.data.model

import com.google.gson.annotations.SerializedName
import com.vector.omdbapp.data.db.FavoriteMovieEntity

data class Movie(
    @SerializedName("Title")
    val title: String,

    @SerializedName("Year")
    val year: String,

    @SerializedName("Poster")
    val posterUrl: String,

    @SerializedName("imdbID")
    val imdbID: String,

    @SerializedName("Type")
    val type: String,
)

data class OmdbSearchResponse(
    @SerializedName("Search")
    val movies: List<Movie>?,
    @SerializedName("totalResults")
    val totalResults: String?,
    @SerializedName("Response")
    val response: String?,
    @SerializedName("Error")
    val error: String?
)

fun Movie.toFavoriteEntity(): FavoriteMovieEntity = FavoriteMovieEntity(
    imdbID = imdbID,
    title = title,
    year = year,
    posterUrl = posterUrl,
    type = type
)

fun FavoriteMovieEntity.toDomain(): Movie = Movie(
    imdbID = imdbID,
    title = title,
    year = year,
    posterUrl = posterUrl,
    type = type
)
