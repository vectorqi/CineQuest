package com.vector.omdbapp.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Main : Screen("main")
    data object MovieDetail : Screen("detail/{imdbID}") {
        fun createRoute(imdbID: String) = "detail/$imdbID"
    }
    data object Poster : Screen("poster/{posterUrl}") {
        fun createRoute(posterUrl: String) = "poster/$posterUrl"
    }
}