# Fidelidad con los mockups

Comparación de la app en el emulador (Wear OS Large Round, 454 × 454, con Manrope) contra los mockups de
`docs/design/LOST v2.dc.html`, láminas 06 (onboarding y patrones), 07 (motor de IA en flujo), 08 (destinos,
semana, ajustes) y 10 (errores y vacíos). Las capturas están en [`docs/capturas/pulido/`](../capturas/pulido/).

Cada diferencia cae en una de tres categorías:

- **(a) Corregida**: la app ahora se parece al mockup (con su commit y, si era un error, su prueba).
- **(b) Decisión consciente**: la app se aparta del mockup a propósito; se dice por qué.
- **(c) Pendiente**: diferencia real que queda por cerrar.

## (a) Corregidas

| Pantalla (mockup) | Diferencia | Corrección | Prueba |
|---|---|---|---|
| Toda la app | Sans del sistema en vez de **Manrope** | Manrope empaquetada (4 pesos, +97 KB) — ver [tipografia-manrope.md](tipografia-manrope.md) | `CaptionFitTest` |
| ¿Necesitas ayuda? (F.2) | Con Manrope la pregunta pasaba a 2 líneas y **"Estoy bien" quedaba fuera de la pantalla** | Pregunta de 18 a 17 sp (cabe en una línea, como en el mockup) y sin espacio extra en `AlertTemplate` | `AlertsFitOnRoundScreenTest` (instrumentada) |
| ¿Vas a otro lado hoy? (V.2) | La segunda respuesta quedaba recortada | La misma corrección | `AlertsFitOnRoundScreenTest` |
| Cuenta regresiva (F.4) | Icono grande encima del texto; **Cancelar quedaba cortado** por el borde redondo | Icono y "Aviso a Laura en" en una línea, en coral, como en el mockup | `AlertsFitOnRoundScreenTest` |
| Laura ya sabe dónde estás (F.4b) | Tarjeta aqua; el mockup usa el icono coral en un círculo tintado, sin tarjeta. **Seguir guiándome** tocaba el borde | `StatusIconBadge` en tono de ayuda, sin tarjeta, con margen bajo el botón | `AlertsFitOnRoundScreenTest` |
| Entendido (V.3 / G.1) | Check suelto; el mockup lo dibuja dentro de un círculo aqua relleno | `StatusIconBadge(isFilled = true)`, conservando el rebote | — (visual) |
| Contactos (pt) y textos de apoyo con Texto grande | Terminaban en "…" | Texto en pt alineado con es; las líneas crecen con el tamaño de letra (`LostTextRole.maxLinesAt`) | `CaptionFitTest`, `LostTextRoleTest` |
| Lugares donde más te desorientas (03.2) | No existía la pantalla | `PlacesScreen`: hasta 3 lugares con icono de la causa, "Cerca de Consultorio 304 · 1 vez · te saliste de la ruta" y "Solo lo ves tú." (1 de noviembre, captura `21`) | `DisorientationPlacesTest`, `LostDatabaseMigrationTest` |
| Complicación (05.1) | Solo texto, sin anillo ni llegada, no abría la app y se actualizaba cada hora | `RANGED_VALUE` (anillo con el avance y la hora de llegada) además de `SHORT_TEXT`, abre Inicio y se actualiza en vivo (`GlanceRefresher`) | `GlanceSummaryMapperTest`, `GlanceRefresherTest` |
| Vista rápida (05.2) | "LOST" y una palabra de estado | "LOST · ahora", "Vas a Casa → U" y "Todo en orden · llegada 7:15" en el color del estado; abre Inicio | `GlanceSummaryMapperTest` (visual en el emulador) |
| Notificación de posible desviación (05.3) | No existía | "LOST · ¿Todo bien en tu ruta?" con Guíame y Estoy bien cuando la app no está a la vista (captura `20`) | `AssistancePromptTest` |
| Errores (10) | Solo se habían revisado en las previews | Los 9 errores se dibujan en el reloj con letra normal y con Texto grande | `ErrorScreensTest` (instrumentada) |
| Tu semana (03.1) | Sin las letras L M M J V y sin marcar el día con desvío | Segmento y letra en ámbar el día en que hubo una alerta (`SegmentedRingLegend`); el resumen lo dice en texto ("1 día con desvío") y TalkBack lee el estado de cada día. Captura `15b` (1 de noviembre) | `GetWeekActivityUseCaseTest`, `InsightsUiStateMapperTest` |

## (b) Decisiones conscientes

| Pantalla (mockup) | El mockup muestra | La app muestra | Por qué |
|---|---|---|---|
| Inicio en modo rutina (C) | Chip · icono + "Vas a la U" · hora · "llegada · 10 de 35 min" | Chip tocable · hora · "Llegas a Casa → U" · Terminar recorrido | Simplificación del 25 de octubre (8 → 4 elementos); el progreso ya lo dice el anillo. |
| Permiso de ubicación (01.2) | Icono de ubicación en un círculo | Sin icono | Con el icono el texto se cortaba y "Ahora no" quedaba bajo el borde ([simplificacion.md](simplificacion.md)). |
| Contacto en el onboarding (01.3) | Fila "Laura · Hermana" + botón + "Elegir otra persona" | Pregunta · Elegir a Laura · nota | La fila hacía lo mismo que el botón. |
| Contactos (04.3) | Inicial "L" en un círculo; Llamar y Quitar permiso lado a lado | Icono de personas; Quitar permiso solo con texto | Se probó la inicial (commit `49883c6`) y empujaba Quitar permiso bajo el borde; se quitó (`b047bb5`). Dos botones lado a lado no caben con etiquetas en pt. |
| ¿Necesitas ayuda? / ¿Vas a otro lado hoy? | Segunda respuesta como píldora gris | Segunda respuesta solo con texto | En el reloj redondo la píldora chocaba con la curva (decisión del 25 de octubre). |
| Monitoreo (D.1–D.2) | Dos líneas sobre fondo negro con arco de similitud | Mapa real con las dos líneas y el chip de estado | Mapa real encendido por defecto (ADR 004); el arco sale de Monitoreo y queda en las alertas. |
| Destinos (02.1) | Título a la izquierda y micrófono redondo arriba a la derecha | Fila "Dime a dónde vas" como primer elemento de la lista | La esquina superior derecha de un reloj redondo queda fuera de la zona segura. |
| Ajustes (04.1) | Valor a la derecha ("Media") e interruptor para Texto grande | Valor debajo del nombre ("Media", "Activado") | Con etiquetas en pt y en Texto grande el valor a la derecha se cortaba. |
| Privacidad (04.4) | "Borrar lo aprendido" como píldora gris | Texto en coral (acción destructiva) y, con Manrope, se llega desplazando | Los textos de privacidad son una decisión ya tomada (incluyen el aviso del mapa); la pantalla se desplaza con la corona y muestra el indicador curvo. |
| Guía (F.3) | Flecha blanca de contorno y "hacia la carrera 7" | Flecha aqua y "≈ 3 min caminando" + "Ya sé dónde estoy" | El nombre de la calle exige geocodificación inversa (otra petición a internet); la salida de la guía se pidió el 25 de octubre. |
| Lugares (03.2) | Dirección de la calle ("Avenida 19 con calle 45") | Destino guardado o rutina ("Cerca de Biblioteca Central", "En tu ruta Casa → U") | La dirección exige enviar a un tercero dónde se desorientó la persona ([ADR 005](../adr/005-lugares-sin-geocodificacion.md)). |
| Vista rápida (05.2) | Aparece sola al reconocer la rutina y se cierra sola | Tile que el usuario agrega al carrusel | Wear OS no tiene una vista transitoria para apps de terceros; la Tile y la complicación se actualizan en vivo. |
| Tu semana (03.1) | "5 de 5" y la frase "Esta semana hiciste tu ruta a la U 5 de 5 veces sin desvíos." | "3 de 5 días" y "3 recorridos · 1 día con desvío" | La frase del mockup supone una sola rutina; el resumen corto cabe en pt y con Texto grande. Cuenta días hábiles: un recorrido del fin de semana no aparece en el anillo. |

## (c) Pendientes

| Pantalla (mockup) | Diferencia | Cómo cerrarla |
|---|---|---|
| Complicación (05.1) | No se vio sobre una esfera: la esfera del emulador es declarativa y no expone huecos por comando | Ponerla en una esfera con huecos en un reloj físico. |

## Capturas

| # | Pantalla | Mockup |
|---|---|---|
| 01 | Inicio en reposo | — |
| 02 | Inicio en recorrido | C |
| 03 | ¿Necesitas ayuda? | F.2 |
| 04 | Cuenta regresiva | F.4 |
| 05 | Laura ya sabe dónde estás | F.4b |
| 06 | Guía | F.3 |
| 07 | Entendido (tras "Ya sé dónde estoy") | G.1 |
| 08 | Mis recorridos | 8.2 |
| 09 | Monitoreo | D.1 |
| 10 | ¿Vas a otro lado hoy? | V.2 |
| 11 | Entendido (desvío voluntario) | V.3 |
| 12 | Privacidad | 04.4 |
| 14 | Detalle de la rutina | 8.3 |
| 15 | Tu semana | 03.1 |
| 19 | Sin ubicación confiable durante un recorrido | 10 |
| 20 | Notificación "¿Todo bien en tu ruta?" | 05.3 |
| 21 | Donde más te desorientas | 03.2 |
| 22 | Vista rápida durante un recorrido | 05.2 |
| 23 | Inicio en pausa tras "Estoy bien" | C |
| 24 | Notificación anunciada sobre la esfera (anillo de LOST abajo) | 05.3 |
| 15b | Tu semana con un día con desvío (datos de prueba en el emulador) | 03.1 |
| 16 | Destinos | 02.1 |
| 17 | Contactos | 04.3 |
| 18 | Ajustes | 04.1 |
