package com.lost.data.demo

import com.lost.domain.port.ContactReader
import com.lost.domain.port.ContactWriter
import com.lost.domain.port.DestinationWriter
import com.lost.domain.port.RoutineReader
import com.lost.domain.port.RoutineWriter
import javax.inject.Inject

/** Repositorios que necesita el sembrador de la demo, agrupados para no pasar muchos parámetros. */
class DemoRepositories @Inject constructor(
    val routineReader: RoutineReader,
    val routineWriter: RoutineWriter,
    val contactReader: ContactReader,
    val contactWriter: ContactWriter,
    val destinationWriter: DestinationWriter
)
