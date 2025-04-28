package com.vector.omdbapp.viewmodel

import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.remote.response.MovieSearchResponse
import com.vector.omdbapp.data.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit test for [MovieViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModelTest {
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MovieViewModel
    private lateinit var repository: MovieRepository

    private val movie = Movie("Batman", "2023", "https://poster.jpg", "t12332", "movie")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = MovieViewModel(repository)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain() // very important to avoid memory leaks
    }

    @Test
    fun `searchMovies updates ui state on success`() = runTest {
        coEvery { repository.searchMovies("Batman", any(), any(), any()) } returns Result.success(
            MovieSearchResponse(listOf(movie), 1)
        )

        viewModel.onQueryChange("Batman")
        viewModel.searchMovies()

        advanceUntilIdle() // ensure coroutine finished

        val finalState = viewModel.homeUiState.value
        assert(!finalState.isLoading)
        assert(finalState.movies.isNotEmpty())
        assert(finalState.movies[0].title == "Batman")
    }

    @Test
    fun `searchMovies updates ui state on failure`() = runTest {
        coEvery { repository.searchMovies(any(), any(), any(), any()) } returns Result.failure(Exception("API failed"))

        viewModel.onQueryChange("Batman")
        viewModel.searchMovies()
        advanceUntilIdle() // ensure coroutine finished
        val finalState = viewModel.homeUiState.value
        assert(!finalState.isLoading)
        assert(finalState.errorMessage == "API failed")
        }
}