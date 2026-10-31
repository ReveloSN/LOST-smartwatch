package com.lost.watch.i18n

import com.lost.designsystem.components.atoms.LostTextRole
import java.awt.Font
import java.awt.font.FontRenderContext
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.w3c.dom.Element

/**
 * Los textos de apoyo (rol CAPTION) no deben terminar en "…", ni con el tamaño normal ni con
 * Texto grande (+30 %). Se miden con la misma Manrope del APK a 12 sp sobre la columna de 159 dp
 * que deja la pantalla redonda grande.
 */
class CaptionFitTest {
    private val keys = mapOf(
        "contact_sees_location" to "",
        "contact_pending" to "",
        "contact_authorized" to "Hermana",
        "home_idle_body" to ""
    )
    private val manrope = Font.createFont(Font.TRUETYPE_FONT, File(FONT_PATH))
    private val context = FontRenderContext(null, true, true)

    private fun lineCount(text: String, scale: Float): Int {
        val font = manrope.deriveFont(CAPTION_SIZE_DP * scale)
        fun width(line: String) = font.getStringBounds(line, context).width
        var lines = 1
        var current = ""
        text.split(" ").forEach { word ->
            val candidate = if (current.isEmpty()) word else "$current $word"
            if (width(candidate) <= COLUMN_WIDTH_DP || current.isEmpty()) {
                current = candidate
            } else {
                lines++
                current = word
            }
        }
        return lines
    }

    private fun strings(folder: String): Map<String, String> {
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(File("src/main/res/$folder/strings.xml"))
        val nodes = document.getElementsByTagName("string")
        return (0 until nodes.length).map { nodes.item(it) as Element }
            .associate { it.getAttribute("name") to it.textContent }
    }

    @TestFactory
    fun `los textos de apoyo caben sin cortarse`() = listOf("values", "values-en", "values-pt").flatMap { folder ->
        listOf(1f, LARGE_TEXT_SCALE).map { scale ->
            DynamicTest.dynamicTest("$folder a ${scale}x") {
                val texts = strings(folder)
                val maxLines = LostTextRole.CAPTION.maxLinesAt(scale)
                keys.forEach { (key, argument) ->
                    val text = texts.getValue(key).replace("%1\$s", argument)
                    assertTrue(lineCount(text, scale) <= maxLines, "\"$text\" ($key, $folder, ${scale}x) no cabe")
                }
            }
        }
    }

    private companion object {
        const val FONT_PATH = "../core/designsystem/src/main/res/font/manrope_medium.ttf"
        const val CAPTION_SIZE_DP = 12f
        const val COLUMN_WIDTH_DP = 159.0
        const val LARGE_TEXT_SCALE = 1.3f
    }
}
