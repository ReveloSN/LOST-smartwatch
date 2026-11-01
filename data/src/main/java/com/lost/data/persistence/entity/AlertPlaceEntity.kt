package com.lost.data.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Fila de un lugar donde LOST preguntó (03.2). */
@Entity(tableName = "alert_places", indices = [Index("timestamp")])
data class AlertPlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "timestamp") val timestampMillis: Long,
    val latitude: Double,
    val longitude: Double,
    val cause: String?,
    @ColumnInfo(name = "routine_name") val routineName: String?
)
