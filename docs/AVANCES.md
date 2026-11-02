# LOST — Avances del proyecto

Registro de lo que se hizo en cada fase del plan (sección 11 de `CLAUDE.md`), las pruebas que pasan,
las decisiones tomadas y lo que queda pendiente.

## Resumen para la entrega

- **Las 10 fases del plan están completas.** El trabajo desde el 25 de octubre está en la rama `mejoras-ui-mapa`, sin merge a `main`.
- **489 pruebas automáticas en verde** (designsystem 11 · domain 120 · ai 51 · data 31 · app 276) y **23 instrumentadas** en el emulador de Wear OS, más ktlint, detekt y Android Lint sin errores. Comando: `./gradlew ktlintCheck detekt test assembleDebug :app:lintDebug`.
- **Demo con GPX** verificada de punta a punta por prueba: rutina normal (silencio), desvío voluntario (pregunta suave), calle cerrada (pregunta → guía → ruta recuperada) y hospital (pregunta → llegada).
- **Diseño** fiel a los mockups: tokens en 3 niveles, Atomic Design, previews redondo/cuadrado, voz y tono, 3 idiomas, accesibilidad verificada por prueba.
- **Mejoras del 25 de octubre** (rama `mejoras-ui-mapa`): interfaz simplificada y verificada en un emulador de Wear OS, **mapa real de OpenStreetMap**, destinos reales por voz, demo sobre calles reales, flecha con brújula y optimización (APK de 57 MB a 3,6 MB, GPS adaptativo).
- **Jornada del 1 de noviembre**: IA robusta con varias semillas y con la precisión del GPS, aviso de ubicación perdida, notificación 05.3, complicación y vista rápida en vivo (05), "Donde más te desorientas" (03.2), guía con vibración de giro, selector de contactos del reloj, icono propio y pantallas de error probadas en el reloj.
- **Lo que falta** (ver "Pendientes generales" al final): prueba con usuarios (SUS), calibrar la IA con recorridos reales y probar voz, brújula y la complicación en un reloj físico.

## Resumen de fases

| # | Fase | Estado |
|---|---|---|
| 0 | Configuración | ✅ Completa |
| 1 | Design system | ✅ Completa |
| 2 | Dominio | ✅ Completa |
| 3 | Motor de IA | ✅ Completa |
| 4 | Datos | ✅ Completa |
| 5 | Pantallas | ✅ Completa |
| 6 | Integración | ✅ Completa |
| 7 | Contenido e i18n | ✅ Completa |
| 8 | Accesibilidad y métricas | ✅ Completa |
| 9 | Documentación | ✅ Completa |

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
- **Patrón State**: `AssistanceState` (Calm, SoftAsk, AskForHelp, Guiding, SharingCountdown, LocationShared, Acknowledged, Paused) + `AssistanceStateMachine` pura.
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

---

## Fase 6 — Integración

**Backend / IA**
- Tubería completa: `LocationSource` (GPS real o GPX) → `TripMonitor` (motor de IA + `TripSession` + máquina de estados) → UI (`StateFlow`) + efectos.
- `TripMonitor` (dominio): alarga el recorrido con cada punto, lo evalúa, publica rutina, guía y llegada, despacha `DetectionUpdated`, detecta "Ya estás en tu ruta" (≤ 25 m) y la llegada (≤ 30 m). Al terminar, la IA aprende del recorrido.
- `AssistanceTimers` (dominio): vibración repetida a los 15 s, 30 s sin respuesta → cuenta regresiva, tic de 1 s, "Entendido" que se cierra a los 3 s. Probado con tiempo virtual.
- `RecoveryGrace` (dominio): tras recuperar la ruta, lo anterior se perdona mientras siga cerca (≤ 40 m).
- Efectos de cada transición (patrón Observer): `AssistanceHapticPolicy` (qué vibración), `AssistanceMetricsRecorder` (alerta, tiempo de decisión, tiempo de recuperación, llegada), `AssistanceFeedbackLearner` ("Estoy bien" sube umbrales, "Sí, guíame" los baja), `LocationSharingEffect` (avisa al contacto y reintenta cada 20 s).
- `TripMonitorRunner` (app) corre todo en el alcance de la aplicación; `LocationTrackingService` (data) es un servicio en primer plano de tipo ubicación con una notificación tranquila, que mantiene vivo el GPS con la pantalla apagada.

**Demo con GPX** — `GpxDemoEndToEndTest` reproduce los cuatro GPX de `sample-routes/` por la tubería real (motor + monitor + máquina de estados):
1. Ruta de siempre → nunca sale de Calm y llega a la U.
2. Desvío voluntario → pregunta suave, nunca "¿Necesitas ayuda?".
3. Calle cerrada → pregunta → "Sí, guíame" → guía → **ruta recuperada** (Calm).
4. Hospital sin rutina → pregunta al dar vueltas y luego llega al consultorio.
En el reloj: compilar *debug*, Ajustes → Demostración (o "Demostración" en Inicio) y elegir el recorrido.

**Pruebas** — dominio 64, app 30 (más las de fases anteriores), todas en verde.

**Decisiones**
- *Gracia de recuperación*: sin ella, las vueltas que ya pasaron seguían pesando al volver a la ruta y LOST preguntaba otra vez por lo mismo (lo descubrió la prueba de punta a punta).
- *El servicio en primer plano solo se inicia con permiso de ubicación* (Android 14+ lo exige para el tipo `location`). En la demo sin permiso la tubería corre igual en el alcance de la app.
- `HapticNotifier` y `SettingsReader` pasan a `fun interface` para crear dobles simples en las pruebas.

**Pendientes**
- Verificación visual en un emulador de Wear OS: en este equipo solo hay una imagen de teléfono (Pixel 8) y la app exige hardware de reloj. Instalar en Android Studio una imagen "Wear OS 5" y ejecutar `./gradlew :app:installDebug`.

---

## Fase 7 — Contenido e internacionalización

**Diseño / contenido**
- Textos finales en español (base), inglés y portugués para `app` y `data` (≈ 200 textos + 8 `plurals`).
- **Voz fija y tono variable**: `ToneResolver` + `Tone` (relajado · atento · firme), cada tono con su texto principal y su vibración. En nivel alto la pregunta pasa a "Creo que necesitas ayuda. Te guío y aviso a tu contacto."
- **Mensaje de error de 3 partes** obligatorio por estructura (`UiMessage` + `LostErrorToUiMessageMapper`), sin códigos internos.
- Idiomas por app en Android 13+ (`generateLocaleConfig`), pseudolocales `en-XA`/`ar-XB` en *debug*, horas, distancias y días con el `Locale`.
- `docs/contenido/glosario-voz.md`: voz, tono, glosario sistema → usuario y tabla de errores.

**Pruebas** — `TranslationsTest`: las tres lenguas tienen exactamente las mismas claves y los mismos marcadores, y ninguna pantalla escribe texto literal en el código. App: 36 pruebas en verde.

**Decisiones**
- Los `plurals` de español y portugués incluyen la categoría `many` (las reglas CLDR recientes la usan para números grandes).
- "Casa → U" se tradujo como "Home → Uni" / "Casa → Universidade" porque "U" solo se entiende en Colombia.

**Pendientes**
- Revisar visualmente en `en-XA` en un emulador de Wear OS (no hay imagen de reloj en este equipo).
- Revisión de las traducciones por hablantes nativos.

---

## Fase 8 — Accesibilidad y métricas

**Accesibilidad (sección 9.5)**
- Contraste WCAG AA verificado por prueba (`ContrastRatioTest`): texto y estados ≥ 4.5:1 sobre negro OLED.
- Estado en 4 canales: color + icono + texto + vibración (`StatusPresentation`, `Tone`), comprobado por `AccessibilityRulesTest`.
- Áreas táctiles ≥ 48 dp: botones de 52 dp a todo el ancho, filas de lista ≥ 48 dp, "Toca para saber por qué" como botón y "Borrar lo aprendido" con alto mínimo.
- TalkBack: alertas como regiones vivas (la pregunta se anuncia sola), descripciones en mapa, indicador de sección y flecha de guía ("Flecha que apunta al noreste").
- Texto grande (+30 %) desde Ajustes y respeto de la escala de fuente del sistema (todo en `sp`).

**Métricas (sección 9.4)**
- `MetricsTracker` → `LocalMetricsTracker` (Room) con eventos de alerta, respuesta (con tiempo), ayuda iniciada, ruta recuperada (con tiempo), llegada, aviso compartido, rutina aprendida y recorrido completado.
- `InsightsCalculator` + pantalla **Tu semana**: eficacia, tiempo de decisión, tiempo de recuperación, falsos positivos y uso, con la meta de cada una.
- `docs/metricas/metricas.md` (qué se mide, dónde y protocolo de prueba) y `docs/metricas/sus.md` (cuestionario SUS, cálculo y plantilla de resultados).

**Pruebas** — app 39 pruebas en verde (incluye `AccessibilityRulesTest`).

**Pendientes**
- Revisión con Accessibility Scanner y TalkBack en un reloj o emulador de Wear OS (no disponible en este equipo).
- Aplicar el cuestionario SUS con usuarios reales y llenar la tabla de resultados.

---

## Fase 9 — Documentación

- `README.md`: qué es LOST, requisitos, cómo ejecutar, cómo reproducir la demo, cómo verificar, estructura y mapa de la documentación.
- `docs/arquitectura.md`: módulos, SOLID, patrones y estructuras de datos con la clase real de cada uno.
- `docs/investigacion/`: personas, journey map (enlazado con los estados del código), planteamiento del problema, guion de entrevistas.
- `docs/ia/`: arquitectura de información (diagrama Mermaid y los 4 sistemas) y motor de IA explicado (flujo, señales, fórmula y pesos).
- `docs/contenido/glosario-voz.md`, `docs/marca/personalidad.md`, `docs/metricas/` (métricas, protocolo y SUS).
- `docs/adr/`: 001 Clean Architecture · 002 DTW vs. distancia fija · 003 máquina de estados (con diagrama).
- Corrección encontrada al documentar: en un lugar nuevo la alerta ya no dice "Te saliste de tu ruta de siempre"; muestra la razón concreta.

---

## Mejoras del 25 de octubre

Trabajo en la rama **`mejoras-ui-mapa`** (sin merge a `main`). El estado anterior quedó marcado con el tag
**`v1-entrega-avances`**. Cada commit pasó `./gradlew ktlintCheck detekt test assembleDebug`.
Esta vez sí hubo **emulador de Wear OS** (Wear OS Large Round, 454 × 454): capturas antes/después en
`docs/capturas/antes/` y `docs/capturas/despues/`.

### Diseño: interfaz menos cargada (Paso 1)

Orden de los PDF: contenido → jerarquía → estructura → estilo. Auditoría completa, pantalla por pantalla,
en **`docs/diseno/simplificacion.md`** (qué había, qué se quitó, qué pasó a un segundo nivel y por qué).

- **Problemas reales encontrados en el emulador**: el permiso de ubicación cortaba el texto con "…", la segunda
  respuesta de las alertas ("Estoy bien", "Guíame", "Ahora no") quedaba fuera de la pantalla redonda, el
  titular de Monitoreo se cortaba y Tu semana tenía más de 12 líneas.
- **Una idea por pantalla**: Inicio en recorrido pasó de 8 a 4 elementos (chip de estado · hora de llegada ·
  destino · Terminar recorrido); Monitoreo es el mapa con un titular de 3–4 palabras; las métricas pasan a una
  pantalla **Métricas** (segundo nivel de Tu semana); sin entradas duplicadas (Tu semana y Demostración).
- **Revelación progresiva**: el chip de estado es la puerta al detalle (Inicio → Monitoreo → ¿Por qué te pregunto?).
- **Design system**: `WatchScreenTemplate` centra el contenido corto; `LostChip` puede ser tocable (flecha "›",
  48 dp); nueva plantilla `MapScreenTemplate`; la segunda respuesta de `DualActionButtons` va solo con texto
  (cabe en el círculo, 52 dp de área táctil); preguntas de hasta 3 líneas (en `en-XA` ya no se cortan);
  sin guiones de corte (partían "reco-rridos").
- Textos más cortos en es/en/pt (`TranslationsTest` en verde) y tabla antes/después en `docs/contenido/glosario-voz.md`.
- **Corona**: con Texto grande la alerta no cabía y "Estoy bien" quedaba debajo, pero las pantallas con
  `WatchScreenTemplate` no escuchaban la corona (solo las listas). Ahora se desplazan con la corona
  (`rotaryScrollable`) y muestran el indicador curvo; con grupos de foco jerárquicos solo la sección visible
  —o la alerta, si está— recibe la corona. Probado con `input rotaryencoder` en el emulador.

### Mapa real (Paso 2) — ver `docs/adr/004-mapa-openstreetmap.md`

- **OpenStreetMap sin llave de API y con renderizador propio** (no hizo falta osmdroid):
  - `domain`: `WebMercator`, `MapViewport`, `ViewportFitter` (encuadre automático), `LruCache` y los puertos
    `MapTileSource`, `DestinationSearchService`, `WalkingRouteService`, `CompassHeadingSource`. Kotlin puro, con pruebas.
  - `data`: `NetworkTileSource` (User-Agent propio) + `CachedMapTileSource` (memoria LRU → disco una semana → red),
    `NominatimDestinationSearch` (solo al enviar, sin autocompletar), `OsrmWalkingRouteService` (a pie),
    `AndroidCompassHeadingSource`. Sin dependencias nuevas (`HttpURLConnection` + `org.json`).
  - Design system: `RealMapView` (teselas oscurecidas con `ColorFilter`, ruta de siempre en gris con borde oscuro,
    hoy en el color del estado, punto del usuario). **No interactivo**: no choca con el pager ni con deslizar-para-volver.
- **Monitoreo y detalle de rutina** muestran el mapa real; sin internet o con el mapa apagado vuelven al mini-mapa
  vectorial y dicen qué hace LOST (mensaje de tres partes `MapUnavailable`).
- **Atribución** "© OpenStreetMap contributors" bajo el estado en Monitoreo, bajo el mapa en el detalle y en Ajustes → **Acerca de**.
- **Privacidad**: interruptor **"Mapa real (usa internet)"** en Ajustes; textos de privacidad y bienvenida corregidos.
  Solo salen las zonas del mapa, el texto buscado y (al empezar Lugar nuevo, con aviso en pantalla) inicio y destino.
  Nunca los recorridos aprendidos ni el contacto.
- **Destinos reales por voz**: lo dictado se busca primero en los destinos guardados; si no está, en Nominatim
  ("Llévame a la biblioteca" → "biblioteca") y el lugar elegido se guarda con sus coordenadas.
- **Demo sobre calles reales**: `tools/generate_osm_routes.py` regenera los 4 GPX con OSRM a pie alrededor de una
  sola constante, `CENTER` (Universidad Nacional de Colombia, Bogotá). El hospital es la clínica más cercana según
  Nominatim (Clínica Palermo). La duración de la rutina y los destinos de ejemplo se calculan desde los GPX.
  Los **4 escenarios de `GpxDemoEndToEndTest`** y las **5 pruebas de la sección 8.5** siguen en verde.
- **Flecha con brújula**: gira según hacia dónde mira el usuario; sin sensor, rumbo absoluto. La brújula solo se
  escucha mientras LOST guía.

### Optimización (Paso 3): números antes → después

| Qué | Antes | Después | Cómo se midió |
|---|---|---|---|
| APK release | 57,4 MB | **3,6 MB** (−94 %) | `assembleRelease` con R8 (`optimization.enable = true`) |
| Evaluación del motor (2 151 puntos a 1 Hz) | 3,61 ms | **2,46 ms** | `EvaluationPerformanceTest` (JVM de escritorio) |
| Seguir el recorrido completo evaluando cada 5 s | 431 ms | **384 ms** | ídem |
| Evaluaciones del motor con GPS a 1 Hz y en calma | 1 por segundo | **1 cada 4 s** (−75 %) | `EvaluationThrottle` (prueba `BatteryPoliciesTest`) |
| Puntos GPS en la ruta de siempre (17 min, en calma) | 208 (cada 5 s) | **69** (cada 15 s, −67 %) | `LocationCadencePolicy`; cada 5 s si algo no cuadra, cada 2 s guiando |
| Inicio en reposo, 10 s (emulador) | p50 27 ms · p90 32 ms · p99 250 ms · tirones 4,3 % | **p50 22 ms · p90 27 ms · p99 46 ms · tirones 1,9 %** | `adb shell dumpsys gfxinfo` |

- **Motor**: remuestreo incremental (`IncrementalTrajectoryResampler`, idéntico al completo), tope de 7 200 puntos en
  memoria (`TripPointsCap`), monitor en `Dispatchers.Default` y proyección de rutas de Monitoreo fuera del hilo de la UI.
- **Batería**: GPS adaptativo, evaluación con límite de frecuencia, brújula y mapa solo mientras se ven
  (`WhileSubscribed`); con la pantalla apagada solo sigue el servicio de ubicación.
- **Compose**: la respiración del anillo y el pulso del mini-mapa se leen en la fase de dibujo (sin recomponer);
  el pulso solo existe si hay un punto de separación; claves estables en las listas; el mapa no cancela cargas con cada
  punto GPS (`conflate`).
- **Room**: las consultas frecuentes ya tenían índice (`metric_events.timestamp`, `trips.started_at`, claves primarias).
- **Release**: R8 + recursos sin uso eliminados; Baseline Profile escrito a mano (`app/src/main/baseline-prof.txt`).
  Probado: la versión release (firmada con la llave de depuración solo para instalarla) arranca, recorre las
  secciones, usa el GPS real y muestra el mapa real.

### Pruebas

**211 pruebas en verde** (antes 167): designsystem 8 · domain 91 · ai 38 · data 29 · app 45.
Nuevas: Web Mercator, viewport, encuadre, LRU, carga del mapa, búsqueda de lugares, parsers de Nominatim y OSRM,
caché en memoria y disco, búsqueda de destinos por voz, brújula, remuestreo incremental, rendimiento del motor y
políticas de batería.

### Decisiones

- **Renderizador propio en vez de Google Maps u osmdroid** (ADR 004): sin llave, sin gestos que choquen y estilo desde tokens.
- **Mapa real encendido por defecto**, con el interruptor a la vista en Ajustes y la verdad en Privacidad.
- **Interruptor único** para todo lo que usa internet (mapa, búsqueda y ruta a pie): apagado, nada sale del reloj.
- **La guía siempre tiene salida**: "Ya sé dónde estoy" termina la guía aunque no haya vuelto a la ruta (antes
  la pantalla no se podía cerrar). Cuenta como "Estoy bien": LOST aprende y no vuelve a preguntar de inmediato.
- **Segunda respuesta solo con texto** en las alertas (el mockup la dibuja como píldora gris; en el reloj redondo chocaba con la curva).
- **Atribución bajo el chip** en Monitoreo: abajo el círculo es demasiado estrecho para que quepa en una línea.
- **GPX deterministas**: el hospital se elige como el más cercano a `CENTER` entre los resultados de Nominatim.
- **Manzana de las vueltas que nunca toca la ruta**: si la tocaba, LOST "recuperaba la ruta" a mitad de las vueltas.
- **Push con `gh`**: el administrador de credenciales de Git pedía iniciar sesión de forma interactiva; se usó
  `gh auth git-credential` solo para el push, sin cambiar la configuración.

### Pendientes

| Pendiente | Por qué | Cómo cerrarlo |
|---|---|---|
| Probar la búsqueda por voz en el reloj | El emulador no tiene micrófono configurado; está cubierta por pruebas unitarias. | Dictar "Llévame a la biblioteca central" en un reloj y elegir el resultado. |
| Brújula en un reloj real | El emulador no simula bien el sensor de rotación. | Probar la guía caminando con un reloj físico. |
| Baseline Profile generado | El actual es escrito a mano (reglas con comodines). | Módulo `baselineprofile` con Macrobenchmark en un reloj. |
| Servidor de teselas propio | tile.openstreetmap.org es comunitario; sirve para la demo, no para producción. | Proveedor propio o de pago con el mismo `MapTileSource`. |
| Texto grande (+30 %): revisar todas las pantallas | Se revisaron Inicio, Monitoreo y la alerta (caben o se desplazan con la corona); faltan las demás. | Activar Texto grande y recorrer el resto con la corona. |
| TalkBack en el emulador | No se recorrió la app con TalkBack. | Activar TalkBack y comprobar el orden de lectura del chip tocable y del mapa. |

### Qué revisar tú

1. Las capturas antes/después (`docs/diseno/simplificacion.md`) y si el chip de estado tocable se entiende como "toca para saber más".
2. Si te parece bien la demo en la **Universidad Nacional** y la **Clínica Palermo** (se cambia con `CENTER` en `tools/generate_osm_routes.py`).
3. El texto de privacidad del mapa (Ajustes → Privacidad) y el aviso de Lugar nuevo.
4. Si prefieres el mapa real **apagado** por defecto (hoy está encendido).
5. Si quieres hacer el merge de `mejoras-ui-mapa` a `main` (no lo hice).

---

## Pulido del 27 de octubre

Trabajo en la rama **`mejoras-ui-mapa`** (sin merge a `main`), desde el tag **`v2-mapa-ui`**. Empezó el 27 de
octubre y terminó el 31. Cada commit pasó `./gradlew ktlintCheck detekt test assembleDebug`; cada corrección trae
una prueba que fallaba antes y pasa después. Emulador: Wear OS Large Round (454 × 454, API 37).

### 1. Fuente Manrope — [`docs/diseno/tipografia-manrope.md`](diseno/tipografia-manrope.md)

- Manrope (SIL OFL 1.1, licencia en `docs/licencias/`) en `core/designsystem/src/main/res/font/`: 4 pesos fijos y
  solo glifos latinos. `LostTypography.brandFamily` la usa en toda la escala. **APK release: 3,60 → 3,70 MB (+97 KB).**
- Textos cortados que aparecieron y se corrigieron: Contactos en pt y, con Texto grande, textos de apoyo en los
  tres idiomas (`LostTextRole.maxLinesAt`: las líneas crecen con la letra). `CaptionFitTest` mide los textos con la
  propia Manrope.

### 2. Fidelidad con los mockups — [`docs/diseno/fidelidad-mockups.md`](diseno/fidelidad-mockups.md)

Comparación pantalla por pantalla con `docs/design/LOST v2.dc.html`; capturas en `docs/capturas/pulido/`.
Cada diferencia quedó como **(a) corregida**, **(b) decisión consciente** o **(c) pendiente**.

- **(a)** Con Manrope "¿Necesitas ayuda?" pasaba a dos líneas y **"Estoy bien" quedaba fuera de la pantalla**: la
  pregunta baja a 17 sp. Cuenta regresiva y "Laura ya sabe dónde estás" como en F.4/F.4b (icono coral, sin tarjeta)
  y con los botones dentro del círculo. "Entendido" con el check en un círculo relleno.
- **(b)** Lo decidido el 25 de octubre (Inicio en 4 elementos, segunda respuesta solo con texto, mapa real en
  Monitoreo…). La inicial del contacto se probó y se quitó: empujaba "Quitar permiso" bajo el borde.
- **(c)** Tu semana con un segmento por día, "Lugares donde más te desorientas", complicación y errores en el emulador.

### 3. Accesibilidad — [`docs/diseno/accesibilidad-revision.md`](diseno/accesibilidad-revision.md)

- **Texto grande:** al activarlo la app volvía a Inicio (`LargeTextScale` cambiaba el árbol de composición):
  corregido. Sin cortes en es y pt.
- **en-XA:** el relleno real es ~50 % (medido con `aapt2`); títulos y textos de apoyo admiten 3 líneas. Sin "…".
- **Auditoría con uiautomator** (≥ 48 dp, etiquetas, orden de lectura): todo en regla salvo un hallazgo — con una
  alerta encima, las filas de la pantalla de abajo seguían para TalkBack como tocables sin etiqueta. `AlertLayers`
  las oculta mientras hay alerta.
- **TalkBack:** se activó en el emulador; habla al aparecer cada alerta y recorre la pantalla en orden. No se pudo
  verificar palabra por palabra (sin audio en el emulador).

### 4. IA ante el ruido del GPS — [`docs/ia/robustez-ruido-gps.md`](ia/robustez-ruido-gps.md)

Los 4 escenarios de la demo con ruido realista (σ 5 m, σ 10 m, rebotes de 40 m, 20 % de puntos perdidos):

| | Ruta de siempre | Desvío voluntario | Calle cerrada | Hospital |
|---|---|---|---|---|
| Antes (con cualquier ruido realista) | **pedía ayuda ✗** | **pedía ayuda ✗** | ✓ | ✓ |
| Después | solo observa ✓ | pregunta suave ✓ | ✓ | ✓ |

- Causa: el remuestreo sumaba el temblor como distancia (+54 % de puntos), eso desbordaba la banda del DTW y el
  temblor parecía giros bruscos. Ajustar umbrales no alcanzaba.
- Cambio: remuestreo por avance en línea recta, promedio de los últimos 15 s, descarte de saltos aislados (> 5 m/s),
  giros medidos entre cuerdas de 40 m y `erraticBaselineRatio` 0,15 → 0,25. **Los pesos de la regresión no cambiaron.**
- Siguen en verde las 5 pruebas de la sección 8.5 y los 4 escenarios GPX. Fuera de lo realista (σ 20 m con saltos
  de 60 m) la ruta de siempre todavía pide ayuda: queda pendiente.

### 5. SUS — [`docs/metricas/guia-aplicacion-sus.md`](metricas/guia-aplicacion-sus.md)

Guía de la sesión, plantilla `docs/metricas/plantilla-sus.csv` y `tools/calcular_sus.py` (puntaje, nota de Sauro y
Lewis, promedio, mediana e intervalo de confianza del 95 % con t de Student; 17 pruebas con `unittest`).
`sus.md` deja los resultados **pendientes**: todavía no hay sesiones con usuarios y no se inventaron datos.

### 6. Pruebas instrumentadas

| Módulo | Prueba | Qué cubre |
|---|---|---|
| `data` | `LostDatabaseTest` (6) | Room contra el SQLite real: rutinas, recorridos de la semana, métricas, destinos. |
| `app` | `AlertsFitOnRoundScreenTest` (4) | Las respuestas de las alertas se ven sin desplazar y dentro del círculo. |
| `app` | `AlertLayersTest` (2) | Con alerta, la pantalla de abajo no se toca ni la lee TalkBack. |
| `app` | `LargeTextScaleTest` (1) | Cambiar Texto grande conserva el estado. |
| `app` | `ScreensTest` (4) | Inicio, Ajustes y Monitoreo sin internet (mini-mapa y qué hacer). |

```bash
./gradlew :data:connectedDebugAndroidTest :app:connectedDebugAndroidTest
```

(`:app:connectedDebugAndroidTest` desinstala la app al final; para conservar los datos de la demo se puede instalar
con `installDebug installDebugAndroidTest` y correr `adb shell am instrument`).

### 7. Baseline Profile

Módulo **`baselineprofile`** (Macrobenchmark 1.5): `BaselineProfileGenerator` abre LOST y pasa por las cinco
secciones; el perfil generado (`app/src/release/generated/baselineProfiles/`) tiene **8 452 reglas** (el escrito a
mano tenía 8 con comodines; se conserva y AGP los combina). `StartupBenchmark` compara el arranque en frío
(5 iteraciones, emulador):

| | Sin perfil | Con Baseline Profile |
|---|---|---|
| Tiempo al primer cuadro, mínimo | 1 083 ms | 1 078 ms |
| Tiempo al primer cuadro, mediana | 1 156 ms | 1 258 ms (una iteración de 2,7 s la sube) |
| CPU por cuadro, P90 / P99 | 71 / 401 ms | 68 / 229 ms |

En el emulador el arranque **no mejora de forma clara**; los cuadros lentos (P99) sí bajan. El emulador no sirve
para medir arranque con precisión: hay que repetirlo en un reloj.

```bash
./gradlew :app:generateBaselineProfile
./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.lost.baselineprofile.StartupBenchmark
```

(Generar el perfil instala una variante release con el mismo paquete: borra los datos de la app de depuración.)

### Capturas finales — `docs/capturas/final/`

El recorrido completo después de todo el pulido: onboarding, Inicio, "¿Necesitas ayuda?", cuenta regresiva,
aviso enviado, guía, "Entendido" y las secciones. Hoja resumen en `00-resumen.png`.

### Pruebas

**244 pruebas JVM en verde** (antes 211): designsystem 11 · domain 92 · ai 43 · data 29 · app 69. Además
**17 instrumentadas** (data 6 · app 11) y **17 de Python** (`tools/`).

### Decisiones

- **Corregir el motor y no solo los umbrales** ante el ruido del GPS: con 7 combinaciones de umbrales los 4
  escenarios seguían fallando; la causa estaba en cómo se mide.
- **Pregunta de las alertas a 17 sp** (antes 18): con Manrope es lo que permite que la segunda respuesta se vea.
- **Títulos y textos de apoyo con 3 líneas** como máximo: en español siguen usando 1–2.
- **Sin inicial en Contactos:** se prefiere que "Quitar permiso" se vea sin desplazar.
- **No se inventaron resultados de SUS.**

### Pendientes

| Pendiente | Cómo cerrarlo |
|---|---|
| Aplicar el SUS con 5 personas | Seguir `docs/metricas/guia-aplicacion-sus.md` y pegar el informe del script en `sus.md`. |
| ~~GPS en cañón urbano (σ 20 m)~~ (hecho el 1 de noviembre con `Location.accuracy`) | — |
| Escuchar TalkBack palabra por palabra y Accessibility Scanner | En un reloj físico. |
| ~~Tu semana con un segmento por día~~ (hecho el 1 de noviembre); "Lugares donde más te desorientas" | Ver la sección (c) de `fidelidad-mockups.md`. |
| Recorridos reales con el reloj | Repetir la tabla de `robustez-ruido-gps.md` con datos grabados. |
| Arranque con Baseline Profile en un reloj | Repetir `StartupBenchmark` en hardware real. |

### Qué revisar tú

1. **Decisión tuya — pesos de la IA:** no cambié `ScoringWeights`; sí cambié el remuestreo, la medición de giros y
   `erraticBaselineRatio` (0,15 → 0,25), lo que permite el paso 4. Revisa `docs/ia/robustez-ruido-gps.md`.
2. La pregunta de las alertas a 17 sp y las capturas de `docs/capturas/pulido/` frente a los mockups.
3. Si quieres aplicar ya el SUS con la guía.
4. ~~Decisión tuya — pausa después de "Ya sé dónde estoy"~~ **Hecho (31 de octubre):** tras "Estoy bien" o "Ya sé
   dónde estoy", LOST no vuelve a preguntar durante 2 minutos (estado `Paused`, ver ADR 003). Sigue observando y
   llegar al destino termina la pausa.
5. Si quieres hacer el merge de `mejoras-ui-mapa` a `main` (no lo hice).

---

## Tu semana por día (1 de noviembre)

Cierra la diferencia con el mockup 03.1 que quedaba pendiente en `docs/diseno/fidelidad-mockups.md`.

- **Un segmento por día hábil, en ámbar el día con desvío**, y debajo la fila **L M M J V** con el mismo color
  (`SegmentedRingLegend`, organismo nuevo del design system). Un día cuenta como "con desvío" si LOST mostró
  al menos una alerta ese día.
- **El desvío no se dice solo con color:** el resumen pasa de "6 recorridos · 1 rutina" a "6 recorridos · sin desvíos"
  o "· 1 día con desvío" (plurales en es/en/pt), y TalkBack lee "lunes, sin desvíos. martes, con desvío. …".
  Las rutinas aprendidas se siguen viendo en Mis recorridos.
- Las letras salen de `strings.xml` y no de `DayOfWeek.getDisplayName`: así miércoles es "M", como en el mockup,
  y no "X".
- `GetWeekTripDaysUseCase` pasa a **`GetWeekActivityUseCase`**, que devuelve los días con recorrido y los días con
  alerta (`WeekActivity`).
- Verificado en el emulador de Wear OS con datos de prueba (lunes y miércoles sin desvío, martes con alerta):
  captura `docs/capturas/pulido/15b-tu-semana-con-desvio.png`. Luego se restauró la base de la demo.
- **5 pruebas nuevas** (`GetWeekActivityUseCaseTest` 3, `InsightsUiStateMapperTest` +2); ktlint, detekt, todas
  las pruebas JVM y `assembleDebug` en verde.

## Precisión del GPS (1 de noviembre)

Cierra el pendiente del cañón urbano. Detalle, tablas y costos en
[`docs/ia/robustez-ruido-gps.md`](ia/robustez-ruido-gps.md#precisión-del-gps-locationaccuracy--1-de-noviembre).

- `Location.accuracy` viaja en `GeoPoint.accuracyMeters`. El motor **ignora los puntos peores que 50 m**,
  **pesa el promedio por 1 / precisión²** y **alarga la ventana del promedio** (hasta 3 veces) cuando la precisión
  es peor que 10 m. Sin precisión (GPX de la demo) todo sigue igual.
- Cañón urbano (σ 20 m, saltos de 60 m), 10 semillas: ruta de siempre 9/10 → **10/10**, desvío voluntario
  0/10 → **9/10**, desorientación y hospital siguen en 10/10.
- **Costo:** con esa señal las vueltas del hospital se notan unos 90 s más tarde.
- **Hallazgo:** con σ 10 m el desvío voluntario pide ayuda en 2–3 de 10 semillas (también antes; la prueba solo
  usaba la semilla 27). Queda en pendientes.
- 26 pruebas nuevas (`GpsAccuracyTest` 7, `GpsNoiseRobustnessTest` 16 → 35); ktlint, detekt, todas las pruebas JVM
  y `assembleDebug` en verde. Commits separados: el filtro y el peso (`0d3e2a3`) y la ventana adaptativa (`7cf327b`),
  por si se prefiere no pagar el retraso del hospital.

## Desvío voluntario estable con varias semillas (1 de noviembre)

- **Diagnóstico:** con σ 10 m el desvío voluntario pedía ayuda en 3 de 20 semillas. La señal de giros bruscos
  contaba una esquina dos veces (la cuerda de 40 m la reparte en dos puntos) y 3 giros en 160 m ya bastaban.
- **Corrección** (`726596e`): una esquina repartida cuenta una vez y la confusión empieza con más de 3 giros por
  ventana. Con 20 semillas todo lo realista pasa 20/20 y la desorientación y el hospital se siguen detectando 20/20.
- **"Voy a otro lado"** (`b39482c`): el resto del recorrido ya no se compara con la ruta de siempre; Inicio dice
  "Caminas seguro a otro lugar".
- Las falsas alarmas se prueban con 10 semillas por variante. Detalle en
  [`docs/ia/robustez-ruido-gps.md`](ia/robustez-ruido-gps.md#varias-semillas-el-desvío-voluntario-con-σ-10-m--1-de-noviembre).

## Jornada del 1 de noviembre (funcionalidades)

Además del diagnóstico del desvío voluntario (sección anterior), quedaron cerrados todos los pendientes de código
de la tabla (c) de `docs/diseno/fidelidad-mockups.md`. Todo se probó en el emulador de Wear OS.

| Qué | Commits | Pruebas |
|---|---|---|
| **Aviso de ubicación perdida.** Tras un minuto sin un punto confiable (≤ 50 m de precisión) Inicio dice "No encontramos tu ubicación · Camina hacia una salida o ventana", con región viva para TalkBack y una vibración suave; se quita con el primer punto bueno y no aparece tras llegar. Captura `19`. | `9d0b41c` | `LocationSignalWatchdogTest` (5), `HomeUiStateMapperTest` |
| **Complicación y vista rápida como el mockup 05.** Complicación `RANGED_VALUE` (anillo con el avance + hora de llegada) y `SHORT_TEXT`; vista rápida "LOST · ahora / Vas a Casa → U / Todo en orden · llegada 7:15" en el color del estado; ambas abren Inicio y se actualizan en vivo solo cuando cambia algo visible. | `5936628`, `a24c5f5` | `GlanceSummaryMapperTest` (5), `GlanceRefresherTest` |
| **Notificación 05.3.** Con la app oculta, "LOST · ¿Todo bien en tu ruta?" con Guíame / Estoy bien (o "¿Vas a otro lado hoy?"). Estoy bien y Voy a otro lado responden sin abrir la app; Guíame la abre. Captura `20`. | `a502d80` | `AssistancePromptTest` (4) |
| **Icono propio.** El lanzador, la notificación y el splash usaban el robot de la plantilla; ahora es el anillo aqua del mockup 05.2 sobre negro, con capa monocroma. | `b2efd4d` | Visto en el lanzador del emulador |
| **Donde más te desorientas (03.2).** Se guarda dónde y por qué preguntó LOST (Room v2, migración automática); hasta 3 lugares agrupados por cuadra, nombrados con destinos o rutinas sin internet ([ADR 005](adr/005-lugares-sin-geocodificacion.md)). "Borrar lo aprendido" también los borra. Captura `21`. | `140d538` | `DisorientationPlacesTest` (4), `LostDatabaseTest` (+1), `LostDatabaseMigrationTest` |
| **Giros sin mirar la pantalla.** Durante la guía, si la persona camina 60° o más fuera del rumbo al punto de regreso, vibra el patrón de giro (como mucho cada 30 s). Antes ese patrón solo sonaba al entrar a la guía. | `5d827a1` | `GuidanceTurnCueTest` (4) |
| **Elegir el contacto entre los del reloj.** En Wear OS el intent de elegir contacto lo atiende un stub vacío; ahora Contactos y la bienvenida ("Elegir otra persona", mockup 01.3) abren una lista propia de los contactos del reloj con teléfono, tras explicar el permiso ("Solo guardo a quien elijas, no tu lista."). Captura `25`, `26`. | `282eba8`, `87b8b8c` | `DeviceContactsUseCasesTest` (3), probado en el emulador con contactos de prueba |
| **Errores (10) y Texto grande.** Los 9 errores se dibujan en el reloj con letra normal y con Texto grande; se revisaron con Texto grande las 11 pantallas que faltaban (hoja `a11y-texto-grande-resto.png`): sin cortes, las largas se desplazan. | `a0d2c7a` | `ErrorScreensTest` (2, instrumentada) |

**Robustez:** `adb shell monkey -p com.lost.watch -s 2711 --throttle 150 3000` en el emulador: ningún cierre ni ANR de
LOST en 1 927 eventos aleatorios (Monkey se detuvo por un ANR de la interfaz del sistema de Wear, no de la app).

**Verificación:** `./gradlew ktlintCheck detekt test assembleDebug` en verde (489 pruebas JVM al cierre del día) y 21 pruebas
instrumentadas en el emulador `Wear_OS_Large_Round` (data 8 · app 13).

### Revisión de código, Lint y pulido de la tarde

Un revisor independiente leyó todos los cambios del día. No encontró errores críticos; se corrigió lo que tenía
sustento:

| Hallazgo | Arreglo | Commit |
|---|---|---|
| `TripSession.dispatch` leía el estado y escribía el siguiente en dos pasos; con el receptor de la notificación ya son 4 hilos despachando y una respuesta podía perderse | `@Synchronized` | `054d5c8` |
| El canal de la notificación sonaba (solo se apagó la vibración) | `setSound(null, null)`, canal nuevo `lost_prompts` | `054d5c8` |
| La cuenta regresiva no se podía cancelar con la app oculta, y la notificación silenciosa no se anunciaba | Notificación "Voy a avisar a tu contacto." con Cancelar, un id por pregunta y un toque de 40 ms (canal `lost_prompts_alert`) | `5130a90` |
| Al volver a LOST desde recientes se repetía un "Guíame" viejo | Se ignora el intent con `FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY` | `054d5c8` |
| La complicación no se redibujaba al reiniciar el proceso | `GlanceRefresher` pide redibujar al arrancar | `ffedefe` |
| Un caso de uso con 5 parámetros y umbrales sueltos | `DisorientationPlaceSummarizer` e `InsightsThresholds` | `38e4235` |

- **Android Lint** (`./gradlew :app:lintDebug`) pasa sin errores: se manejó el permiso de notificaciones como pide
  Lint y se justificaron dos plurales en portugués que ya existían (`f08ec5f`).
- **Build de release con R8** (`./gradlew :app:assembleRelease`): compila, 3,8 MB.
- **Pausa visible** (`13fe944`, ajustada en `5ddfbb7`): tras "Estoy bien", Inicio seguía en coral "Creo que
  necesitas ayuda" aunque LOST no iba a preguntar en 2 minutos. Ahora se ve en calma y el chip dice "Pausa de 2
  minutos", con "Llegas a Casa → U" bajo la hora (captura `23`). El primer texto ("No te pregunto por 2 minutos. Sigo atento.") ocupaba dos
  líneas y empujaba "Terminar recorrido" bajo los puntos del paginador; se acortó y se verificó en el emulador.
- **Vista previa de la Tile** (`a6621be`): la imagen del selector de Tiles era la del diseño viejo (captura `22`).
- **Glosario de voz** (`a96f131`): los textos nuevos, en la tabla sistema → usuario.

### Crítica de diseño independiente

Un agente revisó las capturas nuevas frente a los mockups y las reglas del curso. Se aplicó lo que tenía sustento:

| Hallazgo | Arreglo | Commit |
|---|---|---|
| En la pausa, el ámbar contradecía "Estoy bien" y la hora quedaba sin etiqueta | Calma y chip "Pausa de 2 minutos"; "Llegas a Casa → U" vuelve bajo la hora | `5ddfbb7` |
| La hora del sistema tapaba parte del primer y último segmento de Tu semana | Hueco de 40° arriba en el anillo por segmentos | `d6f1a82` |
| En en-XA el botón "Dónde te desorientas" terminaba en "…" | Botones de solo texto con hasta 3 líneas | `d6f1a82` |
| "desvío" quedaba sola en otra línea | Espacios que no se cortan en "1 día con desvío" | `8267ce0` |
| "Vas a Casa → U" en la vista rápida se leía como si el destino fuera Casa; la llegada quedaba partida | "En tu ruta Casa → U" más grande y "Llegada 7:15" en su línea | `8267ce0` |
| La notificación repetía "LOST" (app y título) | El título es la pregunta | `23e6fb2` |

Una segunda revisión de código, sobre todo lo posterior a la primera, no encontró errores críticos; se corrigió que
Monitoreo mostrara "Ya no es tu ruta" durante la pausa (ahora "En pausa, sigo atento.") y que el sembrador de la
demo fallara con un GPX vacío.

No se aplicó centrar en vertical la lista de Lugares: todas las listas de la app se alinean arriba
(`ListTemplate`). Las capturas 15, 20, 22 y 23 se repitieron en el emulador después de los cambios.

### Decisiones

- **Sin geocodificación inversa** para los lugares (ADR 005): la ubicación de los momentos difíciles no sale del reloj.
- ~~La notificación no vibra~~ **Corregido**: sin sonido ni vibración, Wear OS deja la notificación en la sección
  silenciosa y no la anuncia (verificado en el emulador). Ahora vibra un solo toque de 40 ms y el patrón de cada
  nivel lo sigue dando la escalada (captura `24`).
- **"Guíame" abre la app**: la guía es una flecha que hay que ver, y Android no permite abrir una pantalla desde un
  receptor lanzado por una notificación.
- **El refresco de la complicación y la vista rápida** se pide solo cuando cambia el estado, el destino, la llegada o
  un décimo del avance: pedirlo con cada punto GPS gastaría batería sin cambiar nada en pantalla.

### Qué revisar tú (1 de noviembre)

1. **Umbral de giros de la IA** (`erraticBaselineRatio` 0,25 → 0,375, `726596e`): hasta 3 giros bruscos en 160 m ya no
   cuentan como confusión. Con 20 semillas la desorientación y el hospital se siguen detectando 20/20, pero es un
   cambio del motor que conviene que apruebes.
2. **Ventana larga con GPS impreciso** (`7cf327b`, commit aparte): evita falsas alarmas en el cañón urbano a cambio
   de notar las vueltas del hospital unos 90 s más tarde con esa señal. Si prefieres no pagar ese retraso, se revierte
   solo ese commit.
3. **Lugares sin dirección** (ADR 005): "Cerca de Biblioteca Central" en vez de "Avenida 19 con calle 45", para no
   enviar a nadie dónde se desorientó la persona.
4. **La notificación vibra un toque de 40 ms** además del patrón de la escalada: sin eso Wear OS no la anuncia.
5. **"Pausa de 2 minutos"** en el chip de Inicio tras "Estoy bien", en calma (aqua). Una primera versión la dejaba
   en ámbar; la crítica de diseño mostró que contradecía la respuesta de la persona.
6. **Sin subir nada**: la rama `mejoras-ui-mapa` tiene todos los commits del día (y los anteriores) sin merge a
   `main` ni push.

---

## Pendientes generales

| Pendiente | Por qué quedó pendiente | Cómo cerrarlo |
|---|---|---|
| ~~Ver las pantallas en un emulador de Wear OS~~ | Hecho el 25 de octubre; TalkBack revisado en el emulador el 27. | Escucharlo palabra por palabra en un reloj. |
| Pruebas con usuarios y SUS | Requiere personas reales. La guía, la plantilla y el script ya están (27 de octubre). | Seguir `docs/metricas/guia-aplicacion-sus.md`. |
| Calibrar los pesos de la IA con recorridos reales | Los pesos se ajustaron con recorridos sintéticos y los GPX de la demo. | Grabar recorridos reales y ajustar `DetectionConfig` / `ScoringWeights`. |
| ~~Fuente Manrope~~ | Hecho el 27 de octubre (`docs/diseno/tipografia-manrope.md`). | — |
| ~~Flecha con brújula~~ | Hecho el 25 de octubre (`AndroidCompassHeadingSource`). | Probarla en un reloj físico. |
| ~~Selector de contacto real~~ | Hecho el 1 de noviembre (`282eba8`): lista propia de los contactos del reloj. | Probarlo con contactos reales sincronizados en un reloj físico. |
| Complicación sobre una esfera real | La esfera del emulador es declarativa y no expone huecos por comando. | Ponerla en una esfera con huecos en un reloj físico. |
| ~~Pruebas instrumentadas (Room, Compose UI)~~ | Hecho el 27 de octubre: 17 pruebas en el emulador. | — |
