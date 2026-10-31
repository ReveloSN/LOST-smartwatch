# Robustez del motor ante el ruido del GPS

Los GPX de la demo (`sample-routes/`) son trazos limpios sobre calles reales: cada punto cae exactamente en la
acera. El GPS de un reloj no es así: cada punto tiene unos metros de error, a veces rebota en un edificio y a
veces no llega. Esta revisión reproduce los 4 escenarios de la demo con ese ruido y corrige lo que fallaba.

## Cómo se probó

`GpsNoise` (pruebas de `app`) agrega ruido reproducible a cada GPX (misma semilla, mismo resultado):

| Variante | Qué simula |
|---|---|
| Cielo abierto (σ 5 m) | Error gaussiano de 5 m por punto: reloj al aire libre con buena vista del cielo. |
| Ciudad (σ 10 m) | 10 m: calles con edificios de pocos pisos (Fused Location sin Wi-Fi). |
| Ciudad con rebotes | σ 10 m y el 3 % de los puntos salta 40 m (señal que rebota en un edificio). |
| Puntos perdidos | σ 8 m y el 20 % de los puntos no llega (bolsillo, manga). |
| Cañón urbano (fuera de lo realista) | σ 20 m y el 5 % de los puntos salta 60 m. Se documenta, no se exige. |

`GpsNoiseRobustnessTest` exige, para las 4 variantes realistas, la misma historia que sin ruido:

- **Ruta de siempre:** LOST solo observa (ninguna alerta).
- **Desvío voluntario:** nunca pide ayuda (como mucho, la pregunta suave).
- **Calle cerrada:** pregunta o avisa.
- **Hospital sin rutina:** nota las vueltas.

## Resultado: antes → después

Lo que hizo LOST en cada escenario (`calma`, `suave` = "¿Vas a otro lado hoy?", `AYUDA` = "¿Necesitas ayuda?" o
más). En verde lo que corresponde a la historia del escenario.

| Variante | Ruta de siempre | Desvío voluntario | Calle cerrada | Hospital |
|---|---|---|---|---|
| Sin ruido — antes | calma ✓ | suave ✓ | AYUDA ✓ | AYUDA ✓ |
| Sin ruido — después | calma ✓ | suave ✓ | AYUDA ✓ | AYUDA ✓ |
| σ 5 m — antes | **AYUDA ✗** | **AYUDA ✗** | AYUDA ✓ | AYUDA ✓ |
| σ 5 m — después | calma ✓ | suave ✓ | AYUDA ✓ | AYUDA ✓ |
| σ 10 m — antes | **AYUDA ✗** | **AYUDA ✗** | AYUDA ✓ | AYUDA ✓ |
| σ 10 m — después | calma ✓ | suave ✓ | AYUDA ✓ | AYUDA ✓ |
| Rebotes — antes | **AYUDA ✗** | **AYUDA ✗** | AYUDA ✓ | AYUDA ✓ |
| Rebotes — después | calma ✓ | suave ✓ | AYUDA ✓ | AYUDA ✓ |
| Puntos perdidos — antes | **AYUDA ✗** | **AYUDA ✗** | AYUDA ✓ | AYUDA ✓ |
| Puntos perdidos — después | calma ✓ | suave ✓ | AYUDA ✓ | AYUDA ✓ |
| Cañón urbano — antes | AYUDA ✗ | AYUDA ✗ | AYUDA ✓ | AYUDA ✓ |
| Cañón urbano — después | AYUDA ✗ | AYUDA ✗ | AYUDA ✓ | AYUDA ✓ |

**Antes, con solo 5 m de error, la ruta de siempre pedía ayuda**: en la vida real habría sido una falsa alarma en
casi cada recorrido. Después, las 16 combinaciones realistas cuentan la historia correcta.

## Qué fallaba (diagnóstico)

Se registró cada señal punto a punto (`DeviationSignal` envuelto en un espía):

1. **El remuestreo sumaba el temblor como distancia.** Remuestreaba "un punto cada 20 m *a lo largo del rastro*".
   Con pasos de 7 m y 5 m de error, cada paso medía ~10 m: 1 km en línea recta salía con 77 puntos en vez de 50.
2. **Eso rompía el DTW.** El recorrido de hoy tenía muchos más puntos que la rutina; cuando la diferencia superaba
   la banda de Sakoe-Chiba, toda la fila de la matriz quedaba infinita y la divergencia saltaba de 0,20 a 1,00 entre
   un punto y el siguiente.
3. **El temblor parecía giros bruscos.** Con rumbos entre puntos a 20 m, 5 m de error bastan para "girar" 60°:
   `ErraticHeading` llegaba a 1,00 caminando recto. Y como guarda la peor ventana del recorrido, no volvía a bajar.

Ajustar umbrales no alcanzó: con 7 combinaciones de giro, ventana, celda y espaciado, las 4 variantes seguían en
`AYUDA` (la causa estaba en cómo se mide, no en dónde se corta).

## Qué se cambió

| Pieza | Cambio | Por qué |
|---|---|---|
| `ResampleAccumulator` | Avance en **línea recta** desde el último punto dejado, no a lo largo del rastro | El temblor ya no suma metros (1 km → 50 puntos con o sin ruido). |
| `ResampleAccumulator` + `GpsSmoothing` | Cada punto se **promedia con los de los últimos 15 s** (`gpsSmoothingSeconds`) | A 5 s por punto promedia 3–4 y el error baja a la mitad; con GPS en calma (15 s) casi no promedia; guiando (2 s) promedia más. Sin hora creciente no promedia. |
| `ResampleAccumulator` | Un punto aislado más rápido que **5 m/s** (`gpsJumpSpeedMps`) se descarta; nunca dos seguidos | Los rebotes de 40 m dejan de crear giros. Si la persona de verdad va rápido, el rastro no se atasca. |
| `ResampleAccumulator` | El último punto del remuestreo es el **último real**, no el promedio | Para saber dónde está la persona ahora importa no llegar tarde (fuera de ruta y guía). |
| `ErraticHeadingSignal` | El giro en cada punto se mide entre **cuerdas de 40 m** (`headingChordPoints = 2`) | Una esquina real sigue midiendo 90°; el temblor se reduce a la mitad. Con 1 es el comportamiento anterior. |
| `DetectionThresholds.erraticBaselineRatio` | 0,15 → **0,25** | Con las esquinas medidas completas, dos esquinas en 160 m son caminar por la ciudad, no confusión. |

**Los pesos de la regresión (`ScoringWeights`) y los de las señales de confusión no cambiaron.**

## Pruebas nuevas

| Prueba | Qué fija |
|---|---|
| `NoisyResamplingTest` (ai) | 1 km recto con σ 5 m da ≤ 55 puntos; con σ 5 y σ 10 m casi no hay giros bruscos; un salto de 40 m no crea giros. Las 4 fallaban antes. |
| `SignalsTest`: "caminar recto con el temblor del gps no es errático" | Falla con `headingChordPoints = 1` (antes) y pasa con 2. |
| `GpsNoiseRobustnessTest` (app) | Los 4 escenarios × 4 variantes realistas. |

Siguen en verde: las 5 pruebas de escenario de la sección 8.5, los 4 escenarios de `GpxDemoEndToEndTest`,
`IncrementalResamplerTest` (el remuestreo incremental sigue dando lo mismo que el completo) y
`EvaluationPerformanceTest` (una evaluación de 2 151 puntos sigue en 3–4 ms en esta máquina).

## Límites

- **Cañón urbano (σ 20 m con saltos de 60 m):** la ruta de siempre todavía pide ayuda. Con ese error el GPS no
  distingue la acera de la calle de al lado; haría falta usar la precisión que reporta cada punto
  (`Location.accuracy`) para ignorar los puntos malos. Queda como pendiente.
- El ruido es sintético (gaussiano + saltos). Falta grabar recorridos reales con el reloj y repetir esta tabla.
