package com.lost.data.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lost.data.persistence.entity.DestinationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinationDao {
    @Query("SELECT * FROM destinations")
    fun observeAll(): Flow<List<DestinationEntity>>

    @Upsert
    suspend fun upsert(destination: DestinationEntity)
}
