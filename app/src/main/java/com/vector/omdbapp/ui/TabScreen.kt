package com.vector.omdbapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

/**
 * Displays the main tab UI with a bottom navigation bar.
 * Allows switching between Home and Favorites tabs.
 *
 * @param navController Navigation controller for navigating to detail/poster pages.
 * @param selectedTab Index of the currently selected tab.
 * @param onTabSelected Callback to change the current tab.
 * @param homeListState State of the movie list scroll position in Home tab.
 * @param favoriteListState State of the movie list scroll position in Favorites tab.
 */
@Composable
fun TabScreen(    navController: NavHostController,
                  selectedTab: Int,
                  onTabSelected: (Int) -> Unit,
                  homeListState: LazyListState,
                  favoriteListState: LazyListState) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorites") },
                    label = { Text("Favorites") }
                )
            }
        }
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            if (selectedTab == 0) {
                HomeScreen(navController = navController, listState = homeListState)
            } else {
                FavoriteScreen(navController = navController, listState = favoriteListState,onBrowseClick = { onTabSelected(0)})
            }
        }
    }
}