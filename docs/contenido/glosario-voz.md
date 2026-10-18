# Voz, tono y glosario de LOST

> PDF "Contenido, voz e internacionalización". Orden de trabajo: **contenido → jerarquía → estructura → estilo**.
> Todos los textos reales viven en `app/src/main/res/values*/strings.xml` (nunca lorem ipsum ni texto en el código).

## Voz (fija)

| Atributo | Cómo se escribe | Ejemplo |
|---|---|---|
| Cercana | En "tú", frases cortas, verbos de acción. | "Vas a la U." |
| Tranquila | Sin alarmas, sin signos de exclamación, sin rojo puro. | "Sigue la flecha. Vas bien." |
| Protectora | Ofrece ayuda antes de que la pidan, explica el porqué. | "Te guío y aviso a tu contacto." |
| Discreta | Solo habla cuando hace falta; si es un desvío voluntario, no insiste. | "¿Vas a otro lado hoy?" |

## Tono (variable según el nivel)

El tono lo decide `ToneResolver` (`app/.../ui/ToneResolver.kt`) a partir del `AssistanceLevel`. Cada tono define el texto principal **y** el patrón háptico (`Tone.kt`).

| Nivel | Tono | Texto principal | Contexto | Vibración |
|---|---|---|---|---|
| Bajo / desvío voluntario | Relajado | "¿Vas a otro lado hoy?" | — | Suave (1 × 40 ms) |
| Medio | Atento | "¿Necesitas ayuda?" | "Te saliste de tu ruta de siempre." | Doble (2 × 80 ms) |
| Alto | Firme y claro | "Creo que necesitas ayuda" | "Te guío y aviso a tu contacto." | Insistente (3 × 150 ms) |

## Glosario: lenguaje del sistema → lenguaje del usuario

| El sistema dice | LOST dice |
|---|---|
| Dashboard | Inicio |
| Patterns / rutinas aprendidas | Mis recorridos |
| Trayectoria de referencia | Tu ruta de siempre |
| Deviation detected | Te saliste de tu ruta de siempre |
| Anomaly score 0.82 | (nunca se muestra) "Creo que necesitas ayuda" |
| Loop detected, 3 revisits | Pasaste 3 veces por la misma cuadra |
| Heading variance high | Cambiaste de rumbo varias veces |
| Distance to destination increasing | Te estás alejando de tu destino |
| Dwell time anomaly | Te detuviste donde nunca te detienes |
| ETA delta +12 min | Llevas 12 minutos más de lo normal |
| Recalcular | Volver a tu ruta de siempre |
| SOS trigger | Pedir ayuda / Aviso a Laura |
| Umbral de anomalía | Sensibilidad (Baja · Media · Alta) |
| False positive feedback | "Estoy bien" / "Voy a otro lado" |
| Model updated | Entendido. LOST aprenderá de esto. |
| Geofence / new place mode | Lugar nuevo |
| Trusted contact | Contacto autorizado |
| GPS signal lost | No encontramos tu ubicación. |
| Network error | No pudimos avisar a tu contacto. |
| Location permission denied | LOST no tiene permiso para usar tu ubicación. |
| Purge training data | Borrar lo aprendido |

## Mensajes de error: qué pasó · por qué · qué hacer

Obligatorio por estructura: `UiMessage(whatHappened, why?, whatToDo)` y `LostErrorToUiMessageMapper` (`app/.../ui/`). El "por qué" solo aparece si ayuda a decidir.

| `LostError` | Qué pasó | Por qué | Qué hacer |
|---|---|---|---|
| `LocationUnavailable` | No encontramos tu ubicación. | Puede que estés bajo techo. | Camina hacia una salida o ventana. |
| `NoConnection` | No pudimos avisar a tu contacto. | No hay conexión ahora. | Lo intentaremos de nuevo automáticamente. |
| `BatteryLow(10)` | Batería al 10 %. | — | LOST seguirá cuidando tu ruta en modo ahorro. |
| `LocationPermissionDenied` | LOST no tiene permiso para usar tu ubicación. | Sin ubicación no puede aprender tus recorridos. | Actívalo en los ajustes del reloj. |
| `NoAuthorizedContact` | Aún no tienes un contacto autorizado. | — | Elige uno en Contactos. |
| `NoRoutinesYet` | Aún no conocemos tus recorridos. | — | Camina como siempre y LOST aprenderá. |
| `StorageFailure` | No pudimos guardar tus datos. | — | Intenta de nuevo en un momento. |

## Internacionalización

- Idiomas: `values/` (español, base), `values-en/`, `values-pt/` en `app` y `data`. Idiomas por app en Android 13+ (`generateLocaleConfig`, `resources.properties`).
- `plurals` para "1 vez / 3 veces", "1 minuto / 12 minutos", "segundo / segundos" (con la categoría `many` que exigen español y portugués).
- Sin anchos fijos: los botones usan `fillMaxWidth` y hasta 2 líneas; títulos 2 líneas, cuerpo 3; luego "…". Guion suave automático (`Hyphens.Auto`).
- Horas y distancias con el `Locale` del usuario (`formatMinuteOfDay`, `formatDistance`). Días de la semana con `DayOfWeek.getDisplayName`.
- Pseudolocales activos en *debug* (`en-XA` alarga ~30 % el texto, `ar-XB` invierte la dirección) para encontrar cortes.
- `TranslationsTest` falla la compilación si falta una traducción, si cambia un marcador (`%1$s`, `%2$d`) o si una pantalla escribe texto en el código.
