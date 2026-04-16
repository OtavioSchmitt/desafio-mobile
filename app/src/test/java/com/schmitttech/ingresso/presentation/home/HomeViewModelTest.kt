package com.schmitttech.ingresso.presentation.home

import com.schmitttech.ingresso.domain.usecase.GetComingSoonMoviesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val getComingSoonMoviesUseCase: GetComingSoonMoviesUseCase = mockk()
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should fetch movies and update state to Success`() = runTest {
        // Given
        val movies = emptyList<com.schmitttech.ingresso.domain.model.Movie>()
        coEvery { getComingSoonMoviesUseCase() } returns Result.success(movies)

        // When
        viewModel = HomeViewModel(getComingSoonMoviesUseCase)

        // Then
        assert(viewModel.uiState.value is HomeUiState.Success)
        assertEquals(movies, (viewModel.uiState.value as HomeUiState.Success).movies)
    }

    @Test
    fun `init should fetch movies and update state to Error on failure`() = runTest {
        // Given
        val errorMessage = "Network Error"
        coEvery { getComingSoonMoviesUseCase() } returns Result.failure(Exception(errorMessage))

        // When
        viewModel = HomeViewModel(getComingSoonMoviesUseCase)

        // Then
        assert(viewModel.uiState.value is HomeUiState.Error)
        assertEquals(errorMessage, (viewModel.uiState.value as HomeUiState.Error).message)
    }
}
