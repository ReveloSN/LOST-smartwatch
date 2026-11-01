package com.lost.data.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lost.data.persistence.entity.AlertPlaceEntity

@Dao
interface AlertPlaceDao {
    @Insert
    suspend fun insert(place: AlertPlaceEntity)

    @Query("SELECT * FROM alert_places WHERE timestamp >= :epochMillis ORDER BY timestamp")
    suspend fun since(epochMillis: Long): List<AlertPlaceEntity>

    @Query("DELETE FROM alert_places")
    suspend fun deleteAll()
}
