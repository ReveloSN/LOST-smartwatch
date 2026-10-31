package com.lost.watch.i18n

import com.lost.designsystem.components.atoms.LostTextRole
import java.awt.Font
import java.awt.font.FontRenderContext
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.ceil
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.w3c.dom.Element

/**
 * Ningún texto debe terminar en "…": ni con el tamaño normal, ni con Texto grande (+30 %), ni en la
 * pseudolocalización en-XA (corchetes y ~50 % de palabras de relleno, como la genera aapt2). Se miden
 * con la misma Manrope del APK sobre el ancho real de cada sitio en la pantalla redonda grande.
 */
class CaptionFitTest {
    /** Dónde se dibuja un grupo de textos: rol tipográfico, archivo de la fuente y ancho disponible. */
    private class Slot(val role: LostTextRole, val font: String, val sizeSp: Float, val widthDp: Double)

    private val caption = Slot(LostTextRole.CAPTION, "manrope_medium.ttf", CAPTION_SP, COLUMN_DP)
    private val listTitle = Slot(LostTextRole.TITLE, "manrope_semibold.ttf", TITLE_SP, LIST_TEXT_DP)

    private val cases: Map<Slot, Map<String, String>> = mapOf(
        caption to mapOf(
            "contact_sees_location" to "",
            "contact_pending" to "",
            "contact_authorized" to "Hermana",
            "home_idle_body" to ""
        ),
        listTitle to listOf(
            "settings_sensitivity",
            "settings_vibration",
            "settings_large_text",
            "settings_language",
            "settings_real_map",
            "settings_privacy",
            "settings_demo",
            "settings_about"
        ).associateWith { "" }
    )
    private val context = FontRenderContext(null, true, true)
    private val fonts = mutableMapOf<String, Font>()

    private fun lineCount(text: String, slot: Slot, scale: Float): Int {
        val base = fonts.getOrPut(slot.font) { Font.createFont(Font.TRUETYPE_FONT, File(FONT_DIR + slot.font)) }
        val font = base.deriveFont(slot.sizeSp * scale)
        fun width(line: String) = font.getStringBounds(line, context).width
        var lines = 1
        var current = ""
        text.split(" ").forEach { word ->
            val candidate = if (current.isEmpty()) word else "$current $word"
            if (width(candidate) <= slot.widthDp || current.isEmpty()) {
                current = candidate
            } else {
                lines++
                current = word
            }
        }
        return lines
    }

    /** Como el pseudolocalizador de aapt2: [texto one two three…] con ~50 % más de caracteres (medido en el APK). */
    private fun pseudo(text: String): String {
        val filler = StringBuilder()
        FILLER.split(" ").forEach { if (filler.length < ceil(text.length * EXPANSION)) filler.append(" ").append(it) }
        return "[$text$filler]"
    }

    private fun strings(folder: String): Map<String, String> {
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(File("src/main/res/$folder/strings.xml"))
        val nodes = document.getElementsByTagName("string")
        return (0 until nodes.length).map { nodes.item(it) as Element }
            .associate { it.getAttribute("name") to it.textContent }
    }

    private fun check(label: String, texts: Map<String, String>, scale: Float, transform: (String) -> String) =
        DynamicTest.dynamicTest(label) {
            cases.forEach { (slot, keys) ->
                val maxLines = slot.role.maxLinesAt(scale)
                keys.forEach { (key, argument) ->
                    val text = transform(texts.getValue(key).replace("%1\$s", argument))
                    assertTrue(lineCount(text, slot, scale) <= maxLines, "\"$text\" ($key, $label) no cabe")
                }
            }
        }

    @TestFactory
    fun `los textos caben sin cortarse`() = listOf("values", "values-en", "values-pt").flatMap { folder ->
        val texts = strings(folder)
        listOf(1f, LARGE_TEXT_SCALE).map { scale -> check("$folder a ${scale}x", texts, scale) { it } }
    } + check("en-XA", strings("values"), 1f, ::pseudo) // Android la genera desde los textos por defecto (es).

    private companion object {
        const val FONT_DIR = "../core/designsystem/src/main/res/font/"
        const val CAPTION_SP = 12f
        const val TITLE_SP = 16f
        const val COLUMN_DP = 159.0
        const val LIST_TEXT_DP = 147.0
        const val LARGE_TEXT_SCALE = 1.3f
        const val EXPANSION = 0.5
        const val FILLER = "one two three four five six seven eight nine ten"
    }
}
