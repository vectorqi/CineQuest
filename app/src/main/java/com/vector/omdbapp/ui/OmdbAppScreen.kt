package com.vector.omdbapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vector.omdbapp.R
import com.vector.omdbapp.navigation.MainNavigation
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
    val navController = rememberNavController()

    // Remember independent list states for each screen
    val homeListState = rememberLazyListState()
    val favoriteListState = rememberLazyListState()

    // Track current route to sync bottom navigation selection
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showAppBars = currentRoute?.startsWith("detail/") != true && currentRoute?.startsWith("poster/") != true

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Tracks the current selected tab
//    var selectedTab by rememberSaveable { mutableStateOf(BottomNavItem.Home) }
    if (showAppBars) {
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
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home") {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                BottomNavItem.Home.icon,
                                contentDescription = stringResource(R.string.tab_home)
                            )
                        },
                        label = { Text(stringResource(R.string.tab_home)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "favorites",
                        onClick = {
                            navController.navigate("favorites") {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                BottomNavItem.Favorites.icon,
                                contentDescription = stringResource(R.string.tab_favorites)
                            )
                        },
                        label = { Text(stringResource(R.string.tab_favorites)) }
                    )
            }
        }
    )

    { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainNavigation(
                navController = navController,
                homeListState = homeListState,
                favoriteListState = favoriteListState
            )
        }
    }
    }else{
        Box(modifier = Modifier.fillMaxSize()) {
            MainNavigation(
                navController = navController,
                homeListState = homeListState,
                favoriteListState = favoriteListState
            )
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
}