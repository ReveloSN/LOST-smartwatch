package com.lost.data.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lost.data.persistence.entity.TripEntity

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trip: TripEntity)

    @Query("SELECT * FROM trips WHERE started_at >= :epochMillis ORDER BY started_at")
    suspend fun tripsSince(epochMillis: Long): List<TripEntity>

    @Query("DELETE FROM trips")
    suspend fun deleteAll()
}
