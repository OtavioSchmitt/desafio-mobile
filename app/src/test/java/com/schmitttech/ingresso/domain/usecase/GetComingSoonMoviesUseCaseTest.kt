package com.schmitttech.ingresso.domain.usecase

import com.schmitttech.ingresso.domain.model.Movie
import com.schmitttech.ingresso.domain.repository.MoviesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.OffsetDateTime

class GetComingSoonMoviesUseCaseTest {

    private val repository: MoviesRepository = mockk()
    private val useCase = GetComingSoonMoviesUseCase(repository)

    @Test
    fun `invoke should return movies sorted by premiereDate`() = runTest {
        // Given
        val movie1 = mockMovie(id = "1", date = "2026-05-01T00:00:00+00:00")
        val movie2 = mockMovie(id = "2", date = "2026-04-15T00:00:00+00:00")
        val movie3 = mockMovie(id = "3", date = null)

        coEvery { repository.getComingSoonMovies() } returns Result.success(listOf(movie1, movie2, movie3))

        // When
        val result = useCase().getOrNull()

        // Then
        assertEquals("2", result?.get(0)?.id)
        assertEquals("1", result?.get(1)?.id)
        assertEquals("3", result?.get(2)?.id)
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val exception = Exception("Network Error")
        coEvery { repository.getComingSoonMovies() } returns Result.failure(exception)

        // When
        val result = useCase()

        // Then
        assert(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }



    private fun mockMovie(id: String, date: String?): Movie {
        return Movie(
            id = id,
            title = "Movie $id",
            posterUrl = null,
            premiereDate = date?.let { OffsetDateTime.parse(it) },
            inPreSale = false,
            synopsis = "",
            categories = emptyList(),
            duration = null,
            rating = null
        )
    }
}
