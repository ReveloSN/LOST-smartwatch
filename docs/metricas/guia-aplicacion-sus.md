# Guía para aplicar el SUS de LOST

Paso a paso para una sesión de prueba con usuarios: qué preparar, qué decir, qué anotar y cómo calcular el
puntaje. Complementa el protocolo de [`metricas.md`](metricas.md) y el cuestionario de [`sus.md`](sus.md).

## 1. Antes de la sesión

| Qué | Cómo |
|---|---|
| Participantes | 5 personas como mínimo (con 5 ya aparecen la mayoría de los problemas de uso): una por persona del proyecto — Sebastián (estudiante), Don Jorge (adulto mayor), Andrés (viajero) — y dos más. Nadie del equipo. |
| Reloj | Compilación *debug* instalada, Texto grande apagado salvo que la persona lo pida, idioma de la persona. |
| Datos de la demo | Laura como contacto autorizado y la rutina "Casa → U" aprendida (Ajustes → Demostración las usa). |
| Materiales | Este documento, el cuestionario de `sus.md` impreso o en un formulario, la plantilla [`plantilla-sus.csv`](plantilla-sus.csv) y un cronómetro. |
| Consentimiento | Explicar que se evalúa la app, no a la persona; que no se graba la ubicación real (la demo usa recorridos de ejemplo); y que puede parar cuando quiera. Pedir el sí antes de empezar. |

## 2. Durante la sesión (≈ 20 minutos)

1. **Contexto (1 min).** "LOST es un reloj que aprende tus recorridos y te ayuda si te desorientas. Te voy a pedir
   unas tareas; piensa en voz alta. No hay respuestas malas."
2. **Tareas (10–12 min).** Sin explicar la interfaz. Quien modera elige el escenario en Ajustes → Demostración y
   le devuelve el reloj a la persona:

   | Tarea | Escenario de la demo | Qué observar |
   |---|---|---|
   | T1 · "Camina tu ruta de siempre." | Ruta de siempre | LOST no debe interrumpir. |
   | T2 · "Hoy vas a una tienda." | Desvío voluntario | ¿Entiende "¿Vas a otro lado hoy?" y responde en < 5 s? |
   | T3 · "La avenida está cerrada." | Calle cerrada | ¿Acepta "Sí, guíame" y vuelve a la ruta en < 30 s? ¿Entiende la cuenta regresiva? |
   | T4 · "Busca el consultorio 304." | Lugar nuevo (hospital) | ¿Dicta el destino y sigue la flecha? |

3. **Qué anotar** en cada tarea: si la completó sin ayuda (sí/no), los segundos hasta decidir (desde la vibración
   hasta tocar una respuesta) y lo que dijo en voz alta. Los tiempos también quedan en Tu semana → Métricas.
4. **Cuestionario SUS (3 min).** Inmediatamente después de las tareas, antes de conversar, para no influir.
   Leer la instrucción tal cual y dejar que la persona marque sola. Las 10 afirmaciones alternan positivas y
   negativas: es a propósito, no corregirlas.
5. **Preguntas abiertas (3 min).** Las de `sus.md`.

## 3. Después de la sesión

1. Pasar las respuestas a una copia de la plantilla, por ejemplo `docs/metricas/resultados-sus.csv`: una fila por
   persona, columnas `p1` a `p10` con números del 1 al 5 tal como los marcó (sin invertir nada: el script lo hace).
2. Calcular:

   ```bash
   python tools/calcular_sus.py docs/metricas/resultados-sus.csv
   ```

   Escribe en Markdown la tabla por persona (puntaje y nota) y el resumen: promedio, mediana, desviación,
   intervalo de confianza del 95 % (t de Student, adecuada para pocas personas) y si se cumple la meta.
3. Pegar el informe en la sección **Resultados** de `sus.md` y llenar la fila de `metricas.md` con las métricas
   automáticas de la sesión.

Si una fila está a medias, el script dice qué persona y qué pregunta faltan: un SUS incompleto no se puntúa.

## 4. Cómo leer el resultado

| Puntaje | Nota (Sauro y Lewis) | Lectura |
|---|---|---|
| ≥ 84,1 | A+ | Excelente. |
| 80,8 – 84,0 | A | **Meta de LOST: SUS ≥ 80.** |
| 78,9 – 80,7 | A− | Muy bueno; casi en la meta. |
| 68 aprox. | C | Promedio de la industria. |
| < 51,7 | F | Hay problemas de uso serios. |

- Con 5 personas el intervalo de confianza es ancho: reportarlo junto al promedio. Si el intervalo cruza 80, la
  meta no está demostrada todavía, aunque el promedio la supere.
- El SUS dice *cuánto* cuesta usar LOST, no *por qué*. El porqué está en las notas de las tareas y en las
  preguntas abiertas: cruzar un puntaje bajo con la tarea donde la persona dudó.
- Las afirmaciones 4 y 10 forman la subescala de "aprendizaje"; si solo esas puntúan mal, el problema es la
  primera vez (onboarding), no el uso diario.

## 5. Herramienta

`tools/calcular_sus.py` usa solo la biblioteca estándar de Python. Sus pruebas:

```bash
python -m unittest discover -s tools -p "test_*.py"
```
