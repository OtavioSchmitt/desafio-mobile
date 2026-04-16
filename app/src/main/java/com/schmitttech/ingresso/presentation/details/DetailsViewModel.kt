package com.schmitttech.ingresso.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schmitttech.ingresso.domain.model.Movie
import com.schmitttech.ingresso.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for the Movie Details screen.
 */
sealed interface DetailsUiState {
    data object Loading : DetailsUiState
    data class Success(val movie: Movie) : DetailsUiState
    data class Error(val message: String) : DetailsUiState
}

/**
 * ViewModel for the Details screen.
 * Fetches the specific movie details by ID.
 */
class DetailsViewModel(
    private val repository: MoviesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    fun getMovieDetails(movieId: String) {
        viewModelScope.launch {
            _uiState.value = DetailsUiState.Loading

            repository.getComingSoonMovies()
                .onSuccess { movies ->
                    val movie = movies.find { it.id == movieId }
                    if (movie != null) {
                        _uiState.value = DetailsUiState.Success(movie)
                    } else {
                        _uiState.value = DetailsUiState.Error("Filme não encontrado")
                    }
                }
                .onFailure {
                    _uiState.value = DetailsUiState.Error(it.message ?: "Erro ao carregar detalhes")
                }
        }
    }
}
