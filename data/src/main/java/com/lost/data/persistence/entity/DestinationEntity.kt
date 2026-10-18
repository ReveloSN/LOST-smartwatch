package com.lost.data.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Fila de un destino guardado o reciente. */
@Entity(tableName = "destinations")
data class DestinationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val kind: String,
    @ColumnInfo(name = "last_used_at") val lastUsedAtMillis: Long?
)
