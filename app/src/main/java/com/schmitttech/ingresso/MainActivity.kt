package com.schmitttech.ingresso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.schmitttech.ingresso.presentation.details.DetailsRoute
import com.schmitttech.ingresso.presentation.details.DetailsViewModel
import com.schmitttech.ingresso.presentation.favorites.FavoritesRoute
import com.schmitttech.ingresso.presentation.favorites.FavoritesViewModel
import com.schmitttech.ingresso.presentation.home.HomeRoute
import com.schmitttech.ingresso.presentation.home.HomeViewModel
import com.schmitttech.ingresso.presentation.navigation.DetailsRoute
import com.schmitttech.ingresso.presentation.navigation.FavoritesRoute
import com.schmitttech.ingresso.presentation.navigation.HomeRoute
import com.schmitttech.ingresso.presentation.navigation.PreSaleRoute
import com.schmitttech.ingresso.presentation.presale.PreSaleRoute
import com.schmitttech.ingresso.presentation.presale.PreSaleViewModel
import com.schmitttech.ingresso.ui.theme.IngressoTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IngressoTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentDestination?.hierarchy?.any { it.hasRoute<HomeRoute>() || it.hasRoute<PreSaleRoute>() || it.hasRoute<FavoritesRoute>() } == true) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary
                            ) {
                                NavigationBarItem(
                                    selected = currentDestination.hierarchy.any { it.hasRoute<HomeRoute>() },
                                    onClick = {
                                        navController.navigate(HomeRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                    label = { Text(stringResource(R.string.movies)) }
                                )
                                NavigationBarItem(
                                    selected = currentDestination.hierarchy.any { it.hasRoute<PreSaleRoute>() },
                                    onClick = {
                                        navController.navigate(PreSaleRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                                    label = { Text(stringResource(R.string.pre_sale)) }
                                )
                                NavigationBarItem(
                                    selected = currentDestination.hierarchy.any { it.hasRoute<FavoritesRoute>() },
                                    onClick = {
                                        navController.navigate(FavoritesRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                                    label = { Text(stringResource(R.string.favorites)) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<HomeRoute> {
                            val viewModel: HomeViewModel = koinViewModel()
                            HomeRoute(
                                viewModel = viewModel,
                                onMovieClick = { movieId ->
                                    navController.navigate(DetailsRoute(movieId))
                                }
                            )
                        }

                        composable<PreSaleRoute> {
                            val viewModel: PreSaleViewModel = koinViewModel()
                            PreSaleRoute(
                                viewModel = viewModel,
                                onMovieClick = { movieId ->
                                    navController.navigate(DetailsRoute(movieId))
                                }
                            )
                        }

                        composable<FavoritesRoute> {
                            val viewModel: FavoritesViewModel = koinViewModel()
                            FavoritesRoute(
                                viewModel = viewModel,
                                onMovieClick = { movieId ->
                                    navController.navigate(DetailsRoute(movieId))
                                }
                            )
                        }

                        composable<DetailsRoute> { backStackEntry ->
                            val route: DetailsRoute = backStackEntry.toRoute()
                            val viewModel: DetailsViewModel = koinViewModel()
                            DetailsRoute(
                                movieId = route.movieId,
                                viewModel = viewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onMovieClick = { id ->
                                    navController.navigate(DetailsRoute(id)) {
                                        popUpTo<DetailsRoute> { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}