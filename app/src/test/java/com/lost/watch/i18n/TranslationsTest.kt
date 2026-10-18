package com.lost.watch.i18n

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.w3c.dom.Element

/**
 * Internacionalización verificada en cada compilación: todo texto existe en es, en y pt
 * con los mismos marcadores (%1$s, %2$d…), y ninguna pantalla escribe texto en el código.
 */
class TranslationsTest {
    private val modules = listOf("src/main/res", "../data/src/main/res")
    private val languages = listOf("values-en", "values-pt")
    private val placeholder = Regex("%(\\d+\\$)?[sd]")

    private fun strings(file: File): Map<String, String> {
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        val strings = document.getElementsByTagName("string")
        val plurals = document.getElementsByTagName("plurals")
        val map = mutableMapOf<String, String>()
        for (i in 0 until strings.length) {
            val element = strings.item(i) as Element
            if (element.getAttribute("translatable") != "false") map[element.getAttribute("name")] = element.textContent
        }
        for (i in 0 until plurals.length) {
            val element = plurals.item(i) as Element
            map["plural:" + element.getAttribute("name")] = element.getElementsByTagName("item").item(0).textContent
        }
        return map
    }

    @TestFactory
    fun `cada texto existe en todos los idiomas con los mismos marcadores`() = modules.flatMap { module ->
        val base = strings(File(module, "values/strings.xml"))
        languages.map { language ->
            DynamicTest.dynamicTest("$module/$language") {
                val translated = strings(File(module, "$language/strings.xml"))
                assertEquals(base.keys, translated.keys, "claves distintas en $language")
                base.forEach { (key, text) ->
                    val expected = placeholder.findAll(text).map { it.value }.toSet()
                    val actual = placeholder.findAll(translated.getValue(key)).map { it.value }.toSet()
                    assertEquals(expected, actual, "marcadores de $key en $language")
                }
            }
        }
    }

    @Test
    fun `ninguna pantalla escribe texto visible en el codigo`() {
        val literalText = Regex("(LostText|Text)\\(\\s*(text\\s*=\\s*)?\"[^\"]+\"")
        val offenders = File("src/main/java").walkTopDown()
            .filter { it.extension == "kt" }
            .filter { file -> file.readLines().any { literalText.containsMatchIn(it) } }
            .map { it.name }
            .toList()
        assertTrue(offenders.isEmpty(), "Texto literal en: $offenders")
    }
}
