package com.vector.omdbapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vector.omdbapp.R
import com.vector.omdbapp.navigation.MainNavigation
import com.vector.omdbapp.navigation.Screen
import com.vector.omdbapp.util.LocalAppImageLoader
import com.vector.omdbapp.util.provideGlobalImageLoader

enum class BottomNavItem(
    val icon: ImageVector
) {
    Home(Icons.Filled.Search),
    Favorites(Icons.Filled.Favorite)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmdbAppScreen() {
    val navController = rememberNavController()
    val homeListState = rememberLazyListState()
    val favoriteListState = rememberLazyListState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showAppBars = currentRoute?.startsWith("detail/") != true && currentRoute?.startsWith("poster/") != true

    val context = LocalContext.current
    val imageLoader = remember { provideGlobalImageLoader(context) }

    CompositionLocalProvider(LocalAppImageLoader provides imageLoader) {
        if (showAppBars) {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = currentRoute == Screen.Home.route,
                            onClick = {
                                navController.navigate(Screen.Home.route) {
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
                            selected = currentRoute == Screen.Favorites.route,
                            onClick = {
                                navController.navigate(Screen.Favorites.route) {
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
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    MainNavigation(
                        navController = navController,
                        homeListState = homeListState,
                        favoriteListState = favoriteListState
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                MainNavigation(
                    navController = navController,
                    homeListState = homeListState,
                    favoriteListState = favoriteListState
                )
            }
        }
    }
}
