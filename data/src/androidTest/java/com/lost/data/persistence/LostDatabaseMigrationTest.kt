package com.lost.data.persistence

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Quien actualiza LOST no pierde lo aprendido: la base de la versión 1 pasa a la 2 (lugares de alerta) con
 * la migración automática y conserva sus rutinas.
 */
@RunWith(AndroidJUnit4::class)
class LostDatabaseMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), LostDatabase::class.java)

    @Test
    fun deLaVersion1ALa2SeConservanLasRutinasYAparecenLosLugares() {
        helper.createDatabase(NAME, 1).use { v1 ->
            v1.execSQL(
                "INSERT INTO routines (id, name, destination_kind, path, days, departure_minute, duration_count, " +
                    "duration_mean, duration_m2, times_observed, is_confirmed) " +
                    "VALUES ('casa-u', 'Casa → U', 'UNIVERSITY', '', 'MONDAY', 400, 5, 35.0, 0.0, 5, 1)"
            )
        }
        helper.runMigrationsAndValidate(NAME, 2, true).use { v2 ->
            v2.query("SELECT name FROM routines").use { cursor ->
                cursor.moveToFirst()
                assertEquals("Casa → U", cursor.getString(0))
            }
            v2.query("SELECT COUNT(*) FROM alert_places").use { cursor ->
                cursor.moveToFirst()
                assertEquals(0, cursor.getInt(0))
            }
        }
    }

    private companion object {
        const val NAME = "migration-test.db"
    }
}
