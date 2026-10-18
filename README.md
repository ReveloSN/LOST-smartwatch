# LOST — un guía sereno en tu muñeca

App nativa para **Wear OS** que aprende tus recorridos habituales y nota cuándo el de hoy no se parece al de siempre. Si detecta una desorientación, primero pregunta, luego te guía de vuelta y, si hace falta, avisa a tu contacto autorizado — sin sacar el celular.

> Proyecto final de **Diseño de Interfaces de Software**. Mockups en `docs/design/`, plan completo en `CLAUDE.md`, registro de avances en `docs/AVANCES.md`.

## Qué hace

- **Aprende tu rutina** (Casa → U, L–V, 6:40, 35 min) tras 5 repeticiones.
- **Compara secuencias, no distancias**: DTW contra tu ruta de siempre + señales de bucles, giros, paradas, retraso y alejamiento.
- **Distingue un desvío voluntario** ("¿Vas a otro lado hoy?") de una **desorientación** ("¿Necesitas ayuda?").
- **Ayuda escalonada**: vibrar y preguntar → guiar con una flecha → compartir la ubicación (cuenta de 5 s cancelable).
- **Aprende de ti**: "Estoy bien" hace que pregunte menos en casos así.
- **Lugar nuevo** (hospital, aeropuerto): sin rutina, detecta vueltas y regresos al mismo punto.
- Español, inglés y portugués; texto grande; TalkBack; contraste WCAG AA verificado por prueba.

## Requisitos

- Android Studio reciente con el SDK 37 y JDK 21 (el daemon de Gradle usa JDK 21).
- Un emulador o reloj **Wear OS** (la app declara `android.hardware.type.watch`). En Android Studio: *Device Manager → Create device → Wear OS → Wear OS Large Round*.

## Ejecutar

```bash
./gradlew :app:installDebug
```

La compilación *debug* corre en **modo demo**: GPS simulado con los GPX de `sample-routes/`, aviso al contacto simulado (nunca envía un SMS real) y datos de ejemplo (rutina "Casa → U", Laura como contacto, destinos del campus).

### Demo de la historia de Sebastián

1. Abre LOST y completa la bienvenida (elige a Laura).
2. En **Inicio** toca **Demostración** (o **Ajustes → Demostración**).
3. Elige un recorrido:
   - **Ruta de siempre** → LOST solo observa; el anillo aqua respira.
   - **Desvío voluntario** → "¿Vas a otro lado hoy?" una sola vez.
   - **Calle cerrada** → vueltas a la misma cuadra → "¿Necesitas ayuda?" → "Sí, guíame" → flecha → "Ya estás en tu ruta".
   - **Lugar nuevo (hospital)** → "Pasaste 3 veces por la misma cuadra. ¿Necesitas ayuda?" → guía hasta el Consultorio 304.
4. Desliza entre secciones para ver **Mis recorridos**, **Destinos**, **Contactos**, **Ajustes** y **Tu semana** (métricas).

La reproducción va 10× más rápido que caminar, pero conserva los tiempos reales para el motor.

## Verificar

```bash
./gradlew ktlintCheck detekt test assembleDebug
```

| Módulo | Pruebas |
|---|---|
| `core/designsystem` | 8 (contraste WCAG) |
| `domain` | 64 (máquina de estados, cadena de respuesta, temporizadores, monitor, casos de uso, métricas) |
| `ai` | 34 (las 5 de escenario de la sección 8.5 + geometría, DTW, señales, aprendizaje) |
| `data` | 21 (mappers, repositorios, DataStore, GPX, demo, háptica) |
| `app` | 40 (mappers de UI, ViewModel, demo GPX de punta a punta, traducciones, accesibilidad) |
| **Total** | **167** |

## Estructura

```
LOST/
├── app/                 Pantallas (feature/*), navegación, DI raíz, integración
├── core/designsystem/   Tokens en 3 niveles, tema, átomos → plantillas
├── domain/              Kotlin puro: modelos, puertos, casos de uso, máquina de estados
├── ai/                  Kotlin puro: motor de detección y aprendizaje
├── data/                Room, DataStore, GPS, háptica, contactos, métricas, demo
├── sample-routes/       GPX de la demo + generate_routes.py
├── config/detekt.yml    Reglas de calidad
└── docs/                Diseño, investigación, IA, contenido, marca, métricas y ADRs
```

## Documentación

| Tema | Archivo |
|---|---|
| Avances por fase, decisiones y pendientes | `docs/AVANCES.md` |
| Arquitectura, SOLID, patrones y estructuras de datos | `docs/arquitectura.md` |
| Arquitectura de información | `docs/ia/arquitectura-informacion.md` |
| Motor de IA | `docs/ia/motor-ia.md` |
| Personas, journey map, problema, entrevistas | `docs/investigacion/` |
| Voz, tono, glosario y errores | `docs/contenido/glosario-voz.md` |
| Personalidad de la marca | `docs/marca/personalidad.md` |
| Métricas y cuestionario SUS | `docs/metricas/` |
| Decisiones (ADR) | `docs/adr/` |
| Mockups | `docs/design/LOST v2.dc.html` |

## Stack

Kotlin 2.3 · Jetpack Compose for Wear OS (Material 3) · MVVM + UDF · Hilt · Coroutines/Flow · Room · DataStore · Fused Location Provider · JUnit 5, MockK, Turbine · detekt + ktlint.
