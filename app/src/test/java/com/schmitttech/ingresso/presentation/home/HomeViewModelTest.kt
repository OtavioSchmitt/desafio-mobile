package com.schmitttech.ingresso.presentation.home

import com.schmitttech.ingresso.domain.model.Movie
import com.schmitttech.ingresso.domain.usecase.GetComingSoonMoviesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val useCase: GetComingSoonMoviesUseCase = mockk()
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

    /**
     * SharingStarted.WhileSubscribed requires an active collector.
     * We launch a collect coroutine within the test scope to activate the stateIn flow.
     */
    @Test
    fun `init should transition to Success state after data is loaded`() = runTest {
        // Given
        val movies = listOf(mockMovie("1"), mockMovie("2"))
        coEvery { useCase() } returns Result.success(movies)

        viewModel = HomeViewModel(useCase)

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertEquals(2, (state as HomeUiState.Success).movies.size)

        collectJob.cancel()
    }

    @Test
    fun `init should transition to Error state on network failure`() = runTest {
        // Given
        val errorMessage = "Network Error"
        coEvery { useCase() } returns Result.failure(Exception(errorMessage))

        viewModel = HomeViewModel(useCase)

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Error)
        assertEquals(errorMessage, (state as HomeUiState.Error).message)

        collectJob.cancel()
    }

    @Test
    fun `onGenreSelected should filter movies by genre`() = runTest {
        // Given
        val movie1 = mockMovie("1", genres = listOf("Ação"))
        val movie2 = mockMovie("2", genres = listOf("Drama"))
        coEvery { useCase() } returns Result.success(listOf(movie1, movie2))

        viewModel = HomeViewModel(useCase)
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

        // When
        viewModel.onGenreSelected("Ação")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value as? HomeUiState.Success
        assertEquals(1, state?.movies?.size)
        assertEquals("1", state?.movies?.first()?.id)

        collectJob.cancel()
    }

    private fun mockMovie(
        id: String,
        title: String = "Movie $id",
        genres: List<String> = listOf("Ação")
    ) = Movie(
        id = id,
        title = title,
        posterUrl = null,
        premiereDate = null,
        inPreSale = false,
        synopsis = "",
        categories = genres,
        duration = null,
        rating = null
    )
}
