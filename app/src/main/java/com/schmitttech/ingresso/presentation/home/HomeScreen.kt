@file:OptIn(ExperimentalMaterial3Api::class)

package com.schmitttech.ingresso.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schmitttech.ingresso.R
import com.schmitttech.ingresso.presentation.home.components.MovieCard

/**
 * Entry point for the Home screen that interacts with the ViewModel.
 */
@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    onMovieClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        uiState = uiState,
        onRefresh = viewModel::getMovies,
        onMovieClick = onMovieClick,
        onGenreSelected = viewModel::onGenreSelected
    )
}

/**
 * Stateless UI for the Home screen.
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onMovieClick: (String) -> Unit,
    onGenreSelected: (String?) -> Unit
) {
    val gridState = rememberLazyGridState()
    val selectedGenre = (uiState as? HomeUiState.Success)?.selectedGenre

    LaunchedEffect(selectedGenre) {
        gridState.animateScrollToItem(0)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.coming_soon),
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    actions = {
                         IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )

                if (uiState is HomeUiState.Success) {
                    GenreFilterBar(
                        genres = uiState.genres,
                        selectedGenre = uiState.selectedGenre,
                        onGenreSelected = onGenreSelected
                    )
                }
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState is HomeUiState.Loading,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is HomeUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is HomeUiState.Success -> {
                    if (uiState.movies.isEmpty()) {
                        EmptyContent(stringResource(R.string.empty_filter))
                    } else {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.movies, key = { it.id }) { movie ->
                                MovieCard(movie = movie, onClick = onMovieClick)
                            }
                        }
                    }
                }

                is HomeUiState.Error -> {
                    ErrorContent(message = uiState.message, onRetry = onRefresh)
                }
            }
        }
    }
}

@Composable
private fun GenreFilterBar(
    genres: List<String>,
    selectedGenre: String?,
    onGenreSelected: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedGenre == null,
                onClick = { onGenreSelected(null) },
                label = { Text(stringResource(R.string.all)) },
                colors = chipColors(),
                shape = RoundedCornerShape(100)
            )
        }
        items(genres) { genre ->
            FilterChip(
                selected = selectedGenre == genre,
                onClick = { onGenreSelected(if (selectedGenre == genre) null else genre) },
                label = { Text(genre) },
                colors = chipColors(),
                shape = RoundedCornerShape(100)
            )
        }
    }
}

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primary,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
)

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.error_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(text = message, modifier = Modifier.padding(vertical = 8.dp))
        Button(onClick = onRetry) { Text(stringResource(R.string.error_retry)) }
    }
}

@Composable
private fun EmptyContent(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
