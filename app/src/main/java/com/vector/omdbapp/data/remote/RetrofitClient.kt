package com.vector.omdbapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RetrofitClient object used to provide a singleton Retrofit instance for the OMDB API.
 * 'object' in Kotlin means this is a singleton.
 */
object RetrofitClient {
    private const val BASE_URL = "https://www.omdbapi.com"
    /**
     * Lazily initialized API service for communicating with the OMDB endpoint.
     * The 'by lazy' keyword ensures that 'api' is only created upon first access.
     */
    val api: OmdbApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            // OmdbApi::class.java is the Java class object needed by Retrofit for reflection.
            .create(OmdbApi::class.java)
    }
}
