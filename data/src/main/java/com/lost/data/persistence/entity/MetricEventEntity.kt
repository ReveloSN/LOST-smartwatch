package com.lost.data.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Fila de un evento de métricas. */
@Entity(tableName = "metric_events", indices = [Index("timestamp")])
data class MetricEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    @ColumnInfo(name = "timestamp") val timestampMillis: Long,
    @ColumnInfo(name = "elapsed") val elapsedMillis: Long?
)
