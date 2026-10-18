package com.lost.watch.feature.destinations

import java.text.Normalizer
import java.util.Locale
import javax.inject.Inject

/**
 * Busca un destino por lo que el usuario dijo, sin tildes ni mayúsculas:
 * "llévame a la biblioteca" encuentra "Biblioteca Central".
 */
class DestinationMatcher @Inject constructor() {
    fun match(spokenText: String, items: List<DestinationItem>): DestinationItem? {
        val words = normalize(spokenText).split(" ").filter { it.length >= MIN_WORD_LENGTH }
        return items.maxByOrNull { item -> score(words, normalize(item.name)) }?.takeIf {
            score(words, normalize(it.name)) >
                0
        }
    }

    private fun score(words: List<String>, name: String): Int = words.count { name.contains(it) }

    private fun normalize(text: String): String = Normalizer.normalize(text.lowercase(Locale.ROOT), Normalizer.Form.NFD)
        .replace(DIACRITICS, "")

    private companion object {
        const val MIN_WORD_LENGTH = 4
        val DIACRITICS = Regex("\\p{Mn}+")
    }
}
