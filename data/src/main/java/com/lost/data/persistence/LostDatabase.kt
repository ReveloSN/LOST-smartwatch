package com.lost.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lost.data.persistence.dao.DestinationDao
import com.lost.data.persistence.dao.MetricEventDao
import com.lost.data.persistence.dao.RoutineDao
import com.lost.data.persistence.dao.TripDao
import com.lost.data.persistence.entity.DestinationEntity
import com.lost.data.persistence.entity.MetricEventEntity
import com.lost.data.persistence.entity.RoutineEntity
import com.lost.data.persistence.entity.TripEntity

/** Base de datos local. Todo lo aprendido se queda en el reloj. */
@Database(
    entities = [TripEntity::class, RoutineEntity::class, MetricEventEntity::class, DestinationEntity::class],
    version = 1,
    exportSchema = true
)
abstract class LostDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao

    abstract fun routineDao(): RoutineDao

    abstract fun metricEventDao(): MetricEventDao

    abstract fun destinationDao(): DestinationDao

    companion object {
        const val NAME = "lost.db"
    }
}
