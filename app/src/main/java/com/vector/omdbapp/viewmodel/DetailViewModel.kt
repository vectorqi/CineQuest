package com.vector.omdbapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vector.omdbapp.data.model.MovieDetail
import com.vector.omdbapp.data.model.toMovie
import com.vector.omdbapp.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state representing movie details and favorite status.
 */
data class DetailUiState(
    val isLoading: Boolean = true,
    val movie: MovieDetail? = null,
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for MovieDetailScreen, responsible for loading movie detail data
 * and handling favorite toggle logic.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _detailUiState = MutableStateFlow(DetailUiState())
    val detailUiState: StateFlow<DetailUiState> = _detailUiState.asStateFlow()

    /**
     * Loads full movie details by IMDB ID from the repository.
     */
    fun loadMovieDetail(imdbId: String) {
        viewModelScope.launch {
            _detailUiState.value = DetailUiState(isLoading = true)

            val result = repository.getMovieDetail(imdbId)
            result.onSuccess { detail ->
                val isFav = repository.isFavorite(imdbId)
                _detailUiState.value = DetailUiState(
                    isLoading = false,
                    movie = detail,
                    isFavorite = isFav
                )
            }.onFailure { error ->
                _detailUiState.value = DetailUiState(
                    isLoading = false,
                    errorMessage = error.message
                )
            }
        }
    }

    /**
     * Toggle favorite status of the current movie.
     */
    fun toggleFavorite(movieDetail: MovieDetail) {
        viewModelScope.launch {
            val isFav = repository.isFavorite(movieDetail.imdbID)
            if (isFav) {
                repository.removeFavorite(movieDetail.imdbID)
            } else {
                repository.addFavorite(movieDetail.toMovie())
            }
            _detailUiState.update { it.copy(isFavorite = !isFav) }
        }
    }
}
