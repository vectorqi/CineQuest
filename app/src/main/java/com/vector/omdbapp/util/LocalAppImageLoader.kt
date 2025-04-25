package com.vector.omdbapp.util

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import coil.ImageLoader

val LocalAppImageLoader = staticCompositionLocalOf<ImageLoader> {
    error("No ImageLoader provided")
}

fun provideGlobalImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .crossfade(true)
        .diskCachePolicy(coil.request.CachePolicy.ENABLED)
        .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
        .build()
}