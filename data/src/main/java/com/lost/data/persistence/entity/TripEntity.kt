package com.lost.data.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Fila de un recorrido terminado. Los puntos van codificados en una sola columna. */
@Entity(tableName = "trips", indices = [Index("started_at")])
data class TripEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "started_at") val startedAtMillis: Long,
    @ColumnInfo(name = "points") val encodedPoints: String
)
