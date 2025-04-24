package com.vector.omdbapp.di

import android.content.Context
import android.content.res.Resources
import androidx.room.Room
import com.vector.omdbapp.data.db.FavoriteMovieDao
import com.vector.omdbapp.data.db.MovieDatabase
import com.vector.omdbapp.data.remote.OmdbApi
import com.vector.omdbapp.data.remote.RetrofitClient
import com.vector.omdbapp.data.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideResources(@ApplicationContext applicationContext: Context): Resources {
        return applicationContext.resources
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MovieDatabase =
        Room.databaseBuilder(
            context,
            MovieDatabase::class.java,
            "movie_database"
        ).build()

    @Provides
    fun provideFavoriteMovieDao(db: MovieDatabase): FavoriteMovieDao = db.favoriteMovieDao()

    @Provides
    @Singleton
    fun provideOmdbApi(): OmdbApi = RetrofitClient.api

    @Provides
    @Singleton
    fun provideMovieRepository(
        api: OmdbApi,
        dao: FavoriteMovieDao
    ): MovieRepository = MovieRepository(api,dao)
}
