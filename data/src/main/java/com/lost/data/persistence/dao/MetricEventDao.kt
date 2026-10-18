package com.lost.data.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lost.data.persistence.entity.MetricEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MetricEventDao {
    @Insert
    suspend fun insert(event: MetricEventEntity)

    @Query("SELECT * FROM metric_events WHERE timestamp >= :epochMillis ORDER BY timestamp")
    fun observeSince(epochMillis: Long): Flow<List<MetricEventEntity>>
}
