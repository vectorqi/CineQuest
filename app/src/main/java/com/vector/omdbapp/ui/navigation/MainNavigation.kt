package com.vector.omdbapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vector.omdbapp.ui.CineQuestAppScreen
import com.vector.omdbapp.ui.MovieDetailScreen
import com.vector.omdbapp.ui.PosterScreen
import com.vector.omdbapp.ui.SplashScreen

@Composable
fun MainNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Main.route) {
            CineQuestAppScreen()
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