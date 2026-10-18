# Planteamiento del problema

## Pregunta guía (How might we)

> ¿Cómo podríamos ayudar a una persona que se desvía o se desorienta a **recuperar su ruta en menos de 30 segundos**, sin que tenga que sacar el celular ni pedir ayuda manualmente?

## Problema

Una persona puede desorientarse en un campus, un hospital, un aeropuerto, un centro comercial o una ciudad desconocida, e incluso en un recorrido que hace todos los días. Repetir rutas, alejarse constantemente del destino o volver varias veces al mismo punto puede indicar que necesita ayuda.

## Por qué no basta con una distancia fija (geocerca)

| Caso | Geocerca de 500 m | LOST (compara con la rutina) |
|---|---|---|
| Don Jorge da vueltas dentro del parque 50 min | Nada pasa: sigue dentro del radio. Está perdido y nadie se entera. | Le pregunta a tiempo: dura el doble, repite la misma zona y la secuencia no coincide. |
| Sebastián a 5 km de casa, camino a la U, a su hora | Falsa alarma: salió del radio. | Silencio correcto: es su ruta de siempre, las dos líneas coinciden. |

LOST no pregunta "¿qué tan lejos está?", sino **"¿esto se parece a lo que hace siempre?"**. Ver `docs/adr/002-dtw-vs-distancia-fija.md`.

## Funcionamiento

LOST registra destinos y patrones habituales. Si detecta una desviación significativa calcula un nivel de probabilidad de desorientación y puede (1) vibrar para preguntar, (2) mostrar una ruta alternativa o (3) compartir la ubicación con un contacto autorizado.

## Valor diferencial

El reloj es un apoyo contextual que detecta la necesidad **antes** de que haya que pedir ayuda, y que **no molesta** cuando el desvío es voluntario.

## Metas (ver `docs/metricas/metricas.md`)

Éxito ≥ 90 % · decisión < 5 s · recuperación < 30 s · SUS ≥ 80 · falsos positivos < 10 %.
