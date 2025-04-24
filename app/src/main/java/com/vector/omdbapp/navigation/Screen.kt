package com.vector.omdbapp.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Favorites : Screen("favorites")
    data object MovieDetail : Screen("detail/{imdbID}") {
        fun createRoute(imdbID: String) = "detail/$imdbID"
    }
    data object Poster : Screen("poster/{posterUrl}") {
        fun createRoute(posterUrl: String) = "poster/$posterUrl"
    }
}