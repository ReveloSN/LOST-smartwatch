# User personas

> Lámina "02 — User personas" de los mockups. Tres personas, tres formas de perderse.

## Sebastián, 20 — persona principal

- Estudiante universitario, alfabetización digital alta.
- **Rutina**: Casa → U, lunes a viernes, 6:40 a. m., 35 min · U → Casa, 5:00 p. m., 40 min.
- **Necesita**: que LOST no lo moleste cuando cambia de camino a propósito, pero que lo ayude si de verdad se pierde (calles cerradas, desvíos, edificios nuevos del campus).
- **LOST le da**: silencio en su rutina, una pregunta suave en desvíos voluntarios y guía inmediata cuando da vueltas.
- **En el código**: modo rutina (`RoutineDetectionMode`), `VoluntaryDetourDetector` → `SoftAsk`, escenario GPX *Calle cerrada*.

## Don Jorge, 68

- Adulto mayor, alfabetización digital baja, vista reducida. Contacto: Laura (hija).
- **Rutina**: paseo al parque todas las tardes; citas en un hospital grande.
- **Necesita**: ayuda sin tener que pedirla; texto grande y voz.
- **LOST le da**: detección dentro del parque (sin geocerca, comparando con la rutina), modo lugar nuevo en el hospital y aviso a Laura.
- **En el código**: `NewPlaceDetectionMode`, `LargeTextScale` (+30 %), alertas como regiones vivas para TalkBack, `SharingCountdown` → `LocationShared`, escenario GPX *Lugar nuevo*.

## Andrés, 34

- Viajero de negocios, manos ocupadas, lugares nuevos (aeropuertos, ciudades), siempre con prisa.
- **Necesita**: interacciones de un toque o por voz.
- **LOST le da**: destinos por voz y modo lugar nuevo que avisa si da vueltas o se aleja de su puerta.
- **En el código**: `DestinationsScreen` con `RecognizerIntent` y `DestinationMatcher`, `MovingAwaySignal`.
