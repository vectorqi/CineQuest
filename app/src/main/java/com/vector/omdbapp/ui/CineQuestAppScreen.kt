@file:Suppress("DEPRECATION")

package com.vector.omdbapp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.vector.omdbapp.ui.navigation.MainNavigation
import com.vector.omdbapp.ui.navigation.Screen
import com.vector.omdbapp.util.LocalAppImageLoader
import com.vector.omdbapp.util.provideGlobalImageLoader

/**
 * Main screen of the CineQuest App.
 * Handles navigation, system bar styling, and layout switching between Splash, Tab UI, and detail pages.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CineQuestAppScreen() {
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    val backgroundColor = MaterialTheme.colorScheme.background

    // Track current route from NavController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine whether to show AppBar + Tab UI (not on Splash, Detail, Poster screens)
    val showTabUI = remember(currentRoute) { isMainTabRoute(currentRoute) }

    // Global image loader (delayed until actually needed)
    val context = LocalContext.current
    val imageLoader by remember { mutableStateOf(provideGlobalImageLoader(context)) }

    // Persistent UI states across tabs
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val homeListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    val favoriteListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    // Apply system bar colors
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = backgroundColor,
            darkIcons = useDarkIcons
        )
    }

    // Provide image loader to whole app composition
    CompositionLocalProvider(LocalAppImageLoader provides imageLoader) {
        // Always render navigation graph
        MainNavigation(navController = navController)

        // Show tab layout only on main route (not on splash/detail/poster)
        if (showTabUI) {
            TabScreen(
                navController = navController,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                homeListState = homeListState,
                favoriteListState = favoriteListState
            )
        } else {
            // Optional: Fill screen with NavHost only (e.g., during splash or detail)
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

/**
 * Determines whether the current route is part of the main tab UI.
 * Excludes splash, detail, and poster pages.
 */
private fun isMainTabRoute(route: String?): Boolean {
    return when {
        route == null -> false
        route == Screen.Splash.route -> false
        route.startsWith("detail/") -> false
        route.startsWith("poster/") -> false
        else -> true
    }
}