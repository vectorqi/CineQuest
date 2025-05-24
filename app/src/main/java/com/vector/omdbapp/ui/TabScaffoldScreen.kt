package com.vector.omdbapp.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController

@Composable
fun TabScaffoldScreen() {
    val navController = rememberNavController()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val homeListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val favoriteListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    TabScreen(
        navController = navController,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        homeListState = homeListState,
        favoriteListState = favoriteListState
    )
}
