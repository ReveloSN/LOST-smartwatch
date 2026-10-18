# User journey map — Sebastián, Casa → U

> Un lunes con la avenida cerrada (lámina 02 de los mockups). Es exactamente la historia que reproduce el GPX `sample-routes/demo_disorientation.gpx` y que verifica `GpxDemoEndToEndTest`.

| Hora | Etapa | Qué hace | Piensa / siente | Punto de dolor | LOST · IA | Estado en el código |
|---|---|---|---|---|---|---|
| 6:40 | Sale de casa | Sale a la hora de siempre, audífonos puestos. | "Voy bien de tiempo." 🙂 | — | Reconoce "Casa → U". El anillo aqua respira. | `RoutineMatcher` → `RoutineDetectionMode`, `Calm` |
| 6:48 | Toma la avenida | Gira en la esquina, como cada día. | "Lo de siempre." 🙂 | — | Secuencia igual: casa → esquina → avenida. | DTW open-end ≈ 0, `Calm` |
| 6:55 | Calle cerrada | Una obra cierra la avenida; toma una calle que no conoce. | "Por aquí también se llega… creo." 😐 | La ruta de siempre ya no sirve. | Las dos líneas se separan. Camina recto: nivel bajo, solo observa / pregunta suave. | `OffRouteSignal` alto, confusión baja → `SoftAsk` |
| 7:02 | Se desorienta | Pasa 3 veces por la misma cuadra, cambia de rumbo, se aleja. | "Voy tarde y el celular está en la maleta." 😟 | Manos ocupadas; sacar el celular cuesta tiempo. | Misma cuadra ×3, cambios de rumbo, se aleja → nivel medio. | `LoopSignal`, `ErraticHeadingSignal`, `MovingAwaySignal` → `AskForHelp` |
| 7:04 | **La IA actúa** | Siente dos vibraciones suaves y toca "Sí, guíame". | "Sí, me perdí." 😟 | Riesgo: molestarlo si se desvió a propósito. | "Te saliste de tu ruta de siempre. ¿Necesitas ayuda?" Decide en 2 s. | `HapticPattern.DOUBLE`, `USER_RESPONDED` |
| 7:04 | Lo guía de vuelta | Sigue una flecha grande hasta la ruta conocida. | "Ah, ya sé dónde estoy." 🙂 | — | "Volver a tu ruta de siempre" · punto más cercano. | `Guiding`, `NearestRoutePointGuide` |
| 7:05 | Recupera su ruta | El anillo vuelve a aqua. Sigue sin sacar el celular. | "Listo, de nuevo en mi ruta." 😀 | — | Ruta recuperada; nivel baja a bajo. | `RouteRecovered` → `Calm`, `RecoveryGrace`, `ROUTE_RECOVERED` |
| 7:24 | Llega a la U | Entra por la portería norte. | "Llegué con tiempo." 😀 | — | Aprende el desvío. | `DestinationReached`, `LearnRoutineFromTripUseCase` |
