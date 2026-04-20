package com.schmitttech.ingresso.domain.usecase

import com.schmitttech.ingresso.domain.model.Movie
import com.schmitttech.ingresso.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class GetMovieDetailsUseCase(
    private val repository: MoviesRepository
) {
    fun observeMovie(id: String): Flow<Movie?> {
        return repository.observeMovieById(id)
    }

    fun getRelatedMovies(movie: Movie): Flow<List<Movie>> {
        if (movie.categories.isEmpty()) return emptyFlow()

        val primaryGenre = movie.categories.first()
        return repository.observeRelatedByGenre(excludeId = movie.id, genre = primaryGenre)
    }
}
