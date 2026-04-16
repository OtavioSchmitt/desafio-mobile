package com.schmitttech.ingresso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
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
import com.schmitttech.ingresso.presentation.home.HomeRoute
import com.schmitttech.ingresso.presentation.home.HomeViewModel
import com.schmitttech.ingresso.presentation.navigation.DetailsRoute
import com.schmitttech.ingresso.presentation.navigation.HomeRoute
import com.schmitttech.ingresso.presentation.navigation.SearchRoute
import com.schmitttech.ingresso.presentation.search.SearchScreen
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
                        if (currentDestination?.hierarchy?.any { it.hasRoute<HomeRoute>() || it.hasRoute<SearchRoute>() } == true) {
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
                                    selected = currentDestination.hierarchy.any { it.hasRoute<SearchRoute>() },
                                    onClick = {
                                        navController.navigate(SearchRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                                    label = { Text(stringResource(R.string.explore)) }
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

                        composable<SearchRoute> {
                            SearchScreen()
                        }

                        composable<DetailsRoute> { backStackEntry ->
                            val route: DetailsRoute = backStackEntry.toRoute()
                            val viewModel: DetailsViewModel = koinViewModel()
                            DetailsRoute(
                                movieId = route.movieId,
                                viewModel = viewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}