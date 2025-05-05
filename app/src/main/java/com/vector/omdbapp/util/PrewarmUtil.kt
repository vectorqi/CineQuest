@file:OptIn(InternalCoroutinesApi::class)

package com.vector.omdbapp.util

import android.util.Log
import com.google.gson.Gson
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.remote.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.LockFreeLinkedListNode
import kotlinx.coroutines.launch

/**
 * Utility to prewarm Retrofit and Gson classes in the background to reduce cold-start latency.
 */
object PrewarmUtil {

    /**
     * Call this during app startup to preload classes that may cause VerifyClass blocking on UI thread.
     */
    fun prewarmCriticalClasses() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Retrofit & Gson
                RetrofitClient.api.let { }
                Gson().toJson(Movie(imdbID = "tt0000000", title = "", year = "", type = "", posterUrl = ""))

                // Kotlin coroutines internal class
                LockFreeLinkedListNode()

                // Kotlin ranges
                (1..3).firstOrNull()

                Log.d("Prewarm", "Critical classes prewarmed.")
            } catch (e: Exception) {
                Log.w("Prewarm", "Prewarm failed: ${e.message}")
            }
        }
    }

}
