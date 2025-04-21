package com.vector.omdbapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.vector.omdbapp.R
import com.vector.omdbapp.viewmodel.MovieViewModel
import kotlinx.coroutines.launch

enum class BottomNavItem(
    val icon: ImageVector
) {
    Home(Icons.Filled.Search),
    Favorites(Icons.Filled.Favorite)
}
/**
 * Main composable screen for the OMDb App.
 * Displays a BottomNavigation bar with two destinations: Home and Favorites.
 * The content switches based on the selected tab.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmdbAppScreen() {
    val viewModel: MovieViewModel = hiltViewModel()
    val homeListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Tracks the current selected tab
    var selectedTab by rememberSaveable { mutableStateOf(BottomNavItem.Home) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_title))
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomAppBar {
                NavigationBarItem(
                    selected = selectedTab == BottomNavItem.Home,
                    onClick = { selectedTab = BottomNavItem.Home },
                    icon = { Icon(BottomNavItem.Home.icon, contentDescription = stringResource(R.string.tab_home)) },
                    label = { Text(stringResource(R.string.tab_home)) }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomNavItem.Favorites,
                    onClick = { selectedTab = BottomNavItem.Favorites },
                    icon = { Icon(BottomNavItem.Favorites.icon, contentDescription = stringResource(R.string.tab_favorites)) },
                    label = { Text(stringResource(R.string.tab_favorites)) }
                )
        }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                BottomNavItem.Home -> HomeScreen(viewModel, homeListState)
                BottomNavItem.Favorites -> FavoriteScreen()
            }
        }
    }
    // Observe snackbar messages from the ViewModel
    LaunchedEffect(Unit) {
        viewModel.snackbarFlow.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    // Scaffold with top bar and snackbar
//    Scaffold(topBar = {
//        TopAppBar(title = { Text(context.getString(R.string.app_title)) })
//    },snackbarHost = { SnackbarHost(hostState = snackbarHostState) })
//    {
//        Column(modifier = Modifier.padding(it).fillMaxSize()) {
//            SearchBar(
//                query = uiState.query,
//                onQueryChange = { newQuery -> viewModel.onQueryChange(newQuery) },
//                onSearchClick = { viewModel.searchMovies(context.getString(R.string.empty_query_message)) },
//                selectedYear = uiState.selectedYear,
//                onYearChange = viewModel::onYearChange,
//                selectedType = uiState.selectedType,
//                onTypeChange = viewModel::onTypeChange
//            )
//            when {
//                uiState.isLoading -> {
//                    Box(
//                        contentAlignment = Alignment.Center,
//                        modifier = Modifier.fillMaxSize()
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                }
//                uiState.errorMessage != null -> {
//                    Text(
//                        text = context.getString(R.string.error_label) + uiState.errorMessage,
//                        modifier = Modifier.padding(16.dp),
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                }
//                else -> {
//                    val pullRefreshState = rememberPullRefreshState(
//                        refreshing = uiState.isRefreshing,
//                        onRefresh = { viewModel.refreshMovies() }
//                    )
//
//                    Box(modifier = Modifier
//                        .fillMaxSize()
//                        .pullRefresh(pullRefreshState)
//                    ) {
//                        MovieList()
//
//                        PullRefreshIndicator(
//                            refreshing = uiState.isRefreshing,
//                            state = pullRefreshState,
//                            modifier = Modifier.align(Alignment.TopCenter)
//                        )
//                    }
//                }
//            }
//        }
//    }
}