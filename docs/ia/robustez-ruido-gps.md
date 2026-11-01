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

## Precisión del GPS (`Location.accuracy`) — 1 de noviembre

El reloj informa en cada punto qué tan seguro está: el radio en metros dentro del cual está la persona con un
68 % de confianza. Ahora esa precisión viaja en `GeoPoint.accuracyMeters` (`FusedLocationSource` la llena; los GPX
no la tienen y quedan en `null`) y el motor la usa de tres formas:

| Pieza | Cambio | Por qué |
|---|---|---|
| `GpsSmoothing.isPrecise` + `ResampleAccumulator` | Un punto con precisión peor que **50 m** (`gpsMaxAccuracyMeters`) se ignora siempre; si el recorrido empieza con uno, empieza en el primer punto preciso | El filtro de velocidad descarta un salto aislado, pero **nunca dos seguidos**; el segundo salto de 60 m creaba giros bruscos y una "zona repetida" falsos. Era justo lo que fallaba en el cañón urbano. |
| `GpsWindowAverage` | El promedio de la ventana pesa cada punto por **1 / precisión²** (mínimo 5 m, `gpsAccuracyFloorMeters`) | Es el promedio de mínima varianza: un punto de ±30 m pesa 9 veces menos que uno de ±10 m. |
| `GpsSmoothing.windowSecondsFor` | Con precisión peor que **10 m** (`gpsAccuracyReferenceMeters`) la ventana crece en proporción, hasta **3 veces** (`gpsMaxSmoothingFactor`): 15 s con buena señal, 30 s a ±20 m, 45 s a ±30 m o más | Entre edificios altos, 15 s de promedio no bastan: un desvío tranquilo parecía un ir y venir. |

Sin precisión conocida, todo da exactamente lo mismo que antes (lo fija una prueba).

### Cómo se probó

`GpsNoise` simula además la precisión que informaría el reloj (`AccuracyReport`), con otra semilla para no cambiar
el ruido de cada variante: **ninguna** (como antes), **honesta** (≈ 1,5 σ ± 20 %; un salto llega con su error real)
y **que oculta los saltos** (peor caso: el salto llega con la precisión de un punto bueno).

Además de la semilla 27 de las pruebas, se repitió cada caso con **10 semillas**. Cuántas cuentan la historia
correcta (ruta de siempre: ninguna alerta · desvío voluntario: nunca "¿Necesitas ayuda?" · calle cerrada y hospital:
sí pide ayuda):

| Variante · precisión | Ruta de siempre | Desvío voluntario | Calle cerrada | Hospital |
|---|---|---|---|---|
| σ 5 m · cualquiera | 10/10 | 10/10 | 10/10 | 10/10 |
| σ 10 m · ninguna | 10/10 | **7/10** | 10/10 | 10/10 |
| σ 10 m · honesta | 10/10 | **8/10** | 10/10 | 10/10 |
| Rebotes · ninguna / honesta | 10/10 | **8/10** | 10/10 | 10/10 |
| Puntos perdidos · ninguna | 10/10 | 9/10 | 10/10 | 10/10 |
| Puntos perdidos · honesta | 10/10 | 10/10 | 10/10 | 10/10 |
| Cañón urbano · ninguna | 9/10 | **0/10** | 10/10 | 10/10 |
| Cañón urbano · honesta | **10/10** | **9/10** | 10/10 | 10/10 |
| Cañón urbano · oculta los saltos | 10/10 | 10/10 | 10/10 | 10/10 |

### El costo

Una ventana más larga tarda más en ver las vueltas. Tiempo medio hasta la primera alerta (10 semillas, precisión
honesta):

| Variante | Calle cerrada antes → después | Hospital antes → después |
|---|---|---|
| Sin ruido / σ 5 m | 595 s → 595 s | 365–379 s → igual |
| σ 10 m | 611 s → 606 s | 387 s → 411 s |
| Cañón urbano | 597 s → 610 s | **402 s → 488 s** |

La calle cerrada no cambia (la alerta llega cuando empieza la desorientación del GPX). En el hospital, con la
señal de un cañón urbano, las vueltas se notan **unos 90 s más tarde**. Se eligió la referencia de 10 m porque con
8 m casi no mejora (σ 10 m: 9/10 en el desvío voluntario) y retrasa más.

### Pruebas nuevas

| Prueba | Qué fija |
|---|---|
| `GpsAccuracyTest` (ai, 7) | Dos saltos seguidos marcados como imprecisos no apartan el rastro (y sin precisión sí, más de 10 m); sin precisión el remuestreo es el de siempre; un inicio impreciso se reemplaza; el promedio pesa más el punto preciso; la ventana crece hasta el tope; el remuestreo incremental da lo mismo que el completo. |
| `GpsNoiseRobustnessTest` (app, 16 → 35) | Las 4 variantes realistas también con precisión honesta, y en el cañón urbano: ruta de siempre sin alerta, desvío voluntario sin "¿Necesitas ayuda?", desorientación y vueltas detectadas. |

## Varias semillas: el desvío voluntario con σ 10 m — 1 de noviembre

La tabla de 16 combinaciones de arriba era de **una sola semilla** (27). Con 20 semillas, el desvío voluntario con
σ 10 m pedía ayuda en 3 de 20 (semillas 4, 5 y 9).

### Diagnóstico

Se espiaron las señales en el momento en que LOST pasaba a "¿Necesitas ayuda?" (`GpxStoryHarness(wrapSignals = …)`).
En los tres casos `ErraticHeading` valía 0,5–1,0, y con más de 0,2 el desvío deja de contar como voluntario
(`voluntaryMaxUnsteadiness`). Los giros, con signo, alrededor de una esquina real de la ruta del desvío:

| Recorrido | Giros bruscos (≥ 60°) cerca de la esquina | En la ventana de 8 puntos |
|---|---|---|
| Sin ruido | −90 | 1 |
| Semilla 9 | +70, −62, −101 | 3 → 0,375 → señal 0,5 |
| Semilla 4 | −72, +62, −89, −93 | 4 → 0,5 → señal 1,0 |
| Desorientación (sin ruido) | −179, −154, −67, −74, −70, −115, 179, −114 | 8 |

Dos cosas inflaban la cuenta:

1. **La cuerda de 40 m reparte una esquina en dos giros seguidos del mismo sentido** (−89, −93): es una esquina,
   no dos cambios de dirección.
2. **Un zigzag del temblor más una esquina dan 3 giros en 160 m**, y 3 ya superaba el umbral de 0,25 (2 de 8).

### Corrección

| Pieza | Cambio |
|---|---|
| `ErraticHeadingSignal` | Una racha de giros bruscos **del mismo sentido no más larga que la cuerda** (2 puntos) cuenta como un giro. Una racha más larga es dar vueltas en círculo y cuenta completa (lo fija "dar vueltas cerradas es errático"). |
| `DetectionThresholds` | `erraticBaselineRatio` 0,25 → **0,375** y `erraticSaturationRatio` 0,5 → **0,625**: hasta 3 giros por ventana es caminar por la ciudad; 4 empieza a contar y 5 satura. |
| `Degrees.signedDifferenceTo` | Diferencia de rumbo con signo, para saber hacia qué lado se gira. |

### Resultado con 20 semillas (sin responder la pregunta suave)

| Variante · precisión | Ruta de siempre | Desvío voluntario | Calle cerrada | Hospital |
|---|---|---|---|---|
| Sin ruido | 20/20 | 20/20 | 20/20 | 20/20 |
| σ 5 m · ninguna / honesta | 20/20 | 20/20 | 20/20 | 20/20 |
| σ 10 m · ninguna | 20/20 | 17/20 → **20/20** | 20/20 | 20/20 |
| σ 10 m · honesta | 20/20 | **20/20** | 20/20 | 20/20 |
| Rebotes · ninguna / honesta | 20/20 | **20/20** | 20/20 | 20/20 |
| Puntos perdidos · ninguna / honesta | 20/20 | 20/20 | 20/20 | 20/20 |
| Cañón urbano · honesta | 20/20 | **20/20** | 20/20 | 20/20 |
| Cañón urbano · ninguna (fuera de lo realista) | 20/20 | 3/20 | 20/20 | 20/20 |

La desorientación y el hospital se siguen detectando en todas. `GpsNoiseRobustnessTest` prueba ahora las falsas
alarmas (ruta de siempre y desvío voluntario) con **10 semillas por variante** (195 → 205 casos, ~50 s).

### "Voy a otro lado" deja de comparar con la rutina

En los casos que fallaban tarde, LOST ya había preguntado "¿Vas a otro lado hoy?", pero el arnés no respondía y el
motor seguía comparando con la ruta de siempre (fuera de ruta 1,0, alejarse del destino 1,0, retraso 0,8). Si la
persona responde "Voy a otro lado", ahora la sesión lo recuerda (`TripSessionSnapshot.isGoingElsewhere`) y el resto
del recorrido se evalúa como un lugar nuevo, solo con las señales de confusión. Lo prueban `TripMonitorTest`,
`TripSessionTest` y la historia completa en `GpsNoiseRobustnessTest` (10 semillas).

## Límites

- **Cañón urbano sin precisión:** el desvío voluntario pide ayuda en 17 de 20 semillas, y casi siempre sin pasar
  antes por la pregunta suave. Con la precisión que informa el reloj pasa 20/20.
- Que el reloj informe una precisión **honesta** es un supuesto: si oculta los saltos, en estas variantes la
  ventana larga los absorbe igual, pero hay que confirmarlo con recorridos reales.
- Si el GPS informa más de 50 m durante mucho rato (bajo techo), LOST deja de recibir puntos: no da alertas falsas,
  pero tampoco ve nada. No se muestra todavía el error "No encontramos tu ubicación" en ese caso.
- `accuracyMeters` no se guarda en Room: las rutinas aprendidas se construyen con los puntos ya filtrados por el
  remuestreo.
- El ruido es sintético (gaussiano + saltos). Falta grabar recorridos reales con el reloj y repetir esta tabla.
