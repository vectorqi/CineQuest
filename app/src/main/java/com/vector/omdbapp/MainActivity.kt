package com.vector.omdbapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vector.omdbapp.ui.OmdbAppScreen
import com.vector.omdbapp.viewmodel.MovieViewModel
import com.vector.omdbapp.viewmodel.MovieViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val application = applicationContext as Application
            val viewModel: MovieViewModel = viewModel(factory = MovieViewModelFactory(application))
            OmdbAppScreen(viewModel = viewModel)
        }
    }
}

