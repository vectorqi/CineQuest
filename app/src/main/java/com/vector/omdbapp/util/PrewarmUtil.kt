package com.vector.omdbapp.util

import android.util.Log
import com.google.gson.Gson
import com.vector.omdbapp.data.model.Movie
import com.vector.omdbapp.data.remote.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Utility to prewarm Retrofit and Gson classes in the background to reduce cold-start latency.
 */
object PrewarmUtil {

    /**
     * Call this during app startup to preload classes that may cause VerifyClass blocking on UI thread.
     */
    fun prewarmRetrofitAndGson() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Force Retrofit to verify class structure
                RetrofitClient.api.let { }

                // Force Gson class binding by parsing a dummy object
                Gson().toJson(Movie(imdbID = "tt0000000", title = "", year = "", type = "", posterUrl = ""))

                Log.d("Prewarm", "Retrofit and Gson prewarmed successfully.")
            } catch (e: Exception) {
                Log.w("Prewarm", "Failed to prewarm Retrofit or Gson: ${e.message}")
            }
        }
    }
}
