package com.schmitttech.ingresso.domain.repository

import com.schmitttech.ingresso.domain.model.Movie

/**
 * Repository interface defining the data contract for movies.
 */
interface MoviesRepository {
    /**
     * Fetches the list of movies coming soon.
     * Returns a [Result] containing the list of [Movie] on success, or a failure.
     */
    suspend fun getComingSoonMovies(): Result<List<Movie>>
}

