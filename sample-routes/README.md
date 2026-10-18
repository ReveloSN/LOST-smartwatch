# Recorridos de demostración (GPX)

Archivos que reproduce el modo demo (`SimulatedLocationSource`) en las compilaciones *debug*.
Se empaquetan como *assets* del módulo `data` y se generan con `generate_routes.py`
(misma geometría que los recorridos sintéticos de las pruebas del motor).

| Archivo | Historia | Qué debería pasar |
|---|---|---|
| `demo_normal.gpx` | Sebastián va de la casa a la U por su ruta de siempre (600 m al este, 500 m al norte). | Nivel bajo, anillo aqua que respira. |
| `demo_voluntary_detour.gpx` | Se desvía a una tienda caminando recto y estable. | Nivel bajo + pregunta suave "¿Vas a otro lado hoy?". |
| `demo_disorientation.gpx` | La avenida está cerrada: toma otra calle, da 3 vueltas a la misma manzana, cambia de rumbo, se aleja y luego vuelve a su ruta. | Nivel medio/alto → "¿Necesitas ayuda?" → guía → "Ya estás en tu ruta". |
| `demo_new_place.gpx` | Don Jorge en el hospital, sin rutina: da vueltas cerca del ascensor y luego llega al consultorio. | Modo lugar nuevo, nivel medio: "Volviste al mismo punto. ¿Te ayudo a llegar?". |

Regenerar: `python sample-routes/generate_routes.py`
