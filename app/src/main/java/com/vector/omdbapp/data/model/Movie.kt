package com.vector.omdbapp.data.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("Title")
    val title: String,

    @SerializedName("Year")
    val year: String,

    @SerializedName("Poster")
    val posterUrl: String,

    @SerializedName("imdbID")
    val imdbID: String
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
