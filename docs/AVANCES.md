# LOST — Avances del proyecto

Registro de lo que se hizo en cada fase del plan (sección 11 de `CLAUDE.md`), las pruebas que pasan,
las decisiones tomadas y lo que queda pendiente.

## Resumen de fases

| # | Fase | Estado |
|---|---|---|
| 0 | Configuración | ✅ Completa |
| 1 | Design system | ✅ Completa |
| 2 | Dominio | ✅ Completa |
| 3 | Motor de IA | ✅ Completa |
| 4 | Datos | ✅ Completa |
| 5 | Pantallas | ✅ Completa |
| 6 | Integración | ⏳ |
| 7 | Contenido e i18n | ⏳ |
| 8 | Accesibilidad y métricas | ⏳ |
| 9 | Documentación | ⏳ |

---

## Fase 0 — Configuración

**Qué se hizo**
- Proyecto base de Android Studio ("Empty Wear App With Tile And Complication", `com.lost.watch`, minSdk 30).
- Estructura multi-módulo: `app`, `core:designsystem`, `domain`, `ai`, `data`.
  - `domain` y `ai` son módulos **Kotlin/JVM puros** (sin Android) → se prueban con JUnit 5.
  - `data` y `core:designsystem` son librerías Android.
- Version catalog (`gradle/libs.versions.toml`) con todas las dependencias.
- Hilt (`LostApplication` con `@HiltAndroidApp`, `MainActivity` con `@AndroidEntryPoint`) usando KSP.
- detekt 1.23.8 con `config/detekt.yml` (clase ≤ 150, función ≤ 30, ≤ 4 parámetros, `MagicNumber` activo excepto tests y tokens).
- ktlint 1.8 (estilo `android_studio`, `.editorconfig`) mediante tareas `ktlintCheck` / `ktlintFormat`.

**Comandos de verificación**
```bash
./gradlew ktlintCheck detekt test assembleDebug
```

**Decisiones**
- *AGP 9 trae Kotlin integrado*: el plugin de Gradle de ktlint no detecta los source sets de Android, así que ktlint se ejecuta con su CLI desde tareas propias del proyecto raíz (mismo resultado, sin depender del plugin).
- *JDK del daemon de Gradle = 21*: la plantilla pedía JDK 25, pero detekt 1.23 no corre sobre JDK 25. Se cambió con `./gradlew updateDaemonJvm --jvm-version=21`.
- *targetSdk 37*: la plantilla actual de Android Studio ya usa 37; `CLAUDE.md` dice 34. Se dejó 37 porque Google Play exige el más reciente y no cambia nada del diseño.
- Kotlin 2.3.21, KSP 2.3.12, Hilt 2.60.1, Room 2.8.5.

**Pendientes**
- Ninguno.

---

## Fase 1 — Design system (`core/designsystem`)

**Diseño**
- **Tokens en 3 niveles** (`tokens/`):
  - `PrimitiveTokens`: paleta de los mockups (teal 400 `#3EE6D2`, teal 950, amber 300, coral 400, neutrales), espacios, radios, tamaños de fuente, opacidades y duraciones.
  - `SemanticTokens`: `Status.onRoute/deviation/help`, `Route.habitual/today(tone)`, `Background.base` (#000 OLED), `Text.primary/secondary/onAccent`, `Surface.glass/tinted`, espaciado y formas.
  - `ComponentTokens`: `StatusRing` (grosor, opacidad de pista 14 %, margen de zona segura), `RouteLine` (habitual 28 %), botones (alto ≥ 48 dp), `AlertCard` (superficie tintada 12 %), `SafeArea` (70 % del ancho en redondo).
  - Regla cumplida: los componentes solo usan tokens semánticos o de componente.
- `LostTheme` conecta los tokens con Material 3 de Wear OS; `LostTypography` define la escala Display · Número · Pregunta · Título · Cuerpo · Mínimo.
- **Atomic Design** (`components/`):
  - Átomos: `LostText` (+ `LostTextRole`), `LostIcon`, `LostPrimaryButton`, `LostSecondaryButton`, `LostChip`, `StatusDot`.
  - Moléculas: `DualActionButtons`, `DistanceIndicator`, `RoutineCard`, `ReasonRow`.
  - Organismos: `BreathingStatusRing` (respira en calma, se detiene en desvío, se completa en ayuda), `ProbabilityArc`, `SegmentedProgressRing` (aprendizaje 4/5 y semana), `RouteComparisonMap` (doble línea + pulso en la separación), `AlertCard`, `RoutineCarousel` (lista circular con corona).
  - Plantillas: `WatchScreenTemplate` (zona segura redonda/cuadrada, desplazable), `AlertTemplate`, `ListTemplate`.
- Cada componente tiene previews con `@LostDevicePreviews` (= `@WearPreviewDevices`: redondo pequeño, redondo grande, cuadrado y rectangular) en estado normal y, donde aplica, texto largo (+30 %) y error.
- Accesibilidad desde el sistema: `contentDescription` obligatorio en `LostIcon`, regiones vivas en alertas, botones de ancho completo con alto mínimo 52 dp, texto en `sp`.

**Pruebas**
- `ContrastRatioTest` (JUnit 5): texto y estados superan WCAG AA (4.5:1) sobre negro OLED; también los textos de los botones principal y secundario.

**Decisiones**
- *Conversión de medidas*: los mockups están en px de una pantalla de 454 px (≈ 2x). Se dividió entre dos y se ajustó a la escala de Wear OS (ej.: Display 96 px → 44 sp; mínimo 16 px → 12 sp).
- *Fuente*: los mockups usan Manrope. Por ahora se usa la sans del sistema detrás del token `LostTypography.brandFamily`; cambiar a Manrope es una sola línea cuando se agregue el archivo de fuente (licencia OFL) en `res/font`.
- *Iconos*: Material Symbols Rounded de los mockups → `material-icons-extended` (variante Rounded).
- *Botón doble apilado*: los mockups muestran "Sí, guíame" encima de "Estoy bien"; se apilan para dar a cada respuesta el ancho completo.
- Los textos de las previews viven en `PreviewSamples` (no llegan al usuario; la app usa `strings.xml`).

**Pendientes**
- Comparar visualmente las previews contra los mockups en Android Studio (no se puede renderizar previews desde la línea de comandos sin una herramienta de screenshots).
- Empaquetar Manrope.

---

## Fase 2 — Dominio (`domain`, Kotlin puro)

**Backend**
- Modelos inmutables: `GeoPoint`, `Trip`, `Routine` (trayectoria de referencia + `RoutineSchedule` con `EnumSet<DayOfWeek>` + `DurationStatistics` para Welford), `Destination`, `AuthorizedContact`, `UserSettings`, `Sensitivity`, `PersonalThresholds`.
- Tipos seguros `@JvmInline value class`: `Meters`, `Probability` (valida 0..1), `Degrees` (diferencia angular más corta).
- Detección: `AssistanceLevel`, `DeviationReason` (sealed, sin texto), `DetectionResult`, `DetectionRequest`.
- Errores explícitos: `sealed interface LostError` + `LostResult` (nunca excepciones hacia la UI).
- **Patrón State**: `AssistanceState` (Calm, SoftAsk, AskForHelp, Guiding, SharingCountdown, LocationShared, Acknowledged) + `AssistanceStateMachine` pura.
- **Cadena de responsabilidad**: `AssistanceResponsePolicy` = `VibrateAndAskLink` → `AlternativeRouteLink` → `ShareLocationLink`.
- **Puertos (ISP)**: `LocationSource`, `HapticNotifier`, `ContactNotifier`, `RoutineReader`/`RoutineWriter`, `TripRecorder`/`TripReader`, `MetricsTracker`/`MetricsReader`, `SettingsReader`/`SettingsWriter`, `ContactReader`/`ContactWriter`, `DestinationReader`/`DestinationWriter`, `TripEvaluator` (el motor de IA visto desde el dominio), `RoutineLearningEngine`, `ThresholdAdjuster`, `TimeSource`, `IdGenerator`.
- **Casos de uso (verbo + objeto)**: `EvaluateCurrentTripUseCase`, `LearnRoutineFromTripUseCase`, `ShareLocationWithContactUseCase`, `ApplyUserFeedbackUseCase`, `TrackMetricEventUseCase`, `ObserveWeeklyInsightsUseCase`, `ObserveRoutinesUseCase`, `ConfirmRoutineUseCase`, `RenameRoutineUseCase`, `DeleteRoutineUseCase`, `DeleteLearnedDataUseCase`, `ObserveSettingsUseCase`, `UpdateSettingsUseCase`, `ObserveAuthorizedContactUseCase`, `ChooseAuthorizedContactUseCase`, `RevokeAuthorizedContactUseCase`, `ObserveDestinationsUseCase`, `SaveDestinationUseCase`.
- `InsightsCalculator`: eficacia, tiempo de decisión, tiempo de recuperación, falsos positivos y uso, con las metas del plan (≥ 90 %, < 5 s, < 30 s, < 10 %).

**Pruebas** — 50 pruebas JUnit 5 en verde (`./gradlew :domain:test`): cada transición de la máquina de estados, la cadena de respuesta, las value classes, el cálculo de métricas y los casos de uso (fakes, MockK y Turbine).

**Decisiones**
- *Cancelar la cuenta regresiva* vuelve a Calm (el usuario demostró que está presente) sin tocar los umbrales.
- *Nivel alto sin contacto autorizado*: no hay a quién avisar, así que LOST pregunta (AskForHelp) en vez de iniciar la cuenta.
- *Mientras guía*, nuevas lecturas no interrumpen la guía; solo "llegó a la ruta" o "llegó al destino" la terminan.
- La sensibilidad se modela como factor sobre la duración habitual: con 35 min → Baja 65, Media 50, Alta 42 min (lámina 04.2).

**Pendientes**
- Ninguno.

---

## Fase 3 — Motor de IA (`ai`, Kotlin puro)

**IA**
- Flujo: puntos GPS → `TrajectoryResampler` (un punto cada 20 m) → `DetectionModeFactory` (rutina / lugar nuevo) → señales → `CompositeAnomalyScorer` → `AssistanceLevelClassifier` + `VoluntaryDetourDetector` + `DeviationExplanationBuilder` → `DetectionResult`.
- `geometry/`: `HaversineDistanceCalculator`, `BearingCalculator`, `LocalProjection`, `PolylineDistanceCalculator`, `TrajectoryResampler`, `SpatialGrid` (`HashMap<GridCell, MutableList<Int>>`).
- `sequence/`: `SequenceComparator` (Strategy) con `DtwSequenceComparator` (dos filas `DoubleArray`, banda de Sakoe-Chiba, versión open-end) y `FrechetSequenceComparator` (mismo contrato → Liskov); `RouteProgressLocator`.
- `learning/`: `RoutineLearner` (implementa el puerto `RoutineLearningEngine`), `RoutineMatcher` (`PriorityQueue<RoutineMatch>`), `RoutineConfirmationPolicy` (5 repeticiones), `RunningStatistics` (Welford).
- `signals/` (cada una mide una sola cosa): `OffRouteSignal`, `SequenceDivergenceSignal`, `DelaySignal` (depende de la sensibilidad), `LoopSignal` (grilla espacial), `ErraticHeadingSignal` (buffer circular `ArrayDeque`), `MovingAwaySignal`, `UnusualStopSignal` (una pasada O(n)).
- `scoring/`: `CompositeAnomalyScorer` (Composite) con `z = b0 + w1·desvío + w2·confusión + w3·retraso + w4·desvío·confusión`; `VoluntaryDetourDetector`; `AssistanceLevelClassifier` con umbrales personales.
- `feedback/PersonalThresholdAdjuster` (implementa `ThresholdAdjuster`): "Estoy bien" sube los umbrales, ayuda confirmada los baja, con límites.
- `mode/`: `DetectionMode` (Strategy) con `RoutineDetectionMode` y `NewPlaceDetectionMode`, creados por `DetectionModeFactory` (Factory).
- `LostDetectionEngine` (Facade, implementa el puerto `TripEvaluator`) y `LostEngineAssembly` (arma las piezas; el módulo no depende de Hilt).
- Todos los umbrales y pesos en `DetectionThresholds`, `ScoringWeights`, `DetectionConfig` y `FeedbackSteps`.

**Pruebas** — 34 pruebas en verde (`./gradlew :ai:test`), incluidas las **5 de escenario de la sección 8.5** (`DetectionScenariosTest`) con recorridos sintéticos (`TripFixtures`, `SyntheticWalk`):
1. Rutina normal → nivel BAJO, modo rutina, avance > 50 %.
2. Desvío voluntario (camina recto hacia otro lado) → BAJO + `isVoluntaryDetour` → la máquina de estados pasa a `SoftAsk`.
3. Desorientación con 3 vueltas a la manzana → MEDIO/ALTO, razones "saliste de tu ruta" y "misma cuadra ×3".
4. Lugar nuevo (hospital) dando vueltas → MEDIO en modo lugar nuevo.
5. "Estoy bien" ×3 → el umbral sube y el mismo recorrido pasa de MEDIO a BAJO.
Además: geometría, remuestreo, DTW/Fréchet, avance en la ruta, Welford, aprendizaje con 5 repeticiones, emparejamiento, cada señal y el puntaje combinado (agregar una señal sin tocar el scorer).

**Decisiones**
- *Pesos de la regresión (modo rutina)*: b0 = −4, desvío 1.5, confusión 2, retraso 1, interacción 8. Con pesos más altos en desvío y retraso, un desvío voluntario largo terminaba en nivel medio; el término de interacción es el que realmente separa los dos casos.
- *Desvío voluntario* = desvío ≥ 0.5 y "inestabilidad" (máximo de bucles, giros bruscos y paradas) < 0.2. "Alejarse del destino" no cuenta como inestabilidad porque quien va a otro lado también se aleja.
- *Giros bruscos*: se resta una línea base (15 % de giros por ventana) para que una esquina normal no cuente como caos.
- *Emparejar un recorrido en curso*: se elige la rutina que sale del mismo lugar con menor costo DTW aunque hoy se haya desviado (si se exigiera parecido, un desvío fuerte "perdería" la rutina y caería a modo lugar nuevo).
- *Nombres de rutinas nuevas*: el motor no genera texto; deja el nombre vacío y la UI muestra "Nuevo recorrido" desde `strings.xml`.
- detekt: máximo 4 parámetros por función (umbral 5). Los constructores de inyección pueden tener hasta 6 dependencias.

**Pendientes**
- Calibrar los pesos con recorridos reales (los actuales se ajustaron con los escenarios sintéticos).

---

## Fase 4 — Datos (`data`)

**Backend**
- **Room** (`persistence/`): `LostDatabase` con `TripEntity`, `RoutineEntity`, `MetricEventEntity`, `DestinationEntity`; DAOs; mappers (`TripEntityMapper`, `RoutineEntityMapper`, `MetricEventEntityMapper`, `DestinationEntityMapper`); codecs compactos para puntos y días. Repositorios **(patrón Repository)**: `RoomTripRepository`, `RoomRoutineRepository`, `RoomDestinationRepository`. Esquema exportado en `data/schemas/`.
- **DataStore** (`preferences/`): `DataStoreSettingsRepository` (sensibilidad, vibración, texto grande, bienvenida y umbrales personales) con `SettingsPreferencesMapper`.
- **Contactos** (`contacts/`): `AuthorizedContactRepository` (uno solo, revocable), `SmsContactNotifier` (devuelve `LostError.NoConnection` si el reloj no puede enviar), `FakeContactNotifier` (demo) y `ActiveContactNotifier` que elige según el modo.
- **Ubicación** (`location/`): `FusedLocationSource` **(Adapter)** de Google Play Services → `LocationSource`; `ActiveLocationSource` elige GPS real o simulado.
- **Háptica** (`haptics/`): `AndroidHapticNotifier` + `HapticWaveforms` (suave 40 ms · doble 2×80/120 · insistente 3×150 · giro largo+corto) con la intensidad de Ajustes.
- **Métricas** (`metrics/`): `LocalMetricsTracker` guarda eventos en Room (nada sale del reloj).
- **Demo** (`demo/`): `GpxRouteReader`, `SimulatedLocationSource` (reproduce un GPX 10× más rápido conservando los tiempos reales), `DemoScenarioSelector`, `DemoDataSeeder` (rutina "Casa → U", Laura y destinos de ejemplo).
- **GPX** en `sample-routes/` (normal, desvío voluntario, desorientación con regreso, lugar nuevo) generados por `generate_routes.py` con la misma geometría que las pruebas del motor.
- **Hilt**: `StorageModule` (base de datos, DataStore, alcance de la app) y `DataBindingsModule` (cada puerto → su implementación).

**Pruebas** — 21 pruebas en verde (`./gradlew :data:testDebugUnitTest`): codecs, mappers ida y vuelta, repositorio de rutinas con DAO en memoria (Turbine), DataStore de ajustes y contacto, lectura de todos los GPX, reproducción simulada y ondas hápticas.

**Decisiones**
- *Modo demo = compilación debug* (`BuildConfig.DEMO_MODE`): GPS simulado con los GPX y aviso al contacto simulado. En release se usan el GPS y el SMS reales. Así nunca se envía un SMS real durante una presentación.
- *Puntos codificados como texto* en una columna en lugar de una tabla por punto: un recorrido se lee y se escribe completo, y no hace falta una librería de JSON.
- *Pruebas de repositorios con DAOs en memoria*: los DAO de Room son interfaces; probarlos contra SQLite real requeriría Robolectric o un emulador.
- En Windows el DataStore en archivo no puede reemplazar un archivo abierto; las pruebas con varias escrituras usan un DataStore en memoria.

**Pendientes**
- Pruebas instrumentadas de los DAO de Room en un emulador (`androidTest`).

---

## Fase 5 — Pantallas (`app`)

**Diseño**
- **Arquitectura de información**: 5 secciones globales (Inicio · Mis recorridos · Destinos · Contactos · Ajustes) en un `HorizontalPager` con indicador de sección; navegación local con `SwipeDismissableNavHost` (deslizar = volver), nunca más de 2 niveles. `LostDestination` (sealed interface) define todas las rutas.
- **Capa de alerta fuera del árbol** (`AssistanceOverlay`): se superpone a cualquier pantalla con un fundido suave y vuelve a donde estaba el usuario.
- Pantallas de la sección 9.6, cada una con `XxxScreen` (sin lógica, con *state hoisting*), `XxxViewModel`, `XxxUiState`, `XxxUiEvent`:
  - `onboarding/`: Bienvenida, permiso de ubicación explicado, contacto autorizado.
  - `home/`: anillo que respira con avance sobre la ruta, "Vas a la U · 7:15 · llegada".
  - `learning/`: "4 de 5" con anillo segmentado.
  - `routines/`: lista circular, vacío, detalle con mini-mapa, guardar, renombrar por voz y borrar con confirmación.
  - `monitoring/`: doble línea (siempre vs. hoy), arco de probabilidad, "Lo que noto hoy" y "¿Por qué te pregunto?" en una frase humana.
  - `assistance/`: pregunta suave, pedir ayuda, guía con flecha, cuenta regresiva cancelable, aviso enviado (o error de 3 partes), "Entendido. LOST aprenderá de esto." con rebote y "Ya estás en tu ruta".
  - `destinations/` (búsqueda por voz con `RecognizerIntent`, sugeridos y recientes), `newplace/` (lugar nuevo, guía y llegada), `contacts/` (permiso revocable), `settings/` (sensibilidad con ejemplo real, vibración, texto grande, idioma, privacidad con pulsación larga), `insights/` (Tu semana + métricas), `errors/` (qué pasó · por qué · qué hacer), `demo/`.
  - `tile/` y `complication/`: estado de LOST en un vistazo.
- Modo **texto grande** (+30 %) aplicado a toda la app desde Ajustes.
- `ui/`: `StatusPresentation` (color + icono + texto), `PlaceIcons`, razones y frase explicable con plurales, distancias y horas según el idioma, `RouteProjector` para el mini-mapa, `UiMessage` y `LostErrorToUiMessageMapper`.
- Nuevos componentes del design system: `PageIndicator` y `LostListItem` (`RoutineCard` lo reutiliza).

**Pruebas** — 26 pruebas de la app en verde (`./gradlew :app:testDebugUnitTest`): mappers de Inicio, Monitoreo, Asistencia y Tu semana; ViewModel de asistencia con la sesión real; búsqueda de destinos por voz; mini-mapa; mensajes de error.

**Decisiones**
- *Pager horizontal para la navegación global*: los mockups proponen corona/deslizamiento vertical, pero las listas de cada sección ya usan la corona para desplazarse; con un pager vertical ambos gestos chocarían. El plan (sección 9.1) también menciona `HorizontalPager`.
- *Borrar* (rutina, contacto) pide un segundo toque; *Borrar lo aprendido* exige mantener presionado, como en el mockup.
- *Flecha de guía* apunta al rumbo absoluto (norte arriba); falta combinarla con la brújula del reloj.
- *Elegir otra persona* como contacto: en un reloj no hay un selector de contactos estándar; en la demo se usa a Laura y el contacto se puede quitar en Contactos.
- En modo demo, Inicio no bloquea por permisos para que la presentación siempre funcione.

**Pendientes**
- Girar la flecha con la brújula (sensor de orientación).
- Selector de contacto real (desde el teléfono vinculado).
- Pruebas de UI con Compose en emulador.
