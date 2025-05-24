@file:Suppress("DEPRECATION")

package com.vector.omdbapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vector.omdbapp.ui.navigation.MainNavigation
import com.vector.omdbapp.ui.navigation.Screen

/**
 * Main screen of the CineQuest App.
 * Handles navigation, system bar styling, and layout switching between Splash, Tab UI, and detail pages.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CineQuestAppScreen() {
    val navController = rememberNavController()

    // Track current route from NavController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine whether to show AppBar + Tab UI (not on Splash, Detail, Poster screens)
    val showTabUI = remember(currentRoute) { isMainTabRoute(currentRoute) }

    // Persistent UI states across tabs
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val homeListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    val favoriteListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }
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