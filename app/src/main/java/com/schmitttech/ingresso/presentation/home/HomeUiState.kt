package com.schmitttech.ingresso.presentation.home

import com.schmitttech.ingresso.domain.model.Movie

/**
 * Represents the different UI states for the Home screen.
 * Using a sealed interface ensures that only one state can exist at a time,
 * providing a single source of truth for the Compose UI.
 */
sealed interface HomeUiState {
    
    /**
     * The initial state or the state when a fetch/refresh is in progress.
     */
    data object Loading : HomeUiState
    
    /**
     * The state when data has been successfully fetched.
     * @property movies The list of movies to be displayed.
     */
    data class Success(
        val movies: List<Movie>
    ) : HomeUiState
    
    /**
     * The state when an error occurred during data fetching.
     * @property message A descriptive error message or a resource ID for the user.
     */
    data class Error(
        val message: String
    ) : HomeUiState
}
