package com.schmitttech.ingresso.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schmitttech.ingresso.domain.model.Movie
import com.schmitttech.ingresso.domain.usecase.GetComingSoonMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen.
 * Orchestrates fetching movies and manages the UI state using [HomeUiState]
 * with reactive filtering and sorting.
 */
class HomeViewModel(
    private val getComingSoonMoviesUseCase: GetComingSoonMoviesUseCase
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    private val _selectedGenre = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    /**
     * Observable state that the Compose UI will collect.
     * Combines multiple flows to produce a single source of truth for the UI.
     */
    val uiState: StateFlow<HomeUiState> = combine(
        _movies, _selectedGenre, _isLoading, _error
    ) { movies, selectedGenre, isLoading, error ->
        when {
            error != null -> HomeUiState.Error(error)
            isLoading -> HomeUiState.Loading
            else -> {
                val filtered = if (selectedGenre == null) {
                    movies
                } else {
                    movies.filter { it.categories.contains(selectedGenre) }
                }

                HomeUiState.Success(
                    movies = filtered,
                    genres = movies.flatMap { it.categories }.distinct().sorted(),
                    selectedGenre = selectedGenre
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    init {
        getMovies()
    }

    /**
     * Fetches movies from the UseCase and updates the internal state.
     */
    fun getMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            getComingSoonMoviesUseCase()
                .onSuccess { movies ->
                    _movies.value = movies
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "An unexpected error occurred"
                    _isLoading.value = false
                }
        }
    }

    fun onGenreSelected(genre: String?) {
        _selectedGenre.value = genre
    }
}
