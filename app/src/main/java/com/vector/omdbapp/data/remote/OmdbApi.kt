package com.vector.omdbapp.data.remote

import com.vector.omdbapp.data.model.OmdbSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApi {
    @GET("/")
    suspend fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") query: String
    ): OmdbSearchResponse
}
