package com.lost.watch.feature.destinations

import java.util.Locale

/**
 * Quita del dictado lo que no es el lugar ("Llévame a la biblioteca" → "biblioteca"),
 * para que la búsqueda envíe lo mínimo y encuentre mejor.
 */
object SpokenPlaceQuery {
    private val leadingPhrases = Regex(
        "^(ll[eé]vame|quiero ir|voy|vamos|take me|go|i want to go|me leva|quero ir|vou)" +
            "( (a|al|a la|hasta|hacia|to|to the|para|para o|para a|ao|à))?( (el|la|los|las|the|o|a))? ",
        RegexOption.IGNORE_CASE
    )

    fun clean(spokenText: String): String = spokenText.trim().lowercase(Locale.ROOT).replace(leadingPhrases, "").trim()
}
