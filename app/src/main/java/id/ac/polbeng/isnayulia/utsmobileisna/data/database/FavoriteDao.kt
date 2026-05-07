package id.ac.polbeng.isnayulia.utsmobileisna.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE personId = :personId")
    suspend fun delete(personId: Int)

    @Query("SELECT * FROM favorites")
    suspend fun getAllFavorites(): List<FavoriteEntity>

    @Query("SELECT COUNT(*) FROM favorites WHERE personId = :personId")
    suspend fun isFavorite(personId: Int): Int
}