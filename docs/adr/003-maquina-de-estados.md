# ADR 003 — Máquina de estados explícita para la escalada de ayuda

- **Estado**: aceptada
- **Fecha**: 2026-10-18

## Contexto

La ayuda escala: preguntar suave → preguntar → guiar → cuenta regresiva → avisar al contacto, y vuelve a la calma cuando el usuario responde o recupera su ruta. Si esta lógica se reparte entre pantallas y `if`s, aparecen estados imposibles (por ejemplo, avisar a Laura mientras el usuario ya dijo "Estoy bien").

## Decisión

- **Patrón State**: `AssistanceState` es un `sealed interface` (`Calm`, `SoftAsk`, `AskForHelp`, `Guiding`, `SharingCountdown`, `LocationShared`, `Acknowledged`, `Paused`).
- `AssistanceStateMachine.next(state, event)` es **pura**: sin efectos, sin tiempo, sin Android.
- **Cadena de responsabilidad** para decidir la acción ante una detección: `VibrateAndAskLink` → `AlternativeRouteLink` → `ShareLocationLink`.
- El tiempo vive fuera: `AssistanceTimers` (15 s, 30 s, 1 s, 3 s, 2 min) envía eventos.
- Los efectos observan las transiciones (`TripSession.transitions`): háptica (`AssistanceHapticPolicy`), métricas, aprendizaje y aviso al contacto.

```mermaid
stateDiagram-v2
    [*] --> Calm
    Calm --> SoftAsk: desvío voluntario
    Calm --> AskForHelp: nivel medio
    Calm --> SharingCountdown: nivel alto (con contacto)
    SoftAsk --> Acknowledged: "Voy a otro lado"
    SoftAsk --> Guiding: "Guíame"
    SoftAsk --> Calm: 30 s sin respuesta (no insiste)
    SoftAsk --> AskForHelp: empieza a dar vueltas
    AskForHelp --> Guiding: "Sí, guíame"
    AskForHelp --> Acknowledged: "Estoy bien"
    AskForHelp --> SharingCountdown: nivel alto o 30 s sin respuesta
    SharingCountdown --> SharingCountdown: cada segundo
    SharingCountdown --> LocationShared: llega a 0
    SharingCountdown --> Calm: "Cancelar"
    LocationShared --> Guiding: "Seguir guiándome"
    Guiding --> Calm: ruta recuperada
    Guiding --> Acknowledged: "Ya sé dónde estoy"
    Acknowledged --> Calm: 3 s ("Voy a otro lado")
    Acknowledged --> Paused: 3 s ("Estoy bien" / "Ya sé dónde estoy")
    Paused --> Calm: 2 min sin preguntar
```

## Consecuencias

- ✅ Cada transición tiene su prueba (`AssistanceStateMachineTest`) y los tiempos se prueban con tiempo virtual (`AssistanceTimersTest`).
- ✅ Los estados imposibles no se pueden representar.
- ✅ Agregar un estado obliga al compilador a revisar todos los `when`.
- ⚠️ Decisiones explícitas que antes quedaban implícitas: cancelar la cuenta vuelve a Calm; nivel alto sin contacto pregunta en vez de compartir; mientras guía, nuevas lecturas no interrumpen; tras "Estoy bien" o "Ya sé dónde estoy" hay 2 minutos sin preguntas (`Paused`, 31 de octubre), porque quien sigue buscando la salida volvía a recibir la alerta a los pocos segundos.
