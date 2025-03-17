package com.vector.omdbapp.data.repository

import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.model.OmdbSearchResponse
import com.vector.omdbapp.data.remote.RetrofitClient

/**
 * Repository class responsible for fetching data from the OMDB API,
 * handling business logic and returning results to the ViewModel.
 *
 * NOTE: For demonstration and testing only, the API key is hard-coded here.
 *       In a production environment, do not store API keys directly in the code.
 *       Exposing secrets can lead to security risks and potential abuse of your key.
 */
class MovieRepository {

    private val omdbApi = RetrofitClient.api

    /**
     * Hard-coded OMDB API key:
     * - This is convenient for quick demos or local testing.
     * - It is strongly discouraged to commit real or sensitive keys to a public repository.
     * - In production, store your key securely, e.g., using gradle.properties, BuildConfig, or a secure server.
     */
    private val apiKey = "http://www.omdbapi.com/?i=tt3896198&apikey=1f4ca266"

    /**
     * Searches for movies by a query string using the OMDB API.
     * The function is marked 'suspend' so it can be called from a coroutine.
     *
     * @param query The keyword to search for.
     * @return A Result containing either a list of Movie objects on success, or an Exception on failure.
     */
    suspend fun searchMovies(query: String): Result<List<Movie>> {
        return try {
            val response: OmdbSearchResponse = omdbApi.searchMovies(apiKey, query)
            if (response.response == "True" && response.movies != null) {
                Result.success(response.movies)
            } else {
                val errorMsg = response.error ?: "No results or error from OMDB"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            // If there's an exception (e.g., network or parsing failure), wrap it in a failure Result
            Result.failure(e)
        }
    }
}
