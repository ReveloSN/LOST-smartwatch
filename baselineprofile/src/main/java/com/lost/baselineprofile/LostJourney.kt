package com.lost.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until

/** Recorrido típico de una persona: abre LOST y pasa por las cinco secciones con el dedo. */
internal const val PACKAGE = "com.lost.watch"
private const val SECTIONS = 5
private const val WAIT_MILLIS = 2_000L
private const val SWIPE_PERCENT = 0.8f

internal fun MacrobenchmarkScope.openAndVisitSections() {
    pressHome()
    startActivityAndWait()
    device.wait(Until.hasObject(androidx.test.uiautomator.By.pkg(PACKAGE).depth(0)), WAIT_MILLIS)
    repeat(SECTIONS - 1) {
        device.findObject(androidx.test.uiautomator.By.pkg(PACKAGE).depth(0))?.swipe(Direction.LEFT, SWIPE_PERCENT)
        device.waitForIdle()
    }
}
