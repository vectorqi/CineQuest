package com.vector.omdbapp.data.db
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMovieDao {

    @Query("SELECT * FROM favorite_movies")
    fun getAllFavorites(): Flow<List<FavoriteMovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(movie: FavoriteMovieEntity)

    @Delete
    suspend fun removeFavorite(movie: FavoriteMovieEntity)

    @Query("DELETE FROM favorite_movies WHERE imdbID = :id")
    suspend fun removeFavoriteById(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE imdbID = :id)")
    suspend fun isFavorite(id: String): Boolean
}
