# Cómo presentar LOST

> Guion para la sustentación de **Diseño de Interfaces de Software**. Lo que más se evalúa es el **diseño**
> (los 4 PDF del curso), así que la demo en vivo cuenta una historia y cada momento se conecta con un concepto
> del curso. La ingeniería (arquitectura, IA, pruebas) va después, corta y con evidencia.

**Duración pensada: 15 minutos + preguntas.** Si hay solo 10, ver "Versión de 10 minutos" al final.

**Diapositivas de apoyo** (13, con notas para quien presenta):
<https://claude.ai/artifact/DpYkgh9vkzEjSHZpUPY7Kx>. Se descargan como PowerPoint o PDF desde el menú del
documento. Son privadas hasta que se compartan desde ese mismo menú. Falta poner los nombres del equipo en la
portada.

La historia es la de **Sebastián**, que vive en **Catambuco** y estudia en la **Universidad de Nariño** (sede
Torobajo). Sale a las 6:40, camina hasta la Panamericana, toma el bus y camina los últimos metros hasta el
campus. Es la persona principal de `docs/investigacion/personas.md`.

---

## Antes de entrar (15 minutos antes)

1. **Encender el emulador en frío**, para que tome la hora real. Si arranca desde un *snapshot* viejo, la hora
   queda atrasada y el mapa no carga: el certificado de OpenStreetMap "todavía no es válido" para el reloj.

   ```bash
   emulator -avd Wear_OS_Large_Round -no-snapshot-load
   ```

2. **Dejarlo listo para la demo**: zona horaria de Colombia, pantalla siempre encendida, la app instalada desde
   cero en español.

   ```bash
   adb shell cmd alarm set-timezone America/Bogota
   adb shell svc power stayon true
   ./gradlew :app:installDebug
   adb shell pm clear com.lost.watch
   adb shell cmd locale set-app-locales com.lost.watch --locales es-CO
   ```

3. **Comprobar internet** abriendo una vez el detalle de una ruta: si se ven calles, el mapa real funciona. Sin
   internet la app sigue funcionando y dice "Te muestro tu ruta sin calles." (también es parte del diseño).
4. **Plan B abierto en otra pestaña**, por si el emulador falla:
   - Videos grabados en el emulador (`docs/presentacion/video/`): `1-persona-nueva.mp4` (bienvenida y la
     ruta aprendida día a día, 2:30), `2-calle-cerrada.mp4` (de Catambuco a la alerta y la guía, 2:55) y
     `3-hospital.mp4` (lugar nuevo, 1:50).
   - Las hojas `docs/capturas/presentacion/00-acto-1-persona-nueva.png` y `00-acto-2-rutina-aprendida.png`.
5. Proyectar el emulador con la ventana grande (zoom 2×) y el sonido apagado.

---

## Guion (15 minutos)

### 1. El problema (1 min) — PDF "Investigación, definición y métricas"

- "¿Cómo podríamos ayudar a una persona que se desorienta a recuperar su ruta **en menos de 30 segundos**, sin
  sacar el celular ni pedir ayuda?"
- Tres personas, tres formas de perderse: Sebastián (estudiante), Don Jorge (adulto mayor en un hospital) y
  Andrés (viajero). Mostrar la lámina de personas y el *journey map* (`docs/investigacion/`).
- La idea clave: LOST **no usa una distancia fija**. Aprende tu ruta y separa un **desvío voluntario** de una
  **desorientación**.

### 2. Acto 1 · Una persona nueva (3 min) — PDF "Comunicación con los usuarios" y "Contenido, voz e i18n"

En el reloj, recién instalado:

| Paso | Qué se ve | Qué decir |
|---|---|---|
| Bienvenida | "Hola, soy LOST" | Voz fija: cercana, tranquila, en "tú". Anillo que respira = calma. |
| Permiso | "¿Usamos tu ubicación?" | El porqué antes del diálogo del sistema; "Ahora no" también deja seguir. |
| Contacto | "¿A quién aviso si necesitas ayuda?" → **Elegir a Laura** | Una pregunta por pantalla; texto en el lenguaje del usuario. |
| Inicio | "LOST está atento · Camina y reconoceré tu recorrido." | Estado vacío que explica qué va a pasar, sin culpa. |
| Mis recorridos | "Aún no conocemos tus recorridos." | Arquitectura de información: 5 secciones, deslizar entre ellas, máximo 2 niveles. |

**Aprender la ruta a la vista** (Ajustes → Demostración → **Aprender un día de ruta**):

1. Tocar una vez: "1 de 5 días". Volver a **Mis recorridos**: aparece "Nuevo recorrido · lun · 6:40 a. m. ·
   35 min · ¿Lo confirmo?". Tocarlo: **"1 de 5 · 4 más y lo guardamos como tu ruta de siempre."**
2. Tocar cuatro veces más: "Ruta aprendida · 5 de 5 días". En **Mis recorridos**: **"Casa → U · L–V · 6:40 a.
   m. · 35 min"**. LOST dedujo solo los días, la hora y la duración.
3. Abrir **Casa → U**: el **mapa real de Pasto** con la ruta de Catambuco a la universidad.

Qué decir: "Esto es reconocimiento de patrones: cada día pasa por el mismo aprendizaje que usaría un recorrido
real (`LearnRoutineFromTripUseCase` → `RoutineLearner`). Al quinto día la rutina se confirma."

### 3. Acto 2 · Una semana después (5 min) — el corazón de la demo

Cada recorrido de **Ajustes → Demostración** abre el **monitoreo**: el mapa sigue a la persona (ventana de 1 km
centrada en ella), la ruta de siempre en gris y la de hoy en aqua. Lo que se camina va 10× más rápido y el bus
40×. Deslizar a la derecha lleva a **Inicio**: "Todo en orden", la hora de llegada en grande y "Llegada · Casa →
U"; tocar el estado vuelve al mapa (revelación progresiva).

| Recorrido | Cuándo pasa algo | Qué se ve | Concepto del curso |
|---|---|---|---|
| **Desvío voluntario** | ~85 s | "¿Vas a otro lado hoy?" → **Sí, voy a otro lado** → "Entendido. LOST aprenderá de esto." → "Hoy tomaste otra calle" | Tono **relajado**: pregunta suave, una sola vez. Feedback que la IA usa para aprender. |
| **Calle cerrada** | ~75 s | Vueltas a la manzana → "Te saliste de tu ruta. ¿Necesitas ayuda?" (vibra) → **Sí, guíame** → flecha "Vuelve a tu ruta · 220 m · ≈ 3 min" | Tono **atento**: el estado nunca solo con color (color + icono + texto + vibración). Escalada con máquina de estados. |
| **Lugar nuevo (hospital)** | ~65 s | Mapa del barrio del Hospital San Pedro → "Pasaste 3 veces por la misma cuadra. ¿Necesitas ayuda?" → flecha al **Consultorio 304** | Modo lugar nuevo: sin rutina, busca señales generales (vueltas, alejarse). La explicación dice **por qué**. |

Consejos:

- Si la guía tarda, tocar **Ya sé dónde estoy**: también es una salida válida y muestra "Entendido".
- Opcional: en **Calle cerrada** no responder. A los 15 s vuelve a vibrar y a los 30 s empieza la cuenta
  regresiva "Aviso a Laura en 5…" con **Cancelar**: es el nivel **firme** del tono. Si no se cancela, "Laura
  ya sabe dónde estás" (en la demo nunca se envía un SMS real).
- Al final, **Mis recorridos → Tu semana** ("5 de 5 días") y **Dónde te desorientas**: aparecen los lugares de
  las alertas que se acaban de provocar.

### 4. El diseño detrás (3 min) — PDF "Design Systems"

Mostrar código y documentos, no diapositivas largas:

- **Tokens en 3 niveles**: `PrimitiveTokens` → `SemanticTokens` → `ComponentTokens`. Los componentes nunca usan
  colores literales.
- **Atomic Design**: `core/designsystem/components/` (átomos → moléculas → organismos → plantillas), con previews
  redondo y cuadrado.
- **Arquitectura de información**: `docs/ia/arquitectura-informacion.md` (organización, etiquetado, navegación
  global/local, búsqueda por voz).
- **Contenido**: `docs/contenido/glosario-voz.md` (sistema → usuario) y el mensaje de error de 3 partes. Ejemplo
  vivo: sin permiso de ubicación, Inicio dice qué pasó, por qué y qué hacer, con el botón **Permitir ubicación**.
- **Internacionalización**: español, inglés y portugués; pseudolocale en-XA sin cortes (`docs/capturas/pulido/`).
- **Accesibilidad**: contraste AA, áreas táctiles ≥ 48 dp, TalkBack lee las alertas, Texto grande +30 %.

### 5. La ingeniería, en una lámina (2 min)

- **Clean Architecture** en 5 módulos; `domain` y `ai` son Kotlin puro (sin Android).
- **SOLID y patrones** visibles: Strategy (DTW/Fréchet, modos), Composite (señales), State (asistencia),
  Facade (`LostDetectionEngine`), Repository, Adapter, Observer.
- **IA**: remuestreo cada 20 m, **DTW** con banda contra la ruta de siempre, 7 señales, regresión logística y
  el término desvío × confusión que separa desvío voluntario de desorientación (`docs/ia/motor-ia.md`).
- **Calidad**: cerca de 500 pruebas automáticas + 23 en el reloj, ktlint, detekt y Lint sin alertas.
  Robustez: 205 casos de ruido de GPS.
- **Rendimiento**: con la ruta real de 11 km el motor recalculaba el DTW entero en cada punto; ahora retoma sus
  filas y seguir la ruta completa bajó de **5,2 s a 0,5 s** (≈ 10×).

### 6. Métricas y cierre (1 min)

- Metas: éxito ≥ 90 %, decisión < 5 s, recuperación < 30 s, falsos positivos < 10 %, SUS ≥ 80
  (`docs/metricas/metricas.md`). Están **instrumentadas en el código** y se ven en **Tu semana → Ver métricas**.
- Qué sigue: aplicar el SUS con 5 personas y calibrar la IA con recorridos reales en un reloj físico.

---

## Preguntas probables

| Pregunta | Respuesta corta |
|---|---|
| ¿Por qué no una distancia fija a la ruta? | Porque un desvío de 200 m puede ser voluntario. LOST combina **qué tan lejos** está con **cómo se mueve** (vueltas, cambios de rumbo, alejarse del destino). ADR 002. |
| ¿Qué pasa si el GPS es malo? | Se descartan los puntos imprecisos, se promedian los demás por su precisión y, si se pierde la señal un minuto, LOST lo dice. 205 casos de ruido en pruebas. |
| ¿Y la privacidad? | Todo se queda en el reloj (Room). Al mapa solo viaja la zona (z/x/y), nunca la ubicación exacta. El contacto solo ve la ubicación cuando LOST ayuda. ADR 005. |
| ¿Cómo aprende? | Cada recorrido terminado se compara con DTW contra las rutinas conocidas: si se parece, la refuerza (días, hora, duración con Welford); si no, empieza una nueva. Se confirma a los 5 días. |
| ¿Cómo aprende de mis respuestas? | "Estoy bien" sube el umbral personal (pregunta menos); aceptar ayuda lo baja. |
| ¿Y si voy en bus? | La rutina incluye el bus; las señales de confusión se miden sobre todo al caminar, que es donde uno se desorienta. |
| ¿Batería? | GPS adaptativo según el estado y un motor que procesa solo lo nuevo de cada punto. |
| ¿Funciona sin internet? | Sí. El mapa real es lo único que usa la red; sin ella se ve la ruta sin calles. |

---

## Versión de 10 minutos

1. Problema (1 min).
2. Acto 1 resumido: bienvenida → Laura → Aprender un día de ruta ×5 → Casa → U con el mapa (2 min).
3. Acto 2: solo **Calle cerrada** completo y **Desvío voluntario** hasta la pregunta suave (4 min).
4. Diseño: tokens, Atomic Design, voz y tono, accesibilidad (2 min).
5. Ingeniería y métricas en una lámina (1 min).
