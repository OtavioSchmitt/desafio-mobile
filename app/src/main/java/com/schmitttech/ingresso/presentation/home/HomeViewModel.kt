package com.schmitttech.ingresso.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schmitttech.ingresso.domain.usecase.GetComingSoonMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen.
 * Orchestrates fetching movies and manages the UI state using [HomeUiState].
 */
class HomeViewModel(
    private val getComingSoonMoviesUseCase: GetComingSoonMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    
    /**
     * Observable state that the Compose UI will collect.
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        getMovies()
    }

    /**
     * Fetches movies from the UseCase and updates the UI state based on the result.
     * This is also used for the Pull-to-refresh functionality.
     */
    fun getMovies() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            getComingSoonMoviesUseCase()
                .onSuccess { movies ->
                    _uiState.value = HomeUiState.Success(movies)
                }
                .onFailure { exception ->
                    _uiState.value = HomeUiState.Error(
                        message = exception.message ?: "An unexpected error occurred"
                    )
                }
        }
    }
}
