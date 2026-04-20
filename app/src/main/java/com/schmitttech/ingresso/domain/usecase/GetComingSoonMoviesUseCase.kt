package com.schmitttech.ingresso.domain.usecase

import com.schmitttech.ingresso.domain.model.Movie
import com.schmitttech.ingresso.domain.repository.MoviesRepository
import kotlin.comparisons.compareBy
import kotlin.comparisons.nullsLast

/**
 * UseCase responsible for fetching coming soon movies and applying the necessary sorting.
 */
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetComingSoonMoviesUseCase(
    private val repository: MoviesRepository
) {
    /**
     * Observes movies and sorts them by premiere date (closest first).
     * Movies without a premiere date are placed at the end of the list.
     */
    operator fun invoke(): Flow<List<Movie>> {
        return repository.observeMovies().map { movies ->
            movies.sortedWith(
                compareBy(nullsLast()) { it.premiereDate }
            )
        }
    }
}


