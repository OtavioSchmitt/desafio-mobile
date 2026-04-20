package com.schmitttech.ingresso.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schmitttech.ingresso.domain.repository.MoviesRepository
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
    getComingSoonMoviesUseCase: GetComingSoonMoviesUseCase,
    private val repository: MoviesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isSearchActive = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    val searchQuery: StateFlow<String> = _searchQuery
    val isSearchActive: StateFlow<Boolean> = _isSearchActive
    val isRefreshing: StateFlow<Boolean> = _isLoading

    /**
     * Observable state that the Compose UI will collect.
     * Combines multiple flows to produce a single source of truth for the UI.
     */
    val uiState: StateFlow<HomeUiState> = combine(
        getComingSoonMoviesUseCase(), _searchQuery, _isSearchActive, _isLoading, _error
    ) { movies, query, searchActive, isLoading, error ->
        when {
            error != null -> HomeUiState.Error(error)
            isLoading && movies.isEmpty() -> HomeUiState.Loading
            else -> {
                val filtered = if (searchActive && query.isNotBlank()) {
                    movies.filter { it.title.contains(query, ignoreCase = true) }
                } else {
                    movies
                }

                HomeUiState.Success(
                    movies = filtered,
                    genres = emptyList(),
                    selectedGenre = null
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
     * Triggers a background refresh and observes the cache.
     */
    fun getMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.refreshMovies().onFailure { exception ->
                if (uiState.value !is HomeUiState.Success || (uiState.value as HomeUiState.Success).movies.isEmpty()) {
                    _error.value = exception.message ?: "An unexpected error occurred"
                }
            }
            _isLoading.value = false
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearch() {
        _isSearchActive.value = !_isSearchActive.value
        if (!_isSearchActive.value) {
            _searchQuery.value = ""
        }
    }
}
