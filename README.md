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
- **Mapa real** de OpenStreetMap (sin llave de API): tu ruta de siempre y la de hoy sobre calles reales; sin internet, mini-mapa simple.
- **Destinos reales por voz**: si no está guardado, lo busca en OpenStreetMap y lo guarda con sus coordenadas.
- **Flecha con brújula**: en la guía, la flecha gira según hacia dónde miras.
- Español, inglés y portugués; texto grande; TalkBack; contraste WCAG AA verificado por prueba.

## Requisitos

- Android Studio reciente con el SDK 37 y JDK 21 (el daemon de Gradle usa JDK 21).
- Un emulador o reloj **Wear OS** (la app declara `android.hardware.type.watch`). En Android Studio: *Device Manager → Create device → Wear OS → Wear OS Large Round*.

## Ejecutar

```bash
./gradlew :app:installDebug
```

La compilación *debug* corre en **modo demo**: GPS simulado con los GPX de `sample-routes/` (calles reales alrededor de la Universidad Nacional de Colombia, Bogotá), aviso al contacto simulado (nunca envía un SMS real) y datos de ejemplo (rutina "Casa → U", Laura como contacto, destinos del campus y la Clínica Palermo).

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

## Mapa real y privacidad

El mapa usa **OpenStreetMap** con un renderizador propio (ver `docs/adr/004-mapa-openstreetmap.md`). Se puede apagar en
**Ajustes → Mapa real (usa internet)**; apagado, LOST no hace ninguna petición a internet.

| Qué sale del reloj | Cuándo |
|---|---|
| La zona del mapa (tesela z/x/y) | Al ver Monitoreo o el detalle de una rutina. |
| El texto buscado (sin "llévame a…") | Al enviar un dictado que no coincide con un destino guardado. |
| Origen y destino de una ruta a pie | Al empezar "Lugar nuevo" (la pantalla lo avisa antes). |
| **Nunca** | Los recorridos aprendidos, el historial ni el contacto. |

Atribución: © OpenStreetMap contributors (bajo el estado en Monitoreo y en Ajustes → Acerca de).
Las rutas de demo se regeneran con `python tools/generate_osm_routes.py` cambiando una sola constante (`CENTER`).

## Verificar

```bash
./gradlew ktlintCheck detekt test assembleDebug
```

| Módulo | Pruebas |
|---|---|
| `core/designsystem` | 8 (contraste WCAG) |
| `domain` | 91 (máquina de estados, cadena de respuesta, temporizadores, monitor, casos de uso, métricas, mapa, batería) |
| `ai` | 38 (las 5 de escenario de la sección 8.5 + geometría, DTW, señales, aprendizaje, rendimiento) |
| `data` | 29 (mappers, repositorios, DataStore, GPX, demo, háptica, caché de teselas, Nominatim, OSRM) |
| `app` | 45 (mappers de UI, ViewModel, demo GPX de punta a punta, traducciones, accesibilidad, búsqueda de destinos) |
| **Total** | **211** |

## Estructura

```
LOST/
├── app/                 Pantallas (feature/*), navegación, DI raíz, integración
├── core/designsystem/   Tokens en 3 niveles, tema, átomos → plantillas
├── domain/              Kotlin puro: modelos, puertos, casos de uso, máquina de estados
├── ai/                  Kotlin puro: motor de detección y aprendizaje
├── data/                Room, DataStore, GPS, háptica, contactos, métricas, demo
├── sample-routes/       GPX de la demo (calles reales)
├── tools/               generate_osm_routes.py (regenera los GPX con OSRM)
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
| Decisiones (ADR) | `docs/adr/` (001 arquitectura · 002 DTW · 003 máquina de estados · 004 mapa OpenStreetMap) |
| Simplificación de la interfaz (antes/después) | `docs/diseno/simplificacion.md` |
| Mockups | `docs/design/LOST v2.dc.html` |

## Stack

Kotlin 2.3 · Jetpack Compose for Wear OS (Material 3) · MVVM + UDF · Hilt · Coroutines/Flow · Room · DataStore · Fused Location Provider · JUnit 5, MockK, Turbine · detekt + ktlint.
