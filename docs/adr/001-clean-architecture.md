# ADR 001 — Clean Architecture con módulos

- **Estado**: aceptada
- **Fecha**: 2026-10-18

## Contexto

LOST combina un motor de IA (geometría, secuencias, estadística), una máquina de estados de asistencia, persistencia, sensores (GPS, vibración) y una UI de reloj muy cuidada. El curso evalúa el diseño y exige buenas prácticas de ingeniería: el motor y las reglas deben poder probarse sin un reloj.

## Decisión

Separar el proyecto en cinco módulos Gradle con dependencias en una sola dirección:

```
app ──► domain ◄── data
 │        ▲
 │        └── ai
 └──► core/designsystem
```

- `domain` (Kotlin puro): modelos, puertos (interfaces pequeñas), casos de uso, máquina de estados y sesión de recorrido.
- `ai` (Kotlin puro): el motor; implementa los puertos `TripEvaluator`, `RoutineLearningEngine`, `ThresholdAdjuster` y `GuidanceCalculator`.
- `data` (Android): Room, DataStore, GPS, vibración, SMS, demo; implementa el resto de puertos.
- `core/designsystem` (Android, sin lógica de negocio): tokens, tema y componentes.
- `app`: pantallas (MVVM con UiState inmutable y UiEvent), navegación, DI raíz e integración.

Hilt conecta puertos e implementaciones (`DataBindingsModule`, `AiModule`). `ai` no depende de Hilt: `LostEngineAssembly` arma sus piezas.

## Consecuencias

- ✅ `domain` y `ai` se prueban con JUnit 5 en milisegundos (≈ 100 pruebas sin emulador).
- ✅ Inversión de dependencias: cambiar el GPS por un GPX (demo) o Room por otra base no toca el dominio.
- ✅ La regla de dependencias la impone Gradle: `domain` no puede importar Android aunque alguien lo intente.
- ⚠️ Más archivos y más "fontanería" (mappers, puertos) que una app de un solo módulo.
- ⚠️ La compilación inicial es más lenta (KSP en `app` y `data`).
