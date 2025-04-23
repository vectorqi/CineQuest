package com.vector.omdbapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vector.omdbapp.data.db.FavoriteMovieDao
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.model.toDomain
import com.vector.omdbapp.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing user's favorite movies.
 * It provides reactive state for UI and interacts with the DAO layer.
 */
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteMovieDao: FavoriteMovieDao,
    private val repository: MovieRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _favoriteList = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteList: StateFlow<List<Movie>> = _favoriteList.asStateFlow()

    init {
        // Start collecting the favorite list from Room database
        viewModelScope.launch {
            favoriteMovieDao.getAllFavorites()
                .map { list -> list.map { it.toDomain() } }
                .catch {  // catch any unexpected error from DAO
                    _favoriteList.value = emptyList()  // Fallback to empty
                    _isLoading.value = false
                }
                .collect { movies ->
                    _favoriteList.value = movies
                    _isLoading.value = false
                }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            val isAlreadyFavorite = repository.isFavorite(movie.imdbID)

            if (isAlreadyFavorite) {
                repository.removeFavorite(movie.imdbID)
            } else {
                repository.addFavorite(movie)
            }
        }
    }
}