package com.vector.omdbapp

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.vector.omdbapp.ui.navigation.MainNavigation
import com.vector.omdbapp.ui.theme.APPTheme
import com.vector.omdbapp.util.LocalAppImageLoader
import com.vector.omdbapp.util.provideGlobalImageLoader
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        windowInsetsController.isAppearanceLightNavigationBars = true
        setContent {
            APPTheme(
                darkTheme = isSystemInDarkTheme(),
                dynamicColor = true
            ) {
                val context = applicationContext
                val imageLoader = remember { provideGlobalImageLoader(context) }

                val systemUiController = rememberSystemUiController()
                val backgroundColor = MaterialTheme.colorScheme.background
                val useDarkIcons = !isSystemInDarkTheme()

                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = backgroundColor,
                        darkIcons = useDarkIcons
                    )
                    systemUiController.setNavigationBarColor(
                        color = backgroundColor,
                        darkIcons = useDarkIcons,
                        navigationBarContrastEnforced = false
                    )
                }

                CompositionLocalProvider(LocalAppImageLoader provides imageLoader) {
                    MainNavigation()
                }
            }
        }
    }
}