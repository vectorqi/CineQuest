package com.vector.omdbapp.data.remote

import com.vector.omdbapp.data.model.OmdbSearchResponse
import com.vector.omdbapp.data.remote.response.MovieDetailResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API interface for fetching movie data from the OMDB API.
 */
interface OmdbApi {
    /**
     * Searches for movies using the OMDB API.
     *
     * @param apiKey The API key for authentication.
     * @param query The movie title or keyword to search for.
     * @param page The page number for paginated results.
     * @return An `OmdbSearchResponse` object containing the search results.
     */
    @GET("/")
    suspend fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") query: String,
        @Query("t") type: String,
        @Query("y") year: String,
        @Query("page") page: Int
    ): OmdbSearchResponse

    // Movie detail
    @GET(".")
    suspend fun getMovieDetail(
        @Query("apikey") apiKey: String,
        @Query("i") imdbId: String,
        @Query("plot") plot: String = "full"
    ): MovieDetailResponse
}
