package com.schmitttech.ingresso.data.repository

import com.schmitttech.ingresso.data.remote.api.IngressoApi
import com.schmitttech.ingresso.data.remote.model.MoviesResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MoviesRepositoryImplTest {

    private val api: IngressoApi = mockk()
    private val repository = MoviesRepositoryImpl(api)

    @Test
    fun `getComingSoonMovies should return success when api call succeeds`() = runTest {
        // Given
        val moviesResponse = MoviesResponse(items = emptyList())
        coEvery { api.getComingSoonMovies() } returns moviesResponse

        // When
        val result = repository.getComingSoonMovies()

        // Then
        assert(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
    }

    @Test
    fun `getComingSoonMovies should return failure when api call throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Network Error")
        coEvery { api.getComingSoonMovies() } throws exception

        // When
        val result = repository.getComingSoonMovies()

        // Then
        assert(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
