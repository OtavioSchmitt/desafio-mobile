package com.schmitttech.ingresso.domain.usecase

import com.schmitttech.ingresso.domain.model.Movie
import com.schmitttech.ingresso.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase responsible for fetching movies that are in pre-sale.
 */
class GetPreSaleMoviesUseCase(
    private val repository: MoviesRepository
) {
    operator fun invoke(): Flow<List<Movie>> {
        return repository.observePreSale()
    }
}
