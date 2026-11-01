# ADR 005 — Lugares donde te desorientas, sin geocodificación inversa

- **Estado:** aceptada (1 de noviembre)
- **Contexto de diseño:** mockup 03.2 "Lugares donde más te desorientas" ("Solo lo ves tú, salvo que lo compartas").

## Contexto

La pantalla 03.2 muestra dónde preguntó LOST en el último mes. El mockup nombra cada lugar como una dirección
("Avenida 19 con calle 45"). Para obtener ese nombre hay que enviar las coordenadas a un servicio de
geocodificación inversa (Nominatim u otro). LOST ya usa internet para el mapa, la búsqueda y la ruta a pie, pero
siempre con datos que el usuario eligió mostrar o buscar (ADR 004).

Las coordenadas de esta pantalla son distintas: dicen **dónde se desorientó una persona**, a veces una persona
mayor o neurodivergente. Es información sensible y el mockup promete que solo la ve el usuario.

## Decisión

- Cada vez que LOST pregunta se guarda en Room, en el reloj, el punto, la causa principal (`DeviationCause`) y la
  rutina (`AlertPlace`, tabla `alert_places`).
- El nombre del lugar sale de lo que LOST ya sabe, **sin internet** (`PlaceLabeler`):
  1. "Cerca de {destino guardado}" si hay uno a menos de 250 m;
  2. si no, "En tu ruta {rutina}";
  3. si no, "Un lugar nuevo".
- Las alertas a menos de 100 m (una cuadra) se agrupan en un lugar (`AlertPlaceGrouper`); se muestran hasta 3,
  del más frecuente al menos.
- "Borrar lo aprendido" también borra estos lugares.

## Alternativas descartadas

| Alternativa | Por qué no |
|---|---|
| Geocodificación inversa con Nominatim | Enviaría a un tercero las coordenadas de los momentos difíciles; contradice "Solo lo ves tú". |
| Geocodificación inversa solo con el mapa real encendido | El interruptor del mapa no comunica que también se enviarían estos puntos; mezclar permisos confunde. |
| Mostrar coordenadas | No es lenguaje del usuario. |

## Consecuencias

- ✅ La ubicación de las alertas nunca sale del reloj; lo prueba el diseño (no hay ningún puerto de red en el caso
  de uso `GetDisorientationPlacesUseCase`).
- ✅ Los nombres son significativos para el usuario porque son sus propios destinos y rutinas.
- ⚠️ Un lugar lejos de todo destino y fuera de una rutina se llama "Un lugar nuevo": menos preciso que una
  dirección. Si en el futuro se quiere la dirección, debe ser una opción explícita y aparte en Privacidad.
- La base pasa a la versión 2 con migración automática (solo una tabla nueva), probada en el reloj
  (`LostDatabaseMigrationTest`).
