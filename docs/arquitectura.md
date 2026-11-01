# Arquitectura, SOLID, patrones y estructuras de datos

Mapa para la sustentación: cada concepto del plan apunta a la clase real que lo implementa.

## Módulos (ver `adr/001-clean-architecture.md`)

| Módulo | Tipo | Responsabilidad |
|---|---|---|
| `domain` | Kotlin puro | Modelos, puertos, casos de uso, máquina de estados, sesión y monitor de recorrido |
| `ai` | Kotlin puro | Motor de detección y aprendizaje |
| `data` | Android | Room, DataStore, GPS, háptica, SMS, servicio en primer plano, demo GPX |
| `core/designsystem` | Android | Tokens (3 niveles), tema y componentes (Atomic Design) |
| `app` | Android | Pantallas MVVM, navegación, DI raíz e integración |

## SOLID

| Principio | Dónde se ve |
|---|---|
| **S** — Responsabilidad única | El motor está partido en piezas de una sola tarea: `TrajectoryResampler`, `DtwSequenceComparator`, `RoutineLearner`, cada `DeviationSignal`, `CompositeAnomalyScorer`, `AssistanceLevelClassifier`, `DeviationExplanationBuilder`. Los efectos de la asistencia también: `AssistanceHapticPolicy`, `AssistanceMetricsRecorder`, `AssistanceFeedbackLearner`, `LocationSharingEffect`, `AlertPlaceLogger`. La limpieza del GPS se reparte en `GpsSmoothing` (reglas), `GpsWindowAverage` (promedio ponderado por precisión) y `ResampleAccumulator` (remuestreo); lo que corre junto al monitor, en `LocationSignalWatchdog`, `AssistancePromptEffect` y `GlanceRefresher`, agrupados por `TripBackgroundWork`. |
| **O** — Abierto/cerrado | Una señal nueva = una clase que implemente `DeviationSignal` + registrarla en `LostEngineAssembly.signals()`. `CompositeAnomalyScorer` no cambia (prueba `una senal nueva se suma sin modificar el puntaje combinado`). |
| **L** — Sustitución de Liskov | `RoutineDetectionMode` / `NewPlaceDetectionMode` (contrato `DetectionMode`); `DtwSequenceComparator` / `FrechetSequenceComparator` (contrato `SequenceComparator`, misma prueba para ambos). |
| **I** — Segregación de interfaces | Puertos pequeños en `domain/port/`: `LocationSource`, `HapticNotifier`, `ContactNotifier`, `RoutineReader`, `RoutineWriter`, `TripRecorder`, `TripReader`, `MetricsTracker`, `MetricsReader`, `SettingsReader`, `SettingsWriter`, `ContactReader`, `ContactWriter`, `DestinationReader`, `DestinationWriter`, `DeviceStatusReader`, y para el mapa `MapTileSource`, `DestinationSearchService`, `WalkingRouteService`, `CompassHeadingSource`; para los lugares de 03.2 `AlertPlaceRecorder` (escribe) y `AlertPlaceReader` (lee) por separado. En `app`, `PromptNotifications` y `GlanceSurfaces` son interfaces de un método para probar los efectos sin Android. |
| **D** — Inversión de dependencias | El dominio define los puertos; `data` y `ai` los implementan (`FusedLocationSource`, `AndroidHapticNotifier`, `RoomRoutineRepository`, `LostDetectionEngine`…). Hilt los conecta en `DataBindingsModule` y `AiModule`. |

## Patrones de diseño

| Patrón | Clases |
|---|---|
| Strategy | `SequenceComparator` (DTW/Fréchet), `DetectionMode` (rutina/lugar nuevo), `DeviationSignal` |
| Composite | `CompositeAnomalyScorer` |
| State | `AssistanceState` + `AssistanceStateMachine` |
| Chain of Responsibility | `AssistanceResponsePolicy`: `VibrateAndAskLink` → `AlternativeRouteLink` → `ShareLocationLink` |
| Observer | `TripSession.snapshot` (`StateFlow`) y `TripSession.transitions` (`SharedFlow`) → ViewModels, `AssistanceEffects`, `LocationSignalWatchdog` (ubicación perdida), `AssistancePromptEffect` (notificación 05.3) y `GlanceRefresher` (complicación y vista rápida en vivo) |
| Repository | `RoomRoutineRepository`, `RoomTripRepository`, `RoomDestinationRepository`, `RoomAlertPlaceRepository`, `DataStoreSettingsRepository`, `AuthorizedContactRepository` |
| Adapter | `FusedLocationSource` (Google Play Services → `LocationSource`), `AndroidHapticNotifier`, `NetworkTileSource` / `NominatimDestinationSearch` / `OsrmWalkingRouteService` (OpenStreetMap → puertos del mapa), `AndroidCompassHeadingSource` |
| Decorator / caché en cadena | `CachedMapTileSource` envuelve a `NetworkTileSource`: memoria (LRU) → disco → red, con el mismo contrato `MapTileSource` |
| Factory | `DetectionModeFactory` |
| Facade | `LostDetectionEngine.evaluate(request)` |
| Mapper | `TripEntityMapper`, `RoutineEntityMapper`, `MetricEventEntityMapper`, `DestinationEntityMapper`, `AlertPlaceEntityMapper`, `LostErrorToUiMessageMapper`, `*UiStateMapper`, `GlanceSummaryMapper` (reutiliza el estado de Inicio para que complicación, vista rápida e Inicio digan lo mismo) |
| Use Case | `domain/usecase/*UseCase.kt` (verbo + objeto), p. ej. `GetWeekActivityUseCase`, `GetDisorientationPlacesUseCase` |
| Policy (decisión pura) | `AssistancePromptPolicy` (cuándo preguntar por notificación), `StatusPresentation.of(snapshot)` (estado visible, sin alarmar en la pausa) |

## Estructuras de datos

| Estructura | Uso | Clase |
|---|---|---|
| `ArrayDeque` como buffer circular | Ventana de giros recientes, O(1) al agregar y quitar | `ErraticHeadingSignal` |
| `ArrayDeque` como ventana de tiempo | Puntos GPS de los últimos 15–45 s (según la precisión), promedio ponderado por 1/precisión² | `GpsWindowAverage` |
| Agrupamiento voraz por distancia | Alertas a menos de una cuadra = un lugar; O(n·k) con pocas alertas | `AlertPlaceGrouper` |
| `HashMap<GridCell, MutableList<Int>>` | Grilla de 20 m: "¿ya pasé por aquí?" en O(1) | `SpatialGrid`, `LoopSignal` |
| Matriz DTW de dos filas (`DoubleArray`) | O(n·m) tiempo, O(m) memoria, banda de Sakoe-Chiba | `DtwSequenceComparator` |
| `PriorityQueue<RoutineMatch>` | Rutina más parecida (top-k) | `RoutineMatcher` |
| Estadística acumulada de Welford | Media y desviación de la duración sin guardar todo | `RunningStatistics`, `DurationStatistics` |
| `EnumSet<DayOfWeek>` | Días de una rutina | `RoutineSchedule`, `DayOfWeekSetCodec` |
| `sealed interface` / `enum class` | Estados, niveles, razones, errores, eventos | `AssistanceState`, `AssistanceLevel`, `DeviationReason`, `LostError`, `AssistanceEvent` |
| `@JvmInline value class` | Unidades seguras | `Meters`, `Probability`, `Degrees` |
| `LinkedHashMap` con orden de acceso (LRU) | Teselas descargadas e imágenes decodificadas: lo menos usado sale primero, O(1) | `LruCache`, `CachedMapTileSource`, `RouteMapPresenter` |
| Proyección Web Mercator | Lat/lon → píxeles del mundo y teselas z/x/y; encuadre por búsqueda del zoom máximo que cabe | `WebMercator`, `MapViewport`, `ViewportFitter` |

## Mapa real (ver `adr/004-mapa-openstreetmap.md`)

```
Monitoreo / Detalle ─► MapUiStateFlow ─► RouteMapPresenter ─► LoadRouteMapUseCase ─► MapTileSource
                         (¿mapa real?)    (decodifica, LRU)     (encuadre + teselas)    └ CachedMapTileSource
                                                                                           memoria → disco → red (OSM)
        ◄─ MapUiState.Real (RealMapView) · Simple / Unavailable (RouteComparisonMap) ◄─
```

## Reglas de código

- detekt (`config/detekt.yml`): clase ≤ 150 líneas, función ≤ 30, ≤ 4 parámetros por función, ≤ 10 funciones por clase, complejidad ≤ 10, sin números mágicos (excepto tests y tokens).
- ktlint (`.editorconfig`, estilo `android_studio`).
- Comando: `./gradlew ktlintCheck detekt test`.
