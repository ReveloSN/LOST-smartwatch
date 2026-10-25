# Simplificación de la interfaz

Auditoría hecha el 25 de octubre sobre el emulador **Wear OS Large Round (454 × 454)** con la app en español.
Las capturas de antes están en `docs/capturas/antes/` y las de después en `docs/capturas/despues/`.

Se siguió el orden de los PDF del curso: **contenido → jerarquía → estructura → estilo**.

## Reglas aplicadas

| Regla | Cómo se mide |
|---|---|
| Una idea por pantalla | 1 mensaje principal + 1 dato de apoyo + 1 acción primaria (y como mucho 1 secundaria). |
| Pocos gráficos | Máximo 2 elementos gráficos a la vez (anillo/arco, mapa, icono grande, flecha). |
| Textos cortos | Títulos de 3–4 palabras; frases de 8–10 palabras como máximo. |
| Revelación progresiva | Lo explicativo pasa a un segundo nivel ("Toca para saber más"). |
| Sin repeticiones | La misma información no aparece en dos sitios de la pantalla. |
| Zona segura | Nada cortado por el borde redondo; si no cabe, se quita, no se encoge. |
| Estado accesible | Color + icono + texto + vibración, pero **en un solo chip** (`LostChip`). |

## Hallazgos en el emulador (antes)

1. **Permiso de ubicación** (`02`): el texto se cortaba con "…" ("Tus datos se…") y el botón tocaba el borde.
2. **Alerta "¿Necesitas ayuda?"** (`07`): "Estoy bien" quedaba fuera de la pantalla redonda; había que desplazar para ver la segunda respuesta. Además la explicación aparecía dos veces (arriba como contexto y abajo como frase).
3. **Inicio en recorrido** (`06`): 8 elementos (icono, estado, hora, icono de lugar, destino, progreso, 2 botones); el progreso "0 de 13 min" repetía lo que ya muestra el anillo.
4. **Monitoreo** (`11`): arco + chip + mapa + titular + "Lo que noto hoy" + filas de razones + botón; el titular quedaba cortado por debajo.
5. **Guía** (`08`): "Volver a tu ruta de siempre" arriba y "hacia tu ruta de siempre" abajo decían lo mismo.
6. **Tu semana** (`17`): número, letras de los días, frase resumen y 4 métricas con meta: más de 12 líneas en una pantalla de reloj.

## Pantalla por pantalla

| Pantalla | Había | Se quita | Pasa a segundo nivel | Queda (principal · apoyo · acción) | Por qué |
|---|---|---|---|---|---|
| **Bienvenida** | Título, frase de 14 palabras, puntos, botón | — | — | "Hola, soy LOST" · frase de 9 palabras · Empezar | Frase demasiado larga para leer de un vistazo. |
| **Permiso de ubicación** | Icono, pregunta de 4 palabras, frase de 15 palabras (cortada), 2 botones | Media frase | — | "¿Usamos tu ubicación?" · "Así aprendo tus recorridos. Se quedan en tu reloj." · Permitir / Ahora no | El texto se cortaba con "…". |
| **Inicio (reposo)** | Anillo, icono, título, frase, 2 botones | — | — | "LOST está atento" · "Camina y reconoceré tu ruta." · Empezar recorrido / Demostración | Frase acortada a 5 palabras. |
| **Inicio (recorrido)** | Anillo, icono, estado, hora, icono de lugar, destino, progreso, 2 botones | Icono de lugar, línea de progreso ("0 de 13 min", ya está en el anillo), icono suelto del estado | "Terminar recorrido" pasa a Monitoreo | Chip de estado · hora de llegada · "Vas a la U" · Ver detalles | 8 → 4 elementos. El chip une color, icono y texto en un solo bloque. |
| **Monitoreo** | Arco, chip, mapa, titular largo, "Lo que noto hoy", filas de razones con iconos, botón | Arco de probabilidad (el chip ya da el nivel y el arco competía con el mapa), filas de razones con iconos, encabezado "Lo que noto hoy" | Las razones completas viven en "¿Por qué te pregunto?" | Chip · mapa · titular corto · "Toca para saber más" / Terminar recorrido | Máximo 2 gráficos (mapa + chip). Titulares de 3–4 palabras. |
| **Explicación** | Encabezado, frase, 2 iconos | Iconos sueltos | — | "¿Por qué te pregunto?" · frase humana | La frase ya lo dice; los iconos sin texto no suman. |
| **Pregunta suave** | Arco, icono, pregunta, 2 botones | — | — | "¿Vas a otro lado hoy?" · Sí, voy a otro lado / Guíame | Ya cumplía; se verifica que ambos botones quepan. |
| **¿Necesitas ayuda?** | Arco, icono grande, contexto, pregunta, 2 botones, frase de explicación | Frase de explicación repetida al final | La explicación completa se ve en Monitoreo | Contexto corto · pregunta · Sí, guíame / Estoy bien | "Estoy bien" se cortaba; ahora caben las dos respuestas sin desplazar. |
| **Guía** | Arco, título, flecha, distancia, minutos, "hacia tu ruta…" | Línea final repetida | — | "Vuelve a tu ruta" · flecha + distancia · minutos | La línea final repetía el título. |
| **Cuenta regresiva / aviso enviado / Entendido** | — | — | — | Sin cambios | Ya tenían una sola idea. |
| **Tu semana** | Anillo, encabezado, "0 de 5", letras de días, frase resumen, 4 métricas con meta | Letras de los días (el anillo ya tiene un segmento por día), frase larga | Las 4 métricas pasan a una pantalla "Métricas" | "Tu semana" · "3 de 5 días" · "6 recorridos · 1 rutina" · Ver métricas | De 12+ líneas a 4 elementos. |
| **Ajustes** | 7 filas (incluía "Tu semana") | "Tu semana" (ya está en Mis recorridos: estaba en dos sitios) | — | Lista de ajustes + "Mapa real" + "Acerca de" (Paso 2) | Sin entradas duplicadas. |
| **Privacidad** | 2 frases, botón de mantener presionado | — | — | Textos corregidos para decir la verdad sobre el mapa (Paso 2) | Honestidad sobre lo que sale del reloj. |
| **Contactos, Destinos, Mis recorridos, Demostración** | Listas | — | — | Sin cambios de estructura | Son listas: una fila por elemento ya es una idea por fila. |

## Decisiones

- **El arco de probabilidad sale de Monitoreo, no de las alertas.** En la alerta el arco perimetral es el único gráfico junto al icono y comunica la urgencia; en Monitoreo competía con el mapa (tres gráficos a la vez).
- **"Terminar recorrido" pasa a Monitoreo.** Es una acción rara; en Inicio desplazaba "Ver detalles" fuera de la zona segura.
- **Las métricas pasan a un segundo nivel** (`LostDestination.Metrics`): la navegación sigue sin superar dos niveles bajo las secciones (Mis recorridos → Tu semana → Métricas es el tercer toque, pero sigue siendo el nivel 2 del flujo local de "Tu semana"). Ver `docs/ia/arquitectura-informacion.md`.
- **Estado compacto**: en Inicio y Monitoreo el estado es un solo `LostChip` (color + icono + texto); la vibración la sigue dando `Tone`.
