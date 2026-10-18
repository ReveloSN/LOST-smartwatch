# LOST — Avances del proyecto

Registro de lo que se hizo en cada fase del plan (sección 11 de `CLAUDE.md`), las pruebas que pasan,
las decisiones tomadas y lo que queda pendiente.

## Resumen de fases

| # | Fase | Estado |
|---|---|---|
| 0 | Configuración | ✅ Completa |
| 1 | Design system | ⏳ |
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
