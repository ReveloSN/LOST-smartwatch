package com.lost.data.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Fila de una rutina aprendida, con su trayectoria de referencia y su estadística de duración. */
@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "destination_kind") val destinationKind: String,
    @ColumnInfo(name = "path") val encodedPath: String,
    val days: String,
    @ColumnInfo(name = "departure_minute") val departureMinuteOfDay: Int,
    @ColumnInfo(name = "duration_count") val durationCount: Int,
    @ColumnInfo(name = "duration_mean") val durationMeanMinutes: Double,
    @ColumnInfo(name = "duration_m2") val durationM2: Double,
    @ColumnInfo(name = "times_observed") val timesObserved: Int,
    @ColumnInfo(name = "is_confirmed") val isConfirmed: Boolean
)
