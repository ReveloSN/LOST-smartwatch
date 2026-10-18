# ADR 002 — Comparar con la rutina (DTW) en lugar de una distancia fija

- **Estado**: aceptada
- **Fecha**: 2026-10-18

## Contexto

La forma habitual de detectar que alguien "se perdió" es una geocerca: alertar si se aleja más de X metros de un punto. La lámina F de los mockups muestra por qué falla:

- Don Jorge da vueltas **dentro** del parque durante 50 min → la geocerca de 500 m no dice nada.
- Sebastián está a 5 km de casa camino a la U, a su hora → la geocerca da una falsa alarma.

## Opciones

1. **Geocerca / distancia fija** — simple, pero ignora el *orden*, el *tiempo* y el *patrón*.
2. **Distancia punto a punto con la ruta** — detecta salirse del camino, pero no los bucles ni el retraso, y castiga caminar a otro ritmo.
3. **Comparar secuencias (DTW) + señales de confusión + regresión logística** — compara "¿esto se parece a lo que hace siempre?".

## Decisión

Opción 3:

- Remuestrear cada 20 m para comparar por distancia recorrida.
- **DTW** con banda de Sakoe-Chiba y versión *open-end* para recorridos en curso (tolera cambios de ritmo). Fréchet queda disponible con el mismo contrato.
- Señales independientes de confusión (bucles con grilla espacial, giros con buffer circular, alejarse, paradas) y de retraso (Welford + sensibilidad).
- Regresión logística con un término de **interacción desvío·confusión** para no molestar en desvíos voluntarios.
- Modo lugar nuevo cuando no hay rutina.

## Consecuencias

- ✅ Los dos casos de la lámina F se resuelven bien (pruebas `DetectionScenariosTest`).
- ✅ Explicable: cada señal produce una razón humana.
- ✅ Personalizable: umbrales que aprenden de "Estoy bien".
- ⚠️ Necesita aprender la rutina primero (5 repeticiones); mientras tanto funciona el modo lugar nuevo.
- ⚠️ Los pesos se calibraron con recorridos sintéticos; hay que ajustarlos con datos reales.
- ⚠️ Costo O(n·banda) por evaluación; con puntos cada 20 m es despreciable para recorridos de una hora.
