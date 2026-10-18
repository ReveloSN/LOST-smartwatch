package com.lost.data.system

import com.lost.domain.port.IdGenerator
import java.util.UUID
import javax.inject.Inject

/** Identificadores aleatorios UUID. */
class UuidIdGenerator @Inject constructor() : IdGenerator {
    override fun newId(): String = UUID.randomUUID().toString()
}
