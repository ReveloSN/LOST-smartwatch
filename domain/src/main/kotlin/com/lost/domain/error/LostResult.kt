package com.lost.domain.error

/** Resultado explícito: éxito con valor o fallo con un [LostError] conocido. */
sealed interface LostResult<out T> {
    data class Success<out T>(val value: T) : LostResult<T>

    data class Failure(val error: LostError) : LostResult<Nothing>

    val isSuccess: Boolean get() = this is Success

    fun valueOrNull(): T? = (this as? Success)?.value

    fun errorOrNull(): LostError? = (this as? Failure)?.error
}
