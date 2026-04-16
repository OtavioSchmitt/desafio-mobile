package com.schmitttech.ingresso.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for the application.
 */
@Serializable
data object HomeRoute

@Serializable
data object SearchRoute

@Serializable
data class DetailsRoute(
    val movieId: String
)
