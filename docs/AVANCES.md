# LOST — Avances del proyecto

Registro de lo que se hizo en cada fase del plan (sección 11 de `CLAUDE.md`), las pruebas que pasan,
las decisiones tomadas y lo que queda pendiente.

## Resumen de fases

| # | Fase | Estado |
|---|---|---|
| 0 | Configuración | ✅ Completa |
| 1 | Design system | ✅ Completa |
| 2 | Dominio | ⏳ |
| 3 | Motor de IA | ⏳ |
| 4 | Datos | ⏳ |
| 5 | Pantallas | ⏳ |
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
