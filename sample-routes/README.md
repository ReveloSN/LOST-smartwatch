# Recorridos de demostración (GPX)

Archivos que reproduce el modo demo (`SimulatedLocationSource`) en las compilaciones *debug*.
Se empaquetan como *assets* del módulo `data` y se generan con `tools/generate_osm_routes.py`.

**La historia:** la casa queda en **Catambuco** y la rutina es ir a la **Universidad de Nariño, sede
Torobajo (Pasto)**, de lunes a viernes, saliendo a las 6:40. El trayecto es el real: 250 m a pie hasta la
Panamericana, bus hasta Torobajo y los últimos ~430 m a pie hasta el campus (≈ 35 min, llegada 7:15).

**Siguen calles reales** (OSRM sobre datos de OpenStreetMap: perfil en carro para el bus y a pie para
caminar). Para mover la demo a otra casa o campus se cambian `HOME` y `UNIVERSITY` en el script y se vuelve
a ejecutar: los destinos de ejemplo (`DemoDestinations`) y la rutina que aprende la demo
(`DemoRoutineLearning`) salen de estos GPX, así que se mueven solos.

| Archivo | Historia | Qué debería pasar |
|---|---|---|
| `demo_normal.gpx` | De la casa en Catambuco a la universidad por la ruta de siempre (≈ 11 km, 35 min). | Nivel bajo, anillo aqua que respira. |
| `demo_voluntary_detour.gpx` | Se baja del bus en Torobajo y, en vez de entrar al campus, camina estable hacia una papelería. | Nivel bajo + pregunta suave "¿Vas a otro lado hoy?". |
| `demo_disorientation.gpx` | Calle cerrada: al bajarse del bus no puede seguir por su calle, da 2 vueltas a una manzana real que nunca toca la ruta, vuelve y entra a la U. | "¿Necesitas ayuda?" → guía → "Ya estás en tu ruta". |
| `demo_new_place.gpx` | Primera visita a la Fundación Hospital San Pedro: da 3 vueltas a una manzana vecina y llega al consultorio. | Modo lugar nuevo: pregunta al dar vueltas y luego llega. |

Las manzanas de las vueltas se eligen solas: de varias candidatas, la de menor perímetro que encierra un
área real (en Pasto las calles no forman una cuadrícula y un cuadrado cualquiera puede terminar en ir y
volver por la misma calle).

**En la demo**, lo que se camina se reproduce 10 veces más rápido y el tramo en bus 40 veces más rápido
(`DemoPlaybackPace`): la ruta completa toma un poco más de minuto y medio. El motor siempre ve los tiempos
reales.

Regenerar (necesita internet; hace unas 60 peticiones con pausa de 1 s, respetando la política de uso de OSM):

```bash
python tools/generate_osm_routes.py
```

El resultado es determinista mientras no cambien los datos de OpenStreetMap. Las cuatro historias se
verifican de punta a punta en `GpxDemoEndToEndTest` y con ruido de GPS en `GpsNoiseRobustnessTest`.

Datos de mapa © OpenStreetMap contributors (ODbL).
