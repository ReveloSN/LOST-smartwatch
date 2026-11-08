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
- **Robusta al GPS del reloj**: usa la precisión de cada punto (ignora los malos, pesa menos los dudosos) y no confunde una esquina con temblor con dar vueltas; probado con 10 ruidos distintos por variante.
- **Pregunta aunque no mires la app**: con la pantalla apagada llega "¿Todo bien en tu ruta?" como notificación con Guíame y Estoy bien.
- **Avisa si pierde tu ubicación**: tras un minuto sin GPS confiable dice "No encontramos tu ubicación · Camina hacia una salida o ventana".
- **Complicación y vista rápida en vivo**: anillo con el avance, a dónde vas y la hora de llegada.
- **Dónde más te desorientas**: los lugares donde LOST preguntó en el último mes, nombrados sin enviar tu ubicación a nadie.
- **Guía sin mirar**: durante la guía, una vibración de giro avisa si vas en otra dirección.
- **Elige tu contacto** entre los contactos del reloj (sin selector del sistema en Wear OS, la lista es propia).
- Español, inglés y portugués; texto grande; TalkBack; contraste WCAG AA verificado por prueba.

## Requisitos

- Android Studio reciente con el SDK 37 y JDK 21 (el daemon de Gradle usa JDK 21).
- Un emulador o reloj **Wear OS** (la app declara `android.hardware.type.watch`). En Android Studio: *Device Manager → Create device → Wear OS → Wear OS Large Round*.

## Ejecutar

```bash
./gradlew :app:installDebug
```

La compilación *debug* corre en **modo demo**: GPS simulado con los GPX de `sample-routes/` (calles reales de
Pasto: de una casa en **Catambuco** a la **Universidad de Nariño**, sede Torobajo), aviso al contacto simulado
(nunca envía un SMS real), Laura como contacto sugerido y destinos de ejemplo (laboratorio, biblioteca,
cafetería y el Consultorio 304 de la Fundación Hospital San Pedro).

El guion completo para presentarla está en [`docs/presentacion/guion.md`](docs/presentacion/guion.md).

### Acto 1 · Una persona nueva

1. Instala desde cero (`adb shell pm clear com.lost.watch` para volver a empezar) y completa la bienvenida
   (elige a Laura). **Mis recorridos** está vacío: LOST todavía no conoce tus recorridos.
2. **Ajustes → Demostración → Aprender un día de ruta**: cada toque pasa un día de la semana pasada (la ruta de
   siempre, de lunes a viernes cerca de las 6:40) por el aprendizaje real de LOST. Tras el primero, **Mis
   recorridos** muestra "Nuevo recorrido" y su progreso "1 de 5"; al quinto, **Casa → U · L–V · 6:40 a. m. ·
   35 min**, con el mapa real de Pasto en su detalle.

### Acto 2 · Una semana después

Elige un recorrido en **Ajustes → Demostración**: se abre el monitoreo con el mapa en vivo (en rutas largas, una
ventana de 1 km centrada en la persona).

- **Ruta de siempre** → LOST solo observa; el anillo aqua respira.
- **Desvío voluntario** → se baja del bus y camina a otro lado: "¿Vas a otro lado hoy?" una sola vez (~85 s).
- **Calle cerrada** → vueltas a una manzana cerca de la U → "¿Necesitas ayuda?" (~75 s) → "Sí, guíame" → flecha
  → "Ya estás en tu ruta".
- **Lugar nuevo (hospital)** → "Pasaste 3 veces por la misma cuadra. ¿Necesitas ayuda?" (~65 s) → guía hasta el
  Consultorio 304.

Si se elige un recorrido de la ruta de siempre antes de aprenderla, la demo aprende primero la semana completa.
Luego, **Mis recorridos → Tu semana** muestra la semana y **Dónde te desorientas**, los lugares de las alertas
que acabas de provocar.

Más para mostrar:

- **Pregunta por notificación (05.3):** elige **Calle cerrada** y sal a la esfera con el botón del reloj. Al minuto
  llega "LOST · ¿Todo bien en tu ruta?" con **Guíame** / **Estoy bien**; si no respondes, "Voy a avisar a tu
  contacto." con **Cancelar**.
- **Pausa:** responde **Estoy bien** y vuelve a Inicio: "Pausa de 2 minutos".
- **Ubicación perdida:** toca **Empezar recorrido** sin elegir un recorrido de demostración (no llega ningún punto
  GPS). Al minuto Inicio dice "No encontramos tu ubicación · Camina hacia una salida o ventana".
- **Sin permiso de ubicación** (compilación *release*): Inicio explica qué pasó y ofrece **Permitir ubicación**;
  si Android ya no muestra el diálogo, abre los ajustes de LOST.
- **Vista rápida y complicación (05):** agrega la Tile "LOST" en el carrusel de Tiles (o
  `adb shell am broadcast -a com.google.android.wearable.app.DEBUG_SURFACE --es operation add-tile --ecn component com.lost.watch/com.lost.watch.tile.MainTileService`)
  y la complicación "Estado de LOST" en una esfera que admita anillos.

Lo que se camina se reproduce 10× más rápido y el tramo en bus 40×; el motor siempre ve los tiempos reales.
**Para que el mapa cargue, el emulador debe tener la hora correcta**: enciéndelo en frío
(`emulator -avd <nombre> -no-snapshot-load`); con la hora de un *snapshot* viejo el certificado de
OpenStreetMap "todavía no es válido" y se ve la ruta sin calles.

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
| `core/designsystem` | 11 (contraste WCAG, líneas por rol de texto) |
| `domain` | 120 (máquina de estados, cadena de respuesta, temporizadores, monitor, aviso de ubicación perdida, lugares de desorientación, casos de uso, métricas, mapa, batería) |
| `ai` | 51 (las 5 de escenario de la sección 8.5 + geometría, remuestreo con ruido y precisión del GPS, DTW, señales, aprendizaje, rendimiento) |
| `data` | 31 (mappers, repositorios, DataStore, GPX, demo y sus lugares de ejemplo, háptica, caché de teselas, Nominatim, OSRM) |
| `app` | 276 (mappers de UI, ViewModel, demo GPX de punta a punta, ruido de GPS con 10 semillas por variante, complicación y vista rápida, notificación, traducciones, accesibilidad) |
| **Total JVM** | **489** |

En el emulador de Wear OS (23 pruebas instrumentadas: Room contra SQLite real y su migración, botones de la notificación, alertas en la pantalla
redonda, pantallas de error con letra normal y grande, TalkBack, Texto grande y pantallas sin internet):

```bash
./gradlew :data:connectedDebugAndroidTest :app:connectedDebugAndroidTest
```

Baseline Profile (genera el perfil y mide el arranque; borra los datos de la app de depuración):

```bash
./gradlew :app:generateBaselineProfile
```

Herramientas en Python (cálculo del SUS):

```bash
python -m unittest discover -s tools -p "test_*.py"
```

## Estructura

```
LOST/
├── app/                 Pantallas (feature/*), navegación, DI raíz, integración
├── core/designsystem/   Tokens en 3 niveles, tema, átomos → plantillas
├── domain/              Kotlin puro: modelos, puertos, casos de uso, máquina de estados
├── ai/                  Kotlin puro: motor de detección y aprendizaje
├── data/                Room, DataStore, GPS, háptica, contactos, métricas, demo
├── sample-routes/       GPX de la demo (calles reales)
├── baselineprofile/     Generador del Baseline Profile y benchmark de arranque (Macrobenchmark)
├── tools/               generate_osm_routes.py (GPX con OSRM) · calcular_sus.py (puntaje SUS)
├── config/detekt.yml    Reglas de calidad
└── docs/                Diseño, investigación, IA, contenido, marca, métricas y ADRs
```

## Documentación

| Tema | Archivo |
|---|---|
| Avances por fase, decisiones y pendientes | `docs/AVANCES.md` (último: "Jornada del 1 de noviembre") |
| Arquitectura, SOLID, patrones y estructuras de datos | `docs/arquitectura.md` |
| Arquitectura de información | `docs/ia/arquitectura-informacion.md` |
| Motor de IA | `docs/ia/motor-ia.md` |
| Personas, journey map, problema, entrevistas | `docs/investigacion/` |
| Voz, tono, glosario y errores | `docs/contenido/glosario-voz.md` |
| Personalidad de la marca | `docs/marca/personalidad.md` |
| Métricas y cuestionario SUS | `docs/metricas/` |
| Decisiones (ADR) | `docs/adr/` (001 arquitectura · 002 DTW · 003 máquina de estados · 004 mapa OpenStreetMap · 005 lugares sin geocodificación) |
| Simplificación de la interfaz (antes/después) | `docs/diseno/simplificacion.md` |
| Tipografía Manrope (licencia y peso) | `docs/diseno/tipografia-manrope.md` |
| Fidelidad con los mockups | `docs/diseno/fidelidad-mockups.md` |
| Revisión de accesibilidad (Texto grande, en-XA, TalkBack) | `docs/diseno/accesibilidad-revision.md` |
| Robustez de la IA ante el ruido del GPS | `docs/ia/robustez-ruido-gps.md` |
| Guía para aplicar el SUS | `docs/metricas/guia-aplicacion-sus.md` |
| Mockups | `docs/design/LOST v2.dc.html` |

## Stack

Kotlin 2.3 · Jetpack Compose for Wear OS (Material 3) · MVVM + UDF · Hilt · Coroutines/Flow · Room · DataStore · Fused Location Provider · JUnit 5, MockK, Turbine · detekt + ktlint.
