package com.schmitttech.ingresso.domain.usecase

import com.schmitttech.ingresso.domain.model.Movie
import com.schmitttech.ingresso.domain.repository.MoviesRepository
import java.time.OffsetDateTime
import kotlin.comparisons.compareBy
import kotlin.comparisons.nullsLast

/**
 * UseCase responsible for fetching coming soon movies and applying the necessary sorting.
 */
class GetComingSoonMoviesUseCase(
    private val repository: MoviesRepository
) {
    /**
     * Fetches movies and sorts them by premiere date (closest first).
     * Movies without a premiere date are placed at the end of the list.
     */
    suspend operator fun invoke(): Result<List<Movie>> {
        return repository.getComingSoonMovies().map { movies ->
            movies.sortedWith(
                compareBy(nullsLast<OffsetDateTime>()) { it.premiereDate }
            )
        }
    }
}


