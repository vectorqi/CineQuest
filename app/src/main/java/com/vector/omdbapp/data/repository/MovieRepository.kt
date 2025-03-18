package com.vector.omdbapp.data.repository

import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository class responsible for fetching data from the OMDB API,
 * it handles API requests, response parsing, and error handling.
 *
 * NOTE: For demonstration and testing only, the API key is hard-coded here.
 *       In a production environment, do not store API keys directly in the code.
 *       Exposing secrets can lead to security risks and potential abuse of your key.
 */
class MovieRepository {

    /**
     * Hard-coded OMDB API key:
     * - This is convenient for quick demos or local testing.
     * - It is strongly discouraged to commit real or sensitive keys to a public repository.
     * - In production, store the key securely, e.g., using gradle.properties, BuildConfig, or a secure server.
     */
    private val apiKey = "1f4ca266"

    /**
     * Searches for movies in the OMDB API based on a query.
     *
     * @param query The movie title or keyword to search for.
     * @param page The page number for paginated results (default: 1).
     * @return A `Result<List<Movie>>` containing the list of movies on success, or an error message on failure.
     */
    suspend fun searchMovies(query: String, page: Int = 1): Result<List<Movie>> {
        return withContext(Dispatchers.IO) { // Ensure API call is performed on a background thread
            try {
                // Make the network request using Retrofit
                val response = RetrofitClient.api.searchMovies(apiKey, query, page)
                // Check if the API returned a successful response
                if (response.response == "True" && response.movies != null) {
                    Result.success(response.movies) // Return the movie list
                } else {
                    val errorMsg = response.error ?: "No results or error from OMDB"
                    Result.failure(Exception(errorMsg)) // Return error message from API
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to fetch movies: ${e.message}")) // Return network error
            }
        }
    }
}
