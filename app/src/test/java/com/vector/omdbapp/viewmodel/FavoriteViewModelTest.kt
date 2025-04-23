package com.vector.omdbapp.viewmodel

import app.cash.turbine.test
import com.vector.omdbapp.data.db.FavoriteMovieDao
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.model.toFavoriteEntity
import com.vector.omdbapp.data.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
 * Unit test for [FavoriteViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var dao: FavoriteMovieDao
    private lateinit var repository: MovieRepository

    private val sampleMovie = Movie(
        imdbID = "tt1234567",
        title = "Test Movie",
        year = "2023",
        posterUrl = "https://poster.jpg",
        type = "movie"
    )
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = mockk()
        repository = mockk(relaxed = true)

        // Mock DAO return list
        coEvery { dao.getAllFavorites() } returns flowOf(listOf(sampleMovie.toFavoriteEntity()))

        viewModel = FavoriteViewModel(dao, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `favorite list emits correct data`() = runTest {
        advanceUntilIdle() // Wait for viewModel init block to complete

        viewModel.favoriteList.test {
            val list = awaitItem()
            assert(list.isNotEmpty())
            assert(list.first().title == "Test Movie")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleFavorite removes if already favorite`() = runTest {
        coEvery { repository.isFavorite(sampleMovie.imdbID) } returns true

        viewModel.toggleFavorite(sampleMovie)
        advanceUntilIdle()

        coVerify { repository.removeFavorite(sampleMovie.imdbID) }
    }

    @Test
    fun `toggleFavorite adds if not favorite`() = runTest {
        coEvery { repository.isFavorite(sampleMovie.imdbID) } returns false

        viewModel.toggleFavorite(sampleMovie)
        advanceUntilIdle()

        coVerify { repository.addFavorite(sampleMovie) }
    }

    @Test
    fun `favorite list emits empty when DAO returns no data`() = runTest {
        // Override DAO to return empty list
        coEvery { dao.getAllFavorites() } returns flowOf(emptyList())

        viewModel = FavoriteViewModel(dao, repository) // Re-init with new DAO
        advanceUntilIdle()

        viewModel.favoriteList.test {
            val list = awaitItem()
            assert(list.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }

        assert(!viewModel.isLoading.value)
    }

    @Test
    fun `favorite list remains empty on DAO error`() = runTest {
        // Simulate exception in DAO flow
        coEvery { dao.getAllFavorites() } returns flow {
            throw RuntimeException("Database error")
        }

        viewModel = FavoriteViewModel(dao, repository)
        advanceUntilIdle()

        viewModel.favoriteList.test {
            val list = awaitItem()
            assert(list.isEmpty()) // fallback to empty list on error
            cancelAndIgnoreRemainingEvents()
        }

        assert(!viewModel.isLoading.value) // might still be loading if exception not caught
    }
}
