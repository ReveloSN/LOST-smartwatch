# Recorridos de demostración (GPX)

Archivos que reproduce el modo demo (`SimulatedLocationSource`) en las compilaciones *debug*.
Se empaquetan como *assets* del módulo `data` y se generan con `tools/generate_osm_routes.py`.

**Siguen calles reales** (OSRM con perfil a pie sobre datos de OpenStreetMap) alrededor de una sola
constante, `CENTER` = Universidad Nacional de Colombia, Bogotá. Para mover la demo a otro campus o
barrio se cambia `CENTER` y se vuelve a ejecutar el script: los destinos de ejemplo (`DemoDestinations`)
y la duración de la rutina (`DemoDataSeeder`) se calculan a partir de estos GPX, así que se mueven solos.

| Archivo | Historia | Qué debería pasar |
|---|---|---|
| `demo_normal.gpx` | Sebastián va de su casa en Teusaquillo a la U por su ruta de siempre (≈ 1,5 km, 17 min). | Nivel bajo, anillo aqua que respira. |
| `demo_voluntary_detour.gpx` | A un tercio del camino sale de la ruta y camina estable hacia una tienda. | Nivel bajo + pregunta suave "¿Vas a otro lado hoy?". |
| `demo_disorientation.gpx` | Calle cerrada: sale de la ruta, da 3 vueltas a una manzana real que nunca toca la ruta, vuelve y sigue a la U. | "¿Necesitas ayuda?" → guía → "Ya estás en tu ruta". |
| `demo_new_place.gpx` | Don Jorge llega a la Clínica Palermo (la clínica más cercana a `CENTER` según Nominatim), da 3 vueltas a la manzana y llega al consultorio. | Modo lugar nuevo: pregunta al dar vueltas y luego llega. |

Regenerar (necesita internet; hace unas 8 peticiones con pausa de 1 s, respetando la política de uso de OSM):

```bash
python tools/generate_osm_routes.py
```

El resultado es determinista: con el mismo `CENTER` se obtienen los mismos archivos. Las cuatro historias
se verifican de punta a punta en `GpxDemoEndToEndTest`.

Datos de mapa © OpenStreetMap contributors (ODbL).
