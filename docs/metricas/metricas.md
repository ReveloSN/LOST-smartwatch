# Métricas de éxito de LOST

> PDF "Investigación, definición y métricas". Las métricas están **instrumentadas en el código**:
> `MetricsTracker` (puerto del dominio) → `LocalMetricsTracker` (Room, en el reloj) → `InsightsCalculator` → pantalla **Tu semana**.

## Qué se mide y dónde

| Dimensión | Métrica | Eventos | Quién los registra | Meta |
|---|---|---|---|---|
| **Eficacia** | Tasa de éxito: vuelven a la ruta tras aceptar ayuda | `ASSISTANCE_STARTED` → `ROUTE_RECOVERED` | `AssistanceFeedbackLearner` ("Sí, guíame") y `AssistanceMetricsRecorder` | ≥ 90 % |
| **Eficiencia** | Tiempo de decisión: de la alerta a la respuesta | `ALERT_SHOWN` → `USER_RESPONDED` (con milisegundos) | `AssistanceMetricsRecorder` | < 5 s |
| **Eficiencia** | Tiempo de recuperación: de "guíame" a "Ya estás en tu ruta" | `ROUTE_RECOVERED` (con milisegundos) | `AssistanceMetricsRecorder` | < 30 s |
| **IA** | Falsos positivos: alertas respondidas con "Estoy bien" o "Voy a otro lado" | `ALERT_DISMISSED_IM_FINE` + `ALERT_DISMISSED_GOING_ELSEWHERE` / `ALERT_SHOWN` | `ApplyUserFeedbackUseCase` | < 10 % |
| **Uso** | Recorridos por semana y rutinas aprendidas | `TRIP_COMPLETED`, `ROUTINE_LEARNED` | `LearnRoutineFromTripUseCase` | — |
| **Uso** | Llegadas al destino y avisos al contacto | `DESTINATION_REACHED`, `LOCATION_SHARED` | `AssistanceMetricsRecorder`, `ShareLocationWithContactUseCase` | — |
| **Satisfacción** | SUS | Cuestionario (`sus.md`) | Pruebas con usuarios | ≥ 80 |

Todo se calcula sobre los **últimos 7 días** (`ObserveWeeklyInsightsUseCase`). Si no hay datos, la pantalla dice "Aún sin datos" en vez de inventar un 0 % o un 100 %.

## Privacidad

Los eventos se guardan solo en el reloj (Room). No hay analítica remota. "Borrar lo aprendido" elimina rutinas y recorridos.

## Protocolo de prueba con usuarios

1. **Participantes**: 5 personas, una por perfil de persona (Sebastián, Don Jorge, Andrés) más dos adicionales.
2. **Preparación**: reloj con la compilación *debug* (modo demo), Laura como contacto, rutina "Casa → U" aprendida.
3. **Tareas** (sin explicar la interfaz):
   - T1 · "Camina tu ruta de siempre." (escenario *Ruta de siempre*) → ¿LOST guarda silencio?
   - T2 · "Hoy vas a una tienda." (*Desvío voluntario*) → ¿responde la pregunta suave en < 5 s?
   - T3 · "La avenida está cerrada." (*Calle cerrada*) → ¿acepta la guía y recupera la ruta en < 30 s?
   - T4 · "Busca el consultorio 304." (*Lugar nuevo*) → ¿dicta el destino y llega?
4. **Registro**: la pantalla Tu semana muestra las métricas automáticas; quien observa anota errores y comentarios.
5. **Cierre**: cuestionario SUS y preguntas abiertas.

## Resultados

| Fecha | Participantes | Éxito | Decisión | Recuperación | Falsos positivos | SUS |
|---|---|---|---|---|---|---|
| (pendiente) | | | | | | |
