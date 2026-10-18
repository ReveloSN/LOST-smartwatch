# LOST — Plan de desarrollo para Claude Code

> App nativa **Wear OS** con IA que aprende los recorridos habituales del usuario y detecta desvíos y posibles situaciones de desorientación.
> Proyecto final de **Diseño de Interfaces de Software**. Lo que más se evalúa es el **diseño** (los 4 PDF del curso); el backend debe demostrar **buenas prácticas de ingeniería**.

---

## 0. Cómo debes trabajar (instrucciones para Claude Code)

1. Lee este archivo completo antes de escribir código. Es la fuente de verdad del proyecto.
2. Los mockups de Claude Design están en `docs/design/`. **En lo visual mandan los mockups**; en la arquitectura y el comportamiento manda este plan. Si hay conflicto, pregunta.
3. Trabaja **por fases** (sección 11). Al terminar cada fase compila, ejecuta las pruebas, muestra un resumen corto y espera confirmación antes de seguir.
4. No crees archivos gigantes. Respeta las reglas de la sección 4 (tamaño de clases y funciones).
5. Antes de crear una clase, verifica que su nombre diga exactamente lo que hace y que tenga una sola razón para cambiar.
6. Todo texto visible al usuario va en `strings.xml`, nunca escrito en el código.
7. Cada fase incluye sus pruebas. No avances con pruebas fallando.
8. **Haz un commit cada vez que termines una funcionalidad o corrección** (ver sección 0.1). Nunca acumules varias funcionalidades en un solo commit.

### 0.1 Commits (Conventional Commits) — OBLIGATORIO

**Cuándo:** un commit por cada funcionalidad, corrección o cambio terminado, con el código compilando y las pruebas pasando. Commits pequeños y atómicos: un cambio lógico por commit.

**Formato:** `tipo(alcance): descripción corta en español, en minúscula y en imperativo`

| Tipo | Cuándo usarlo | Ejemplo |
|---|---|---|
| `feat` | Nueva funcionalidad | `feat(ai): agregar detector de bucles con grilla espacial` |
| `fix` | Corrección de errores | `fix(assistance): corregir cuenta regresiva que no se cancelaba` |
| `refactor` | Mejorar código sin cambiar el comportamiento | `refactor(ai): dividir AnomalyScorer en señales independientes` |
| `style` | Formato, ktlint, espacios (sin cambios de lógica) | `style: aplicar formato ktlint al módulo data` |
| `test` | Agregar o corregir pruebas | `test(ai): agregar escenario de desvío voluntario` |
| `docs` | Documentación | `docs(ia): agregar diagrama de arquitectura de información` |
| `design` | Tokens, tema, componentes visuales | `design(tokens): definir tokens semánticos de estado` |
| `i18n` | Textos y traducciones | `i18n: agregar traducciones al portugués` |
| `perf` | Mejoras de rendimiento | `perf(ai): usar dos filas en la matriz DTW` |
| `build` | Gradle, dependencias, configuración | `build: configurar detekt con umbrales del proyecto` |
| `chore` | Tareas de mantenimiento | `chore: actualizar .gitignore` |

Alcances sugeridos: `ai`, `domain`, `data`, `designsystem`, `tokens`, `onboarding`, `home`, `routines`, `monitoring`, `assistance`, `settings`, `insights`, `nav`.

Si el cambio necesita explicación, agrega un cuerpo después de una línea en blanco que diga **qué** cambió y **por qué**.

### 0.2 Sin marca de agua — OBLIGATORIO
- **Nunca** agregues en los commits líneas como `Co-Authored-By: Claude…`, `Generated with Claude Code`, `🤖`, enlaces de sesión ni ninguna otra firma o atribución de IA.
- Lo mismo aplica para el código, los comentarios, la documentación, los README, los nombres de archivos y las descripciones de pull requests: nada de "generado por IA" ni marcas parecidas.
- El mensaje del commit contiene **solo** el tipo, el alcance, la descripción y (opcionalmente) el cuerpo.

---

## 1. Qué es LOST

**Problema:** una persona puede desorientarse en un campus, un hospital, un aeropuerto o una ciudad desconocida, e incluso en un recorrido que hace todos los días (casa → universidad). Repetir zonas, alejarse del destino o volver al mismo punto puede indicar que necesita ayuda.

**Funcionamiento:** LOST registra los destinos y los patrones habituales. Si detecta una desviación significativa, calcula un nivel de probabilidad y puede **(1)** vibrar para preguntar si el usuario necesita ayuda, **(2)** mostrar una ruta alternativa o **(3)** compartir la ubicación con un contacto autorizado.

**IA:** detección de anomalías, análisis de secuencias de movimiento y reconocimiento de patrones. Compara el comportamiento actual con trayectorias de referencia, **no con una distancia fija**.

**Planteamiento del problema:** ¿Cómo podríamos ayudar a una persona que se desvía o se desorienta a recuperar su ruta en menos de 30 segundos, sin sacar el celular ni pedir ayuda manualmente?

### Conceptos clave del dominio
| Concepto | Significado |
|---|---|
| **Recorrido (Trip)** | Secuencia ordenada de puntos GPS con tiempo, de un origen a un destino. |
| **Rutina (Routine)** | Recorrido que se repite: mismo camino, horario, días, duración y ritmo parecidos. Ej.: Casa → U, L–V, 6:40, 35 min. Es la **trayectoria de referencia**. |
| **Desvío voluntario** | El usuario se sale de la ruta pero camina recto y estable hacia otro lugar → probabilidad baja → pregunta suave "¿Vas a otro lado hoy?". |
| **Desorientación** | Se sale de la ruta **y además** da vueltas, se detiene, cambia de dirección o se aleja → la probabilidad sube. |
| **Modo rutina** | Hay una rutina conocida: se compara contra ella. |
| **Modo lugar nuevo** | No hay rutina (hospital, aeropuerto): se buscan señales generales (bucles, regresos, alejarse del destino). |
| **Nivel de asistencia** | Bajo / Medio / Alto, derivado de la probabilidad. |

---

## 2. Stack técnico

| Necesidad | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.x |
| UI | Jetpack Compose for Wear OS (`androidx.wear.compose:compose-material3`, `compose-foundation`, `compose-navigation`) |
| Arquitectura de presentación | MVVM con flujo de datos unidireccional (UiState inmutable + eventos) |
| Inyección de dependencias | Hilt |
| Asincronía | Coroutines + Flow / StateFlow |
| Persistencia | Room (recorridos y rutinas) + DataStore (preferencias) |
| Ubicación | Fused Location Provider en un Foreground Service |
| Háptica | `Vibrator` / `VibrationEffect` detrás de una interfaz |
| Pruebas | JUnit 5, MockK, Turbine (Flows), kotlinx-coroutines-test, Compose UI Test |
| Calidad | detekt + ktlint (configuración en la sección 4) |
| Simulación | Archivos GPX en `sample-routes/` + modo demo interno |

`minSdk 30`, `targetSdk 34`.

---

## 3. Arquitectura (Clean Architecture + módulos)

```
LOST/
├── app/                 → MainActivity, navegación, DI raíz, ViewModels y pantallas (por feature)
├── core/designsystem/   → Tokens, tema y componentes (Atomic Design). Sin lógica de negocio.
├── domain/              → Kotlin PURO: modelos, interfaces (puertos) y casos de uso. Sin Android.
├── ai/                  → Kotlin PURO: motor de detección (patrones, secuencias, anomalías).
├── data/                → Room, DataStore, ubicación, háptica, contactos (implementan los puertos).
└── docs/                → Diseño, investigación, decisiones (ADR) y métricas.
```

**Regla de dependencias (obligatoria):**
```
app ──► domain ◄── data
 │        ▲
 │        └── ai
 └──► core/designsystem
```
- `domain` y `ai` **no importan nada de Android**, así se prueban con JUnit puro.
- `data` implementa las interfaces de `domain` (inversión de dependencias).
- La UI nunca habla con Room ni con el GPS: solo con ViewModels → casos de uso.

---

## 4. Reglas de código (se revisan en cada fase)

### Tamaño y forma
- **Clase:** idealmente ≤ 100 líneas; **máximo 150**. Si crece, se divide por responsabilidad.
- **Función:** ≤ 20 líneas; máximo 30. Máximo 4 parámetros (si hay más, se usa un `data class`).
- **Un archivo = una clase pública principal.**
- Nada de números mágicos: todos los umbrales viven en `DetectionThresholds` / `DetectionConfig`.

### Nombres que dicen lo que hacen
- Prohibidos los nombres genéricos: `Manager`, `Helper`, `Utils`, `Processor`, `Handler` sin contexto, `Data`, `Info`.
- Las clases son **sustantivos con su rol**: `RoutineLearner`, `TrajectoryResampler`, `DtwSequenceComparator`, `LoopSignal`, `AssistanceLevelClassifier`.
- Los casos de uso son **verbo + objeto**: `LearnRoutineFromTripUseCase`, `EvaluateCurrentTripUseCase`, `ShareLocationWithContactUseCase`.
- Los booleanos se nombran como pregunta: `isOnRoutine`, `hasAuthorizedContact`.

### detekt (umbrales en `config/detekt.yml`)
`LargeClass: 150`, `LongMethod: 30`, `LongParameterList: 4`, `TooManyFunctions: 10 por clase`, `ComplexMethod: 10`, `MagicNumber: activo (excepto en tests y tokens)`.

### Otros principios
- **Inmutabilidad:** modelos de dominio como `data class` con `val`; colecciones de solo lectura hacia afuera.
- **Errores explícitos:** `sealed interface LostError` + `Result`/`Either`; nunca excepciones genéricas hacia la UI.
- **KISS / YAGNI / DRY:** nada de capas o abstracciones que no se usen.
- **Ley de Deméter:** no encadenar `a.b.c.d`.
- **Composición sobre herencia.**
- Comentarios KDoc solo donde se explique el *porqué*.

---

## 5. Principios SOLID aplicados (cómo se ven en LOST)

| Principio | Aplicación concreta |
|---|---|
| **S — Responsabilidad única** | El motor de IA se divide en piezas pequeñas: `TrajectoryResampler` (remuestrea), `DtwSequenceComparator` (compara secuencias), `RoutineLearner` (agrupa recorridos en rutinas), cada `DeviationSignal` (mide una sola señal), `AnomalyScorer` (combina), `AssistanceLevelClassifier` (convierte en nivel), `DeviationExplanationBuilder` (explica por qué). Ninguna clase hace dos cosas. |
| **O — Abierto/cerrado** | Agregar una señal nueva (p. ej., `UnusualStopSignal`) = crear una clase que implemente `DeviationSignal` y registrarla en DI. **No se modifica** `AnomalyScorer`. |
| **L — Sustitución de Liskov** | `RoutineDetectionMode` y `NewPlaceDetectionMode` implementan `DetectionMode` y son intercambiables sin que el caso de uso lo note. `DtwSequenceComparator` y `FrechetSequenceComparator` cumplen el mismo contrato `SequenceComparator`. |
| **I — Segregación de interfaces** | Interfaces pequeñas: `LocationSource`, `HapticNotifier`, `ContactNotifier`, `RoutineReader`, `RoutineWriter`, `TripRecorder`, `MetricsTracker`. Nadie depende de métodos que no usa. |
| **D — Inversión de dependencias** | `domain` define las interfaces; `data` las implementa (`FusedLocationSource`, `AndroidHapticNotifier`, `RoomRoutineRepository`). Hilt las conecta. Los casos de uso solo conocen abstracciones. |

---

## 6. Patrones de diseño (dónde y por qué)

| Patrón | Dónde | Por qué |
|---|---|---|
| **Strategy** | `SequenceComparator` (DTW / Fréchet), `DetectionMode` (rutina / lugar nuevo), `DeviationSignal` | Cambiar el algoritmo sin tocar a quien lo usa. |
| **Composite** | `CompositeAnomalyScorer` agrupa N `DeviationSignal` y combina sus puntajes con pesos | Tratar muchas señales como una sola. |
| **State** | `AssistanceState` (sealed): `Calm`, `SoftAsk`, `AskForHelp`, `Guiding`, `SharingCountdown`, `LocationShared` + `AssistanceStateMachine` | La escalada de ayuda es una máquina de estados explícita y probable con tests. |
| **Chain of Responsibility** | `AssistanceResponsePolicy`: vibrar → ruta alternativa → compartir ubicación | Cada eslabón decide si actúa o pasa al siguiente. |
| **Observer** | `Flow` / `StateFlow` desde `LocationSource` → caso de uso → ViewModel → UI | Flujo reactivo y unidireccional. |
| **Repository** | `RoomRoutineRepository`, `RoomTripRepository` | Aísla la persistencia del dominio. |
| **Adapter** | `FusedLocationSource` adapta Google Play Services a `LocationSource` | El dominio no conoce Google. |
| **Factory** | `DetectionModeFactory` elige el modo según exista una rutina que coincida | Centraliza la decisión de creación. |
| **Facade** | `LostDetectionEngine` expone `evaluate(trip): DetectionResult` y oculta las piezas internas | API simple para el caso de uso. |
| **Mapper** | `TripEntityMapper`, `RoutineEntityMapper`, `LostErrorToUiMessageMapper` | Separa modelos de base de datos, dominio y UI. |
| **Use Case / Interactor** | Un caso de uso por acción del usuario o del sistema | Lógica de aplicación clara y testeable. |

---

## 7. Estructuras de datos (justificadas)

| Estructura | Uso |
|---|---|
| `ArrayDeque<GeoPoint>` como **buffer circular** | Ventana deslizante con los últimos N puntos para medir caos de dirección y rectitud. O(1) al agregar o quitar. |
| **Grilla espacial** `HashMap<GridCell, MutableList<Int>>` | Detectar bucles y regresos al mismo punto: cada punto cae en una celda de 20 m; revisitar una celda tras alejarse es O(1). |
| **Matriz DTW con dos filas** (`DoubleArray`) | Compara secuencias en O(n·m) de tiempo y O(m) de memoria, con banda de Sakoe-Chiba. |
| `PriorityQueue<RoutineMatch>` | Elegir la rutina más parecida al recorrido actual (top-k por similitud). |
| **Estadística acumulada (Welford)** `RunningStatistics` | Media y desviación de la duración de cada rutina sin guardar todos los recorridos. |
| `EnumSet<DayOfWeek>` | Días en que ocurre una rutina. |
| `sealed interface` / `enum class` | Estados de asistencia, niveles, razones de la explicación y errores. |
| `@JvmInline value class` | `Meters`, `Probability`, `Degrees`: tipos seguros que evitan mezclar unidades. |

---

## 8. Motor de IA (módulo `ai/`)

### 8.1 Flujo
```
Puntos GPS ─► TripRecorder ─► TrajectoryResampler ─► DetectionModeFactory
                                                    ├─ RoutineDetectionMode   (hay rutina)
                                                    └─ NewPlaceDetectionMode  (no hay rutina)
                                                            │
                                     DeviationSignals ─► CompositeAnomalyScorer ─► Probability
                                                            │
                         AssistanceLevelClassifier ─► AssistanceLevel + DeviationExplanation
                                                            │
                                      AssistanceStateMachine ─► acción en la UI / háptica
```

### 8.2 Clases del motor (todas cortas, una responsabilidad)
```
ai/
├── geometry/
│   ├── HaversineDistanceCalculator.kt   → distancia entre puntos
│   ├── BearingCalculator.kt             → rumbo y diferencia angular
│   └── TrajectoryResampler.kt           → un punto cada 20 m
├── sequence/
│   ├── SequenceComparator.kt            → interfaz (Strategy)
│   ├── DtwSequenceComparator.kt         → DTW con banda; versión "open-end" para recorridos en curso
│   └── RouteProgressLocator.kt          → en qué punto de la rutina va el usuario (0..1)
├── learning/
│   ├── RoutineLearner.kt                → agrupa recorridos similares en rutinas
│   ├── RoutineMatcher.kt                → ¿este recorrido pertenece a qué rutina? (PriorityQueue)
│   ├── RoutineConfirmationPolicy.kt     → una rutina se confirma tras 5 repeticiones
│   └── RunningStatistics.kt             → Welford (duración media y desviación)
├── signals/
│   ├── DeviationSignal.kt               → interfaz: measure(context): SignalReading (0..1)
│   ├── OffRouteSignal.kt                → distancia a la ruta de siempre
│   ├── SequenceDivergenceSignal.kt      → divergencia DTW de la secuencia
│   ├── DelaySignal.kt                   → tiempo real vs. tiempo esperado en ese punto
│   ├── LoopSignal.kt                    → bucles y regresos (grilla espacial)
│   ├── ErraticHeadingSignal.kt          → cambios bruscos de dirección (buffer circular)
│   ├── MovingAwaySignal.kt              → tendencia a alejarse del destino
│   └── UnusualStopSignal.kt             → se detiene donde normalmente no se detiene
├── scoring/
│   ├── CompositeAnomalyScorer.kt        → combina señales (regresión logística con pesos)
│   ├── VoluntaryDetourDetector.kt       → desvío alto + confusión baja = desvío voluntario
│   └── AssistanceLevelClassifier.kt     → probabilidad → BAJO / MEDIO / ALTO (umbrales personalizados)
├── explanation/
│   └── DeviationExplanationBuilder.kt   → lista de DeviationReason (sin texto; la UI traduce)
├── feedback/
│   └── PersonalThresholdAdjuster.kt     → "Estoy bien" sube el umbral; ayuda confirmada lo baja
├── mode/
│   ├── DetectionMode.kt                 → interfaz (Strategy)
│   ├── RoutineDetectionMode.kt
│   ├── NewPlaceDetectionMode.kt
│   └── DetectionModeFactory.kt
├── config/DetectionThresholds.kt        → todos los umbrales y pesos
└── LostDetectionEngine.kt               → Facade
```

### 8.3 Lógica de puntaje (de referencia, ajustable en `DetectionThresholds`)
- `deviation = max(OffRoute, SequenceDivergence)`
- `confusion = media ponderada(Loop, ErraticHeading, MovingAway, UnusualStop)`
- `z = b0 + w1·deviation + w2·confusion + w3·delay + w4·(deviation·confusion)` → `probability = sigmoid(z)`
- El término `deviation·confusion` es lo que separa **desvío voluntario** (desvío alto, confusión baja → probabilidad baja → `SoftAsk`) de **desorientación** (ambos altos → Medio/Alto).
- **Modo lugar nuevo:** sin `deviation`; usa `confusion` + `MovingAway` respecto al destino marcado.
- **Explicación:** `DeviationReason` es un `sealed interface` (`LeftUsualRoute(meters)`, `RepeatedArea(times)`, `TakingLonger(minutes)`, `MovingAwayFromDestination`, `ErraticDirection`, `UnusualStop`). La UI la convierte en texto humano con `strings.xml`. Ej.: *"Te saliste de tu ruta de siempre y pasaste 3 veces por la misma cuadra."*

### 8.4 Escalada (máquina de estados)
```
Calm ──desvío voluntario──► SoftAsk ──"Voy a otro lado"──► Calm (aprende)
  │                                   └─"Guíame"──────────► Guiding
  └──nivel MEDIO──► AskForHelp ──"Sí, guíame"──► Guiding ──llega a la ruta──► Calm
                       │       └─"Estoy bien"──► Calm (PersonalThresholdAdjuster sube umbral)
                       └─nivel ALTO o 30 s sin respuesta──► SharingCountdown(5 s, cancelable)
                                                              └──► LocationShared
```

### 8.5 Pruebas obligatorias del motor
Con recorridos sintéticos (`TripFixtures`): **rutina normal** → BAJO; **desvío voluntario** → `SoftAsk`; **desorientación con bucles** → MEDIO/ALTO; **lugar nuevo dando vueltas** → MEDIO; **"Estoy bien" repetido** → el umbral sube. Más pruebas unitarias de cada señal, de DTW, del remuestreo, de Welford y de cada transición de la máquina de estados.

---

## 9. Diseño de la interfaz (LO QUE SE EVALÚA)

Cada concepto de los PDF debe ser **visible en el código y en `docs/`**.

### 9.1 PDF "Design Systems" → Arquitectura de Información + Atomic Design + Tokens

**Los 4 sistemas de la arquitectura de información:**
| Sistema | Implementación |
|---|---|
| Organización | Secciones: Inicio · Mis recorridos · Destinos · Contactos · Ajustes |
| Etiquetado | Etiquetas cortas y humanas en `strings.xml` (`nav_home`, `nav_routines`…) |
| Navegación | **Global:** `HorizontalPager`/lista con la corona rotatoria entre secciones. **Local:** `SwipeDismissableNavHost` dentro de cada flujo. Máximo 2 niveles (jerarquía plana). |
| Búsqueda | Búsqueda de destinos por voz (`RecognizerIntent`) + recientes y sugeridos por la IA |

`LostDestination` es un `sealed interface` con todas las rutas de navegación. Diagrama en `docs/ia/arquitectura-informacion.md`.

**Design Tokens en 3 niveles** (`core/designsystem/tokens/`):
```
PrimitiveTokens.kt   → color.teal400, color.amber300, color.coral400, color.gray600, space4..space16, radiusFull, fontSize16..48
SemanticTokens.kt    → status.onRoute, status.deviation, status.help, route.habitual, route.today, bg.base (#000 OLED), text.primary
ComponentTokens.kt   → statusRing.strokeWidth, routeLine.habitualOpacity, primaryButton.container, alertCard.surface
```
Regla: **los componentes solo usan tokens semánticos o de componente, nunca primitivos ni colores literales.**

**Atomic Design** (`core/designsystem/components/`):
```
atoms/      → LostIcon, LostText, LostPrimaryButton, LostChip, StatusDot
molecules/  → DualActionButtons (Sí/No), DistanceIndicator, RoutineCard, ReasonRow
organisms/  → BreathingStatusRing, ProbabilityArc, RouteComparisonMap (doble línea), AlertCard, RoutineCarousel
templates/  → WatchScreenTemplate (zonas seguras redondas), AlertTemplate, ListTemplate
```
Páginas = pantallas de `app/feature/*`. Cada componente tiene `@Preview` para reloj redondo y cuadrado (`@WearPreviewDevices`), en estados normal, error y texto largo.

### 9.2 PDF "Contenido, voz e internacionalización"
- **Orden:** contenido → jerarquía → estructura → estilo. Primero se escriben los textos reales en `strings.xml`; nunca lorem ipsum.
- **Voz (fija):** cercana, tranquila, en "tú". **Tono (variable):** `ToneResolver` asigna un tono según `AssistanceLevel` (relajado / atento / firme), que define qué recurso de texto y qué patrón háptico se usan.
- **Mensaje de error de 3 partes, obligatorio por estructura:**
  ```kotlin
  data class UiMessage(
      @StringRes val whatHappened: Int,
      @StringRes val why: Int?,       // solo si ayuda a actuar
      @StringRes val whatToDo: Int,
  )
  ```
  `LostErrorToUiMessageMapper` convierte `LostError` → `UiMessage`. **Nunca se muestran códigos internos** ("GPS signal lost" → "No encontramos tu ubicación").
- **Lenguaje del usuario, no del sistema:** documentado en `docs/contenido/glosario-voz.md` con una tabla sistema → usuario.
- **Internacionalización:** `values/` (es), `values-en/`, `values-pt/`; `plurals` para "1 vez / 3 veces"; sin anchos fijos en botones (`fillMaxWidth` + `maxLines` + `TextOverflow.Ellipsis` o ajuste de línea); probar con **pseudolocales** (`en-XA`) y con textos un 30 % más largos; formatos de hora y distancia con `Locale`.

### 9.3 PDF "Comunicación con los usuarios"
- **Atributos de personalidad:** calmado, claro, protector, discreto. Documentados en `docs/marca/personalidad.md` y aplicados al tono y a las animaciones.
- **Conexión emocional:** fondo OLED negro, acento aqua y anillo que "respira" (`infiniteTransition`) en estado tranquilo; transición suave hacia el modo asistencia; micro-feedback al confirmar.
- **Iconografía** reconocible y siempre con etiqueta en las acciones críticas.
- **Animaciones y háptica que comunican estado:** `HapticPattern` (enum) `GENTLE`, `DOUBLE`, `INSISTENT` según el nivel.
- **Feedback del usuario:** respuestas "Estoy bien" / "Voy a otro lado" que la IA usa para aprender (con mensaje "LOST aprenderá de esto").

### 9.4 PDF "Investigación, definición y métricas"
- `docs/investigacion/`: personas (Sebastián estudiante casa↔U, Don Jorge adulto mayor, Andrés viajero), journey map, planteamiento del problema y guion de entrevistas.
- **Métricas instrumentadas en código** mediante la interfaz `MetricsTracker` (implementación local `LocalMetricsTracker` que guarda eventos en Room):
  | Métrica | Evento |
  |---|---|
  | Eficacia (tasa de éxito) | `assistance_started` → `route_recovered` / `destination_reached` |
  | Eficiencia (tiempo en tarea) | tiempo entre `alert_shown` y `user_responded`; tiempo hasta recuperar la ruta |
  | Falsos positivos | `alert_dismissed_im_fine` / total de alertas |
  | Uso | rutinas aprendidas, recorridos por semana |
- Pantalla **Insights** que muestra estas métricas al usuario.
- `docs/metricas/sus.md`: cuestionario SUS (10 preguntas) para aplicarlo en las pruebas con usuarios, y la plantilla de resultados.
- Metas: éxito ≥ 90 %, decisión < 5 s, recuperación < 30 s, SUS ≥ 80, falsos positivos < 10 %.

### 9.5 Accesibilidad
Contraste WCAG AA ≥ 4.5:1; el estado **nunca** se comunica solo con color (siempre color + icono + texto + vibración); áreas táctiles ≥ 48 dp; `contentDescription` en todo; soporte para fuente grande; TalkBack lee las alertas.

### 9.6 Pantallas (`app/feature/`)
```
onboarding/      → Bienvenida, permiso de ubicación explicado, contacto autorizado
home/            → Estado tranquilo + modo rutina ("Vas a la U · llegada 7:15")
learning/        → "Aprendiendo tu rutina" (progreso 4/5)
routines/        → Mis recorridos (carrusel) + detalle con mini-mapa
monitoring/      → Recorrido de hoy vs. ruta de siempre (doble línea) + ProbabilityArc + explicación
assistance/      → SoftAsk, AskForHelp, Guiding (flecha), SharingCountdown, LocationShared
newplace/        → Destino por voz + guía en lugar nuevo
insights/        → Métricas semanales
settings/        → Sensibilidad, contactos, idioma, vibración, privacidad
errors/          → Sin GPS, sin conexión, batería baja, estado vacío
complication/    → Complicación de esfera + Tile
```
Cada feature tiene: `XxxScreen.kt` (sin lógica), `XxxViewModel.kt`, `XxxUiState.kt`, `XxxUiEvent.kt`. Los composables reciben estado y lambdas (*state hoisting*); ningún composable accede a repositorios.

---

## 10. Capa de datos (`data/`)
```
location/   FusedLocationSource (Adapter), LocationTrackingService (Foreground Service)
persistence/ LostDatabase, TripDao, RoutineDao, TripEntity, RoutineEntity, *EntityMapper
            RoomTripRepository, RoomRoutineRepository
preferences/ DataStoreSettingsRepository
haptics/    AndroidHapticNotifier
contacts/   AuthorizedContactRepository, SmsContactNotifier (+ FakeContactNotifier para la demo)
metrics/    LocalMetricsTracker
demo/       GpxRouteReader, SimulatedLocationSource (reproduce los GPX: normal, desvío voluntario, desorientación)
```

---

## 11. Fases de trabajo

| # | Fase | Entregable | Criterio de aceptación |
|---|---|---|---|
| 0 | Configuración | `git init` + `.gitignore` de Android, proyecto multi-módulo, Gradle con version catalog, Hilt, detekt y ktlint | Compila; `./gradlew detekt test` pasa; primer commit `build: configurar proyecto multi-módulo` |
| 1 | Design system | Tokens en 3 niveles, `LostTheme`, átomos → organismos con previews | Previews redondo y cuadrado idénticos a los mockups |
| 2 | Dominio | Modelos, `value class`, interfaces (puertos), `LostError`, casos de uso | Sin imports de Android; pruebas en verde |
| 3 | Motor de IA | Todo el módulo `ai/` + `TripFixtures` | Las 5 pruebas de escenario de la sección 8.5 pasan |
| 4 | Datos | Room, DataStore, ubicación, háptica, contactos, métricas | Pruebas de repositorios y mappers |
| 5 | Pantallas | Todas las features con ViewModel + UiState | Navegación global/local funcionando |
| 6 | Integración | Servicio de ubicación → motor → máquina de estados → UI + háptica | Demo con GPX: desvío detectado y guía de regreso |
| 7 | Contenido e i18n | Textos finales es/en/pt, `UiMessage` de 3 partes, pseudolocale | Ningún texto escrito en el código; sin cortes en en-XA |
| 8 | Accesibilidad y métricas | Contraste, descripciones, `MetricsTracker`, pantalla Insights | Revisión con Accessibility Scanner |
| 9 | Documentación | `docs/` completo + README de ejecución + ADRs | Todo lo de la sección 9 está documentado |

---

## 12. Documentación (`docs/`)
```
docs/
├── design/          → mockups de Claude Design (fuente visual)
├── investigacion/   → personas, journey map, problema
├── ia/              → arquitectura de información (diagrama), motor de IA explicado
├── contenido/       → voz y tono, glosario sistema → usuario, mensajes de error
├── marca/           → personalidad y atributos
├── metricas/        → métricas, SUS, resultados
└── adr/             → decisiones: 001-clean-architecture, 002-dtw-vs-distancia-fija, 003-maquina-de-estados
```

## 13. Definición de "terminado" (por cada clase o pantalla)
- [ ] Nombre que describe exactamente lo que hace; una sola responsabilidad.
- [ ] ≤ 150 líneas por clase, ≤ 30 por función; detekt sin alertas.
- [ ] Depende de interfaces, no de implementaciones.
- [ ] Usa tokens (nunca colores o tamaños literales) y `strings.xml`.
- [ ] Tiene pruebas (lógica) o previews en sus estados (UI).
- [ ] Accesible: contraste, `contentDescription`, área táctil.
- [ ] Funciona en es, en y pt sin cortes de texto.
- [ ] Tiene su commit con formato Conventional Commits (`feat:`, `fix:`, …) y **sin ninguna marca de agua ni atribución de IA**.
