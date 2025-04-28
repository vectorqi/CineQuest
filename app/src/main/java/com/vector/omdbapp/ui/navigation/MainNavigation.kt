package com.vector.omdbapp.ui.navigation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vector.omdbapp.ui.FavoriteScreen
import com.vector.omdbapp.ui.HomeScreen
import com.vector.omdbapp.ui.MovieDetailScreen
import com.vector.omdbapp.ui.PosterScreen

@Composable
fun MainNavigation(navController: NavHostController,
                   homeListState: LazyListState,
                   favoriteListState: LazyListState
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, listState = homeListState)
        }
        composable(Screen.Favorites.route) {
            FavoriteScreen(navController = navController, listState = favoriteListState)
        }
        composable(Screen.MovieDetail.route) { backStackEntry ->
            val imdbID = backStackEntry.arguments?.getString("imdbID") ?: ""
            MovieDetailScreen(imdbId = imdbID, navController = navController)
        }
        composable(Screen.Poster.route) { backStackEntry ->
            val posterUrl = backStackEntry.arguments?.getString("posterUrl") ?: ""
            PosterScreen(posterUrl = posterUrl, navController = navController)
        }
    }
}
