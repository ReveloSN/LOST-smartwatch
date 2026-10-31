# Cuestionario SUS (System Usability Scale) para LOST

Se aplica al terminar cada sesión de prueba con usuarios (ver `metricas.md`). Meta: **SUS ≥ 80**.
Paso a paso de la sesión en [`guia-aplicacion-sus.md`](guia-aplicacion-sus.md).

## Instrucciones para la persona participante

> Responde cada afirmación pensando en el reloj que acabas de usar.
> 1 = Totalmente en desacuerdo · 5 = Totalmente de acuerdo. Marca solo una opción por fila.

| # | Afirmación | 1 | 2 | 3 | 4 | 5 |
|---|---|---|---|---|---|---|
| 1 | Creo que me gustaría usar LOST con frecuencia. | ☐ | ☐ | ☐ | ☐ | ☐ |
| 2 | Encontré LOST innecesariamente complejo. | ☐ | ☐ | ☐ | ☐ | ☐ |
| 3 | Pensé que LOST era fácil de usar. | ☐ | ☐ | ☐ | ☐ | ☐ |
| 4 | Creo que necesitaría ayuda de una persona experta para usar LOST. | ☐ | ☐ | ☐ | ☐ | ☐ |
| 5 | Las funciones de LOST (preguntar, guiar, avisar) están bien integradas. | ☐ | ☐ | ☐ | ☐ | ☐ |
| 6 | Pensé que había demasiada inconsistencia en LOST. | ☐ | ☐ | ☐ | ☐ | ☐ |
| 7 | Imagino que la mayoría de las personas aprendería a usar LOST muy rápido. | ☐ | ☐ | ☐ | ☐ | ☐ |
| 8 | Encontré LOST muy incómodo de usar. | ☐ | ☐ | ☐ | ☐ | ☐ |
| 9 | Me sentí muy seguro/a usando LOST. | ☐ | ☐ | ☐ | ☐ | ☐ |
| 10 | Necesité aprender muchas cosas antes de poder usar LOST. | ☐ | ☐ | ☐ | ☐ | ☐ |

## Cálculo del puntaje

1. Afirmaciones **impares** (1, 3, 5, 7, 9): aporte = respuesta − 1.
2. Afirmaciones **pares** (2, 4, 6, 8, 10): aporte = 5 − respuesta.
3. Sumar los 10 aportes (0 a 40) y multiplicar por **2.5** → puntaje de 0 a 100.

Interpretación habitual: < 68 por debajo del promedio · 68–80 bueno · **> 80 excelente (meta de LOST)**.

## Registro de respuestas

Las respuestas se pasan a [`plantilla-sus.csv`](plantilla-sus.csv) (una fila por persona, `p1`–`p10` del 1 al 5
tal como se marcaron) y el puntaje lo calcula `tools/calcular_sus.py`:

```bash
python tools/calcular_sus.py docs/metricas/resultados-sus.csv
```

El script invierte las pares, suma, multiplica por 2,5, asigna la nota de Sauro y Lewis y calcula promedio,
mediana, desviación e intervalo de confianza del 95 %. Está probado (`tools/test_calcular_sus.py`).

## Resultados

**Pendiente de aplicar.** Todavía no se han hecho sesiones con usuarios; no hay puntajes que reportar. Al aplicar
el cuestionario, pegar aquí el informe que genera el script:

| Participante | Perfil | Escenario | SUS | Nota |
|---|---|---|---|---|
| P01 | Sebastián (estudiante) | Calle cerrada | — | — |
| P02 | Don Jorge (adulto mayor) | Hospital | — | — |
| P03 | Andrés (viajero) | Destino por voz | — | — |
| P04 | | | — | — |
| P05 | | | — | — |

- Participantes: — · Promedio: — · Intervalo de confianza del 95 %: —
- Meta de LOST (SUS ≥ 80): —

## Preguntas abiertas (después del SUS)

1. ¿En qué momento sentiste que LOST te ayudó de verdad?
2. ¿Hubo algún momento en que LOST te molestó o preguntó sin necesidad?
3. ¿Entendiste por qué LOST te preguntó? ¿Cómo lo dirías tú?
4. ¿La vibración te avisó a tiempo? ¿Fue muy suave o muy fuerte?
