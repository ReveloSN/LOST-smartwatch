# Revisión de accesibilidad

Revisión hecha en el emulador Wear OS Large Round (454 × 454, 320 dpi) con Manrope ya empaquetada. Completa lo
que piden la sección 9.5 del plan y el PDF de comunicación con los usuarios: contraste, estado que nunca va solo
en color, áreas táctiles ≥ 48 dp, `contentDescription`, letra grande y TalkBack.

## 1. Texto grande (+30 %)

Se activó **Ajustes → Texto grande** y se recorrieron las 5 secciones, desplazando cada una, en es y pt.

| Hallazgo | Corrección | Prueba |
|---|---|---|
| Al activar o desactivar Texto grande la app volvía a **Inicio** (se perdía la sección visible) | `LargeTextScale` siempre usa el mismo `CompositionLocalProvider` (factor 1 o 1,3); antes el contenido cambiaba de lugar en el árbol de composición | `LargeTextScaleTest` (instrumentada) |
| Textos de apoyo y frases terminaban en "…" (Contactos en los 3 idiomas, Inicio en pt) | `LostTextRole.maxLinesAt(fontScale)`: las líneas crecen en proporción a la letra; números y display siguen en una línea | `LostTextRoleTest`, `CaptionFitTest` |

Resultado: sin cortes en ninguna sección. Las pantallas que no caben se desplazan con la corona y muestran el
indicador curvo. Capturas: [es](../capturas/pulido/a11y-texto-grande-es.png) ·
[pt](../capturas/pulido/a11y-texto-grande-pt.png).

## 2. Pseudolocalización en-XA

`en-XA` (activada en el build de depuración) agrega acentos, corchetes y palabras de relleno. Midiendo el APK con
`aapt2 dump resources`, el relleno es de **~50 %** de la longitud y se genera desde los textos por defecto (es).

| Hallazgo | Corrección | Prueba |
|---|---|---|
| "…" en la frase de Inicio, la nota del contacto y títulos de lista ("Mapa real (usa internet)", "Laboratorio 3, Bloque C") | Títulos y textos de apoyo admiten 3 líneas (en español siguen ocupando 1–2) | `CaptionFitTest` simula en-XA con la expansión medida |

Resultado: sin "…" en las 5 secciones. Captura: [en-XA](../capturas/pulido/a11y-en-xa.png). En las alertas la
pregunta en en-XA ocupa dos líneas y la segunda respuesta se alcanza desplazando (con indicador).

## 3. Auditoría semántica con uiautomator

Se volcó el árbol de accesibilidad (`uiautomator dump`) de cada sección (arriba, al medio y abajo) y de las
alertas, y se revisó con un script: tamaño de cada elemento tocable, etiqueta y orden de lectura.

| Revisión | Resultado |
|---|---|
| Áreas táctiles ≥ 48 dp | Todos los tocables completos miden ≥ 48 dp (botones 52 dp; filas de lista ≥ 48 dp en el centro). Los únicos "pequeños" eran filas a medio salir por el borde, que la lista encoge al desplazarse. |
| Etiquetas | Todo tocable tiene texto o `contentDescription`; el indicador de sección dice "Inicio, sección 1 de 5". |
| Orden de lectura | De arriba abajo y de lo importante a lo secundario: en la alerta, "Te saliste de tu ruta. → ¿Necesitas ayuda? → Sí, guíame → Estoy bien". |
| **Hallazgo:** con una alerta encima, las filas de la pantalla de abajo seguían en el árbol como tocables **sin etiqueta** | `AlertLayers` limpia la semántica de la pantalla de abajo mientras la alerta está visible. Un toque en una zona vacía de la alerta tampoco llega abajo. | `AlertLayersTest` (instrumentada) |

Después de la corrección la alerta expone solo sus dos respuestas.

## 4. Alertas en la pantalla redonda

Ver también [fidelidad-mockups.md](fidelidad-mockups.md). Con Manrope, "Estoy bien" quedaba fuera de la pantalla.
`AlertsFitOnRoundScreenTest` (instrumentada, 4 pruebas) verifica que las dos respuestas se ven sin desplazar y que
los botones en píldora quedan dentro del círculo con 4 dp de margen.

## 5. TalkBack

TalkBack 16 (Wear) viene en el emulador. Se activó con `settings put secure enabled_accessibility_services`
durante la demo "Calle cerrada":

- TalkBack habló al aparecer cada alerta (las alertas son regiones vivas) y durante la cuenta regresiva.
- Al deslizar, el foco recorre la pantalla en orden y enfoca el chip de estado con su etiqueta.
- **Límite de esta revisión:** el emulador no deja escuchar el audio y el registro de TalkBack no incluye el texto
  dicho, así que no se verificó palabra por palabra. Queda como pendiente para un reloj físico.

## 6. Contraste

Sin cambios de color en esta revisión: `ContrastRatioTest` sigue verificando WCAG AA (≥ 4,5:1) para todo color de
texto o estado sobre el fondo OLED. El nuevo `StatusIconBadge` relleno usa `Text.onAccent` sobre `Status.onRoute`,
la misma pareja ya verificada en los botones primarios.

## Pendientes

| Pendiente | Cómo cerrarlo |
|---|---|
| Escuchar TalkBack palabra por palabra | En un reloj físico: demo "Calle cerrada" con TalkBack y anotar lo que dice en cada alerta. |
| Accessibility Scanner | No está instalado en el emulador de Wear; aplicarlo en un reloj con Play Store. |
| Alertas en en-XA | La segunda respuesta se alcanza desplazando; aceptable para una pseudolocalización, revisar con traducciones reales más largas. |
