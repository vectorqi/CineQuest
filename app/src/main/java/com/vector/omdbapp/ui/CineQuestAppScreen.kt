@file:Suppress("DEPRECATION")

package com.vector.omdbapp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.vector.omdbapp.ui.navigation.MainNavigation
import com.vector.omdbapp.ui.navigation.Screen
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
fun CineQuestAppScreen() {
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    val backgroundColor = MaterialTheme.colorScheme.background

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showAppBars = currentRoute != Screen.Splash.route && currentRoute?.startsWith("detail/") != true && currentRoute?.startsWith("poster/") != true

    val context = LocalContext.current
    val imageLoader = remember { provideGlobalImageLoader(context) }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val homeListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }
    val favoriteListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = backgroundColor,
            darkIcons = useDarkIcons
        )
    }

    CompositionLocalProvider(LocalAppImageLoader provides imageLoader) {
        if (showAppBars) {
                    MainNavigation(navController = navController)
                    if (navController.currentBackStackEntry?.destination?.route in listOf(
                            Screen.Splash.route,
                            Screen.MovieDetail.route,
                            Screen.Poster.route
                        )
                    ) {
                        // do nothing
                    } else {
                        TabScreen(
                            navController = navController,
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            homeListState = homeListState,
                            favoriteListState = favoriteListState
                        )
                    }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                MainNavigation(navController = navController)
            }
        }
    }
}
