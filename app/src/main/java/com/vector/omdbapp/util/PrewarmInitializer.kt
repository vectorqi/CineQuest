package com.vector.omdbapp.util


import android.content.Context
import androidx.startup.Initializer

/**
 * App Startup initializer to prewarm critical classes (e.g., Retrofit, Gson, Coroutines).
 * This helps reduce UI thread blocking due to class verification on cold start.
 */
class PrewarmInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        PrewarmUtil.prewarmCriticalClasses()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
