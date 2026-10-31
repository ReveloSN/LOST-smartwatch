# ADR 004 — Mapa real con OpenStreetMap y un renderizador propio

- **Estado**: aceptada
- **Fecha**: 2026-10-25

## Contexto

Los mockups mostraban rutas y direcciones de ejemplo sobre un mini-mapa vectorial (dos líneas sobre negro).
Para la entrega el mapa debe ser **real**: calles, nombres y la ruta de siempre sobre la ciudad. Restricciones:

- Es un **reloj**: pantalla redonda de 1,4", gestos ya ocupados (pager horizontal entre secciones,
  deslizar a la derecha para volver, corona para desplazar listas).
- Fondo **OLED negro** y la ruta como protagonista (PDF "Comunicación con los usuarios").
- **Privacidad**: los recorridos aprendidos y el contacto nunca salen del reloj (sección 9.5 y Privacidad).
- Proyecto académico: **sin llave de API** ni cuenta de facturación.

## Opciones

1. **Google Maps SDK / Maps Compose** — buen mapa, pero exige llave de API y cuenta con facturación,
   añade Play Services Maps (APK más pesado) y su estilo oscuro no se integra con los tokens. Además el mapa
   es interactivo por defecto (gestos que chocan con el pager y con deslizar-para-volver).
2. **osmdroid** — usa OpenStreetMap sin llave, pero es una vista `View` clásica pensada para teléfono,
   interactiva, con su propio sistema de caché y estilos difíciles de llevar a Compose y a tokens.
3. **OpenStreetMap + renderizador propio en Compose** — teselas por HTTP, matemática Web Mercator en Kotlin puro,
   caché propia y un organismo `RealMapView` que dibuja teselas y rutas en un `Canvas`.

## Decisión

Opción 3.

- **Dominio (Kotlin puro, probado)**: `WebMercator`, `MapViewport` (teselas visibles y proyección de puntos),
  `ViewportFitter` (encuadre automático sin gestos), `LruCache` y los puertos `MapTileSource`,
  `DestinationSearchService` y `WalkingRouteService`.
- **Datos (Adapters)**: `NetworkTileSource` (tile.openstreetmap.org con User-Agent propio), `CachedMapTileSource`
  (memoria LRU → disco una semana → red), `NominatimDestinationSearch` (solo al enviar la búsqueda, sin autocompletar)
  y `OsrmWalkingRouteService` (perfil a pie).
- **Design system**: `RealMapView` (organismo) dibuja teselas oscurecidas con un `ColorFilter` (invertir, casi sin color,
  55 % de brillo), la ruta de siempre en gris tenue, la de hoy en el color del estado y el punto del usuario.
  **No es interactivo**: el encuadre se ajusta solo.
- **Respaldo**: sin internet o con el interruptor apagado se ve el mini-mapa vectorial de siempre (`RouteComparisonMap`)
  y una línea de qué hace LOST (mensaje de tres partes `MapUnavailable`).
- **Atribución** "© OpenStreetMap contributors" siempre visible bajo el estado y en Ajustes → Acerca de.

## Política de uso de OpenStreetMap

- User-Agent propio que identifica la app y el repositorio.
- Como mucho 9 teselas por pantalla (en la práctica 4–6, porque se ven al 150 %), sin precarga ni descargas masivas.
- Caché en memoria (48 teselas) y en disco (400 archivos, una semana): una zona no se vuelve a pedir.
- Nominatim: una petición por búsqueda enviada, 3 resultados. OSRM: una ruta por destino elegido.
- El generador de GPX (`tools/generate_osm_routes.py`) espera 1 s entre peticiones.

## Privacidad

| Qué sale del reloj | Cuándo |
|---|---|
| Zona del mapa (z/x/y de la tesela) | Al ver Monitoreo o el detalle de una rutina, con el mapa real encendido. |
| Texto de la búsqueda (sin "llévame a…") | Solo al enviar un dictado que no coincide con un destino guardado. |
| Origen y destino de una ruta a pie | Solo al empezar "Lugar nuevo"; la pantalla lo dice antes. |
| **Nunca** | Los recorridos aprendidos, el historial ni el contacto autorizado. |

Interruptor en Ajustes: **"Mapa real (usa internet)"**. Apagado, LOST no hace ninguna petición a OpenStreetMap.

## Consecuencias

- (+) Sin llave de API, sin costo, sin dependencias nuevas (HTTP con `HttpURLConnection`, JSON con `org.json`).
- (+) Toda la matemática del mapa se prueba con JUnit sin emulador.
- (+) El estilo del mapa sale de los tokens (`ComponentTokens.RealMap`).
- (−) No hay zoom ni arrastre: a propósito, pero si un usuario lo pidiera habría que añadirlo como pantalla aparte.
- (−) Depende de servidores comunitarios (tile.openstreetmap.org, Nominatim, routing.openstreetmap.de):
  para producción habría que usar un proveedor de teselas propio o de pago.
- Tiempo: el renderizador propio tomó menos de 2 horas, así que no hizo falta recurrir a osmdroid.
