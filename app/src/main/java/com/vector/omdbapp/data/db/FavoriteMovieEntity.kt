package com.vector.omdbapp.data.db
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey val imdbID: String,
    val title: String,
    val year: String,
    val posterUrl: String,
    val type: String
)