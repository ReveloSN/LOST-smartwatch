# Arquitectura de información

> PDF "Design Systems" → los 4 sistemas de la arquitectura de información. Plana a propósito: cinco secciones, nunca más de dos niveles. Las alertas no viven en el árbol: son una capa que aparece sobre cualquier pantalla.

## Diagrama

```mermaid
flowchart TD
    LOST((LOST)) --> HOME[Inicio]
    LOST --> ROUTINES[Mis recorridos]
    LOST --> DEST[Destinos]
    LOST --> CONTACTS[Contactos]
    LOST --> SETTINGS[Ajustes]

    HOME -- toca el estado --> MON[Monitoreo<br/>mapa: siempre vs. hoy]
    MON -- toca el estado --> WHY[¿Por qué te pregunto?]

    ROUTINES --> DETAIL[Detalle del recorrido<br/>guardar · renombrar · borrar]
    ROUTINES --> LEARN[Aprendiendo tu rutina 4/5]
    ROUTINES --> WEEK[Tu semana] --> METRICS[Métricas]

    DEST --> VOICE[Dictar por voz]
    DEST --> NEWPLACE[Lugar nuevo] --> GUIDE[Navegación activa] --> ARRIVAL[Llegada]

    CONTACTS --> CONTACT[Contacto autorizado<br/>quitar permiso]

    SETTINGS --> SENS[Sensibilidad]
    SETTINGS --> PRIV[Privacidad]
    SETTINGS --> DEMO[Recorridos de demostración]

    subgraph ALERTA [Capa de alerta · fuera del árbol]
        SOFT[¿Vas a otro lado hoy?] --> ASK[¿Necesitas ayuda?] --> GUIDING[Vuelve a tu ruta]
        ASK --> COUNT[Aviso a Laura en 5 s] --> SHARED[Laura ya sabe dónde estás]
        ASK --> ACK[Entendido. LOST aprenderá]
        GUIDING --> BACK[Ya estás en tu ruta]
    end
```

## Los 4 sistemas

| Sistema | Decisión | Dónde está en el código |
|---|---|---|
| **Organización** | 5 secciones globales: Inicio · Mis recorridos · Destinos · Contactos · Ajustes. Jerarquía plana (máximo 2 niveles). | `navigation/GlobalSection.kt`, `navigation/LostDestination.kt` |
| **Etiquetado** | Etiquetas cortas y humanas: "Inicio" (no "Dashboard"), "Mis recorridos" (no "Patrones"), "Volver" (no "Recalcular"), "Pedir ayuda" (no "SOS trigger"), "Sensibilidad" (no "Umbral de anomalía"). | `res/values*/strings.xml` (`nav_*`), `docs/contenido/glosario-voz.md` |
| **Navegación** | **Global**: pager horizontal entre secciones con indicador. **Local**: `SwipeDismissableNavHost` (deslizar a la derecha = volver). **Capa de alerta**: `AssistanceOverlay` sobre cualquier pantalla. | `navigation/SectionsPager.kt`, `navigation/LostNavHost.kt`, `feature/assistance/AssistanceOverlay.kt` |
| **Búsqueda** | Sin teclado: dictado por voz ("Llévame a…"), destinos recientes y sugeridos. | `feature/destinations/DestinationsScreen.kt`, `DestinationMatcher.kt` |

## Cambios del 25 de octubre (simplificación)

- **Revelación progresiva**: el chip de estado es la puerta al detalle (Inicio → Monitoreo → ¿Por qué te pregunto?).
- **Sin entradas duplicadas**: "Tu semana" vive solo en Mis recorridos; "Demostración" solo en Ajustes.
- **Métricas** pasan a un segundo nivel de Tu semana (`LostDestination.Metrics`).
- Detalle en `docs/diseno/simplificacion.md`.

## Por qué pager horizontal y no corona vertical

El mockup propone girar la corona o deslizar en vertical entre secciones. Pero las secciones son listas que ya usan la corona y el deslizamiento vertical para desplazarse; los dos gestos chocarían. Se usó un pager horizontal (también previsto en la sección 9.1 del plan) y la corona queda para desplazar dentro de cada sección.
