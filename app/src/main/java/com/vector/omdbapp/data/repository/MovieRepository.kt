package com.vector.omdbapp.data.repository

import com.vector.omdbapp.data.db.FavoriteMovieDao
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.model.MovieDetail
import com.vector.omdbapp.data.remote.response.MovieSearchResponse
import com.vector.omdbapp.data.model.toFavoriteEntity
import com.vector.omdbapp.data.remote.OmdbApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class responsible for fetching data from the OMDB API,
 * it handles API requests, response parsing, and error handling.
 *
 * NOTE: For demonstration and testing only, the API key is hard-coded here.
 *       In a production environment, do not store API keys directly in the code.
 *       Exposing secrets can lead to security risks and potential abuse of your key.
 */
@Singleton
class MovieRepository @Inject constructor(
    private val api: OmdbApi,
    private val favoriteMovieDao: FavoriteMovieDao){

    /**
     * Hard-coded OMDB API key:
     * - This is convenient for quick demos or local testing.
     * - It is strongly discouraged to commit real or sensitive keys to a public repository.
     * - In production, store the key securely, e.g., using gradle.properties, BuildConfig, or a secure server.
     */
    private val apiKey = "1f4ca266"

    /**
     * Searches for movies by query and page number using the OMDB API.
     *
     * @param query The search keyword/title.
     * @param page The page number for pagination.
     * @return A Result wrapping MovieSearchResult on success, or an Exception on failure.
     */
    suspend fun searchMovies(query: String, type: String, year: String, page: Int = 1): Result<MovieSearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.searchMovies(apiKey, query, type, year, page)
                if (response.response == "True" && response.movies != null) {
                    // Parse totalResults; if null or invalid, default to 0
                    val totalCount = response.totalResults?.toIntOrNull() ?: 0
                    // Wrap the movies and total count in a MovieSearchResult
                    val searchResult = MovieSearchResponse(
                        movies = response.movies,
                        totalResults = totalCount
                    )
                    Result.success(searchResult)
                } else {
                    val errorMsg = response.error ?: "No results or error from OMDB"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to fetch movies: ${e.message}"))
            }
        }
    }

    suspend fun getMovieDetail(imdbId: String): Result<MovieDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getMovieDetail(apiKey, imdbId, "full")
                if (response.response == "True") {
                    Result.success(response.toDomain())
                } else {
                    Result.failure(Exception(response.error ?: "Failed to fetch movie detail"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun isFavorite(imdbID: String): Boolean {
        return favoriteMovieDao.isFavorite(imdbID)
    }

    suspend fun addFavorite(movie: Movie) {
        favoriteMovieDao.addFavorite(movie.toFavoriteEntity())
    }

    suspend fun removeFavorite(imdbID: String) {
        favoriteMovieDao.removeFavoriteById(imdbID)
    }
}