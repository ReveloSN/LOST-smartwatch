# Tipografía: Manrope

Los mockups (`docs/design/`) usan **Manrope**. Hasta el 27 de octubre la app usaba la sans del sistema;
ahora la fuente va empaquetada en `core/designsystem/src/main/res/font/` y `LostTypography.brandFamily`
la usa en toda la escala (display, número, pregunta, título, cuerpo, apoyo y botón).

## Licencia

Manrope © 2018 The Manrope Project Authors, bajo **SIL Open Font License 1.1**. El texto completo está en
[`docs/licencias/manrope-OFL.txt`](../licencias/manrope-OFL.txt). La OFL permite empaquetarla en una app;
los archivos derivados (pesos fijos y subconjunto) no usan el nombre reservado de otra forma que la original.

## Cómo se empaquetó

- Origen: la fuente variable `Manrope[wght].ttf` del repositorio oficial de Google Fonts.
- Solo los **4 pesos** que usa la escala: 400 (cuerpo), 500 (apoyo), 600 (pregunta, título) y 700 (display,
  número, botón). Se generaron como archivos fijos con `fontTools` (`instancer`), porque así cada peso es un
  `Font(R.font…, FontWeight…)` normal de Compose.
- Solo los **glifos latinos** (U+0020–U+024F, puntuación, flechas y monedas): cubren es, en y pt. Si aparece
  un carácter que no está (por ejemplo, en la pseudolocalización `en-XA`), Android usa la fuente del sistema
  solo para ese carácter.

## Tamaño del APK

| Build | Antes (sans del sistema) | Después (Manrope) |
|---|---|---|
| Release con R8 | 3 600 802 bytes (3,60 MB) | 3 697 974 bytes (3,70 MB) — **+97 KB (+2,7 %)** |

Los 4 archivos ocupan 216 KB sin comprimir; el APK los comprime.

## Textos cortados que aparecieron (y cómo se corrigieron)

Manrope es un poco más ancha que Roboto. Se recorrieron las 5 secciones en es, en y pt, con el tamaño normal y
con **Texto grande** (+30 %):

| Dónde | Qué pasaba | Corrección |
|---|---|---|
| Contactos (pt) | "Vê sua localização só quando o LOST está te aju…" | Texto en pt alineado con el de es: "…só quando o LOST te ajuda." |
| Contactos (es, en, pt) con Texto grande | La nota del contacto terminaba en "…" | `LostTextRole.maxLinesAt(fontScale)`: con texto grande los roles de varias líneas ganan líneas en proporción (apoyo 2 → 3, cuerpo 3 → 4). Números y display siguen en una línea. |
| Inicio (pt) con Texto grande | "Caminhe e vou reconhecer seu per…" | La misma regla de líneas. |

Pruebas: `CaptionFitTest` mide los textos de apoyo con la propia Manrope (AWT) en la columna de 159 dp, con el
tamaño normal y con texto grande, en los tres idiomas. `LostTextRoleTest` fija la regla de líneas.
