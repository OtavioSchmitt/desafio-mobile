package com.schmitttech.ingresso.data.repository

import com.schmitttech.ingresso.data.mapper.toDomain
import com.schmitttech.ingresso.data.remote.api.IngressoApi
import com.schmitttech.ingresso.domain.model.Movie
import com.schmitttech.ingresso.domain.repository.MoviesRepository

/**
 * Implementation of the [MoviesRepository] repository.
 * Responsible for fetching data from the API and converting it to the domain.
 */
class MoviesRepositoryImpl(
    private val api: IngressoApi
) : MoviesRepository {

    override suspend fun getComingSoonMovies(): Result<List<Movie>> {
        return runCatching {
            api.getComingSoonMovies().items.map { it.toDomain() }
        }
    }
}

