# Personalidad de la marca

> PDF "Comunicación con los usuarios". *Un guía sereno en tu muñeca. Conoce tus caminos y nota cuándo el de hoy no se parece al de siempre.*

## Atributos

| Atributo | Qué significa | Cómo se ve en la interfaz | Cómo se siente (háptica / movimiento) | Dónde está |
|---|---|---|---|---|
| **Calmado** | Nunca alarma; la urgencia se comunica con claridad, no con miedo. | Negro OLED, acento aqua, coral en vez de rojo puro, la alerta es una tarjeta tintada (12 %) y no una pantalla roja. | Anillo que "respira" (4 s) en calma; transiciones con fundido de 600 ms. | `BreathingStatusRing`, `SemanticTokens.Motion`, `AlertCard` |
| **Claro** | Una idea por pantalla, lenguaje humano, sin porcentajes ni códigos. | Pregunta grande, máximo dos botones apilados, "qué pasó · por qué · qué hacer". | Cada nivel tiene su vibración distinta. | `AlertTemplate`, `UiMessage`, `Tone` |
| **Protector** | Se adelanta: ayuda antes de que la pidan y explica por qué. | "¿Por qué te pregunto?", guía al punto más cercano, aviso a Laura con cuenta cancelable. | Doble vibración que se repite a los 15 s; insistente en la cuenta regresiva. | `ExplanationScreen`, `GuidingAlert`, `AssistanceTimers` |
| **Discreto** | No molesta si el desvío es voluntario; los datos se quedan en el reloj. | "¿Vas a otro lado hoy?" una sola vez; "Tus recorridos se quedan en tu reloj". | Una sola vibración suave; si no responde, no insiste. | `SoftAskAlert`, `VoluntaryDetourDetector`, `PrivacyScreen` |

## Conexión emocional

- **Micro-feedback** al confirmar: check con rebote (*spring*) y "Entendido. LOST aprenderá de esto." (`AcknowledgedAlert`).
- **Alivio** al recuperar la ruta: el anillo vuelve a aqua y respira, "Ya estás en tu ruta" (`BackOnRouteAlert`).
- **Control**: el usuario confirma, renombra o borra lo aprendido; puede quitar el permiso a su contacto.

## Iconografía

Material Symbols redondeados, peso ligero, **siempre con etiqueta en las acciones críticas** (los botones llevan texto; los iconos decorativos tienen `contentDescription = null` porque el texto ya los describe).

## Háptica que comunica estado

| Patrón | Onda | Cuándo |
|---|---|---|
| `GENTLE` | 1 × 40 ms | Pregunta suave, confirmación, aviso enviado, ruta recuperada |
| `DOUBLE` | 2 × 80 ms, pausa 120 | "¿Necesitas ayuda?" (se repite a los 15 s) |
| `INSISTENT` | 3 × 150 ms | Nivel alto y cuenta regresiva |
| `DIRECTION` | largo + corto | Empieza la guía |
