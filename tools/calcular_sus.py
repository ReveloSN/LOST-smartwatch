"""Calcula el SUS (System Usability Scale) de las pruebas con usuarios de LOST.

Lee un CSV con la plantilla de docs/metricas/plantilla-sus.csv (una fila por participante, respuestas
del 1 al 5 en p1..p10) y escribe un informe en Markdown listo para pegar en docs/metricas/sus.md:

    python tools/calcular_sus.py docs/metricas/resultados-sus.csv

Solo usa la biblioteca estándar de Python. Las filas sin ninguna respuesta (las vacías de la plantilla)
se ignoran; una fila a medias es un error, porque un SUS incompleto no se puede puntuar.
"""
import csv
import statistics
import sys
from dataclasses import dataclass

META_LOST = 80.0
PROMEDIO_INDUSTRIA = 68.0
PREGUNTAS = [f'p{i}' for i in range(1, 11)]

# Escala de notas de Sauro y Lewis (2016): puntaje mínimo de cada nota.
NOTAS = [(84.1, 'A+'), (80.8, 'A'), (78.9, 'A−'), (77.2, 'B+'), (74.1, 'B'), (72.6, 'B−'),
         (71.1, 'C+'), (65.0, 'C'), (62.7, 'C−'), (51.7, 'D'), (0.0, 'F')]

# t de Student de dos colas al 95 % según los grados de libertad (más allá de 30 se usa 1,96).
T_95 = {1: 12.706, 2: 4.303, 3: 3.182, 4: 2.776, 5: 2.571, 6: 2.447, 7: 2.365, 8: 2.306, 9: 2.262,
        10: 2.228, 11: 2.201, 12: 2.179, 13: 2.160, 14: 2.145, 15: 2.131, 16: 2.120, 17: 2.110,
        18: 2.101, 19: 2.093, 20: 2.086, 25: 2.060, 30: 2.042}


@dataclass
class Resultado:
    participante: str
    perfil: str
    escenario: str
    sus: float


@dataclass
class Resumen:
    cantidad: int
    promedio: float
    mediana: float
    desviacion: float | None
    intervalo: tuple[float, float] | None

    @property
    def cumple_meta(self) -> bool:
        return self.promedio >= META_LOST


def puntaje(respuestas: list[int]) -> float:
    """Impares aportan respuesta − 1; pares, 5 − respuesta. La suma (0 a 40) por 2,5 da 0 a 100."""
    if len(respuestas) != len(PREGUNTAS):
        raise ValueError(f'se esperaban 10 respuestas y llegaron {len(respuestas)}')
    if any(r not in range(1, 6) for r in respuestas):
        raise ValueError(f'las respuestas van de 1 a 5: {respuestas}')
    aportes = [(r - 1) if i % 2 == 0 else (5 - r) for i, r in enumerate(respuestas)]
    return sum(aportes) * 2.5


def nota(sus: float) -> str:
    return next(letra for minimo, letra in NOTAS if sus >= minimo)


def leer_csv(ruta: str) -> list[dict]:
    with open(ruta, encoding='utf-8-sig', newline='') as archivo:
        return list(csv.DictReader(archivo))


def evaluar(filas: list[dict]) -> list[Resultado]:
    resultados = []
    for fila in filas:
        valores = {p: (fila.get(p) or '').strip() for p in PREGUNTAS}
        if not any(valores.values()):
            continue
        faltan = [p for p, v in valores.items() if not v]
        if faltan:
            raise ValueError(f'{fila.get("participante")}: faltan las respuestas {", ".join(faltan)}')
        respuestas = [int(valores[p]) for p in PREGUNTAS]
        resultados.append(Resultado(fila.get('participante', ''), fila.get('perfil', ''),
                                    fila.get('escenario', ''), puntaje(respuestas)))
    return resultados


def t_95(grados: int) -> float:
    claves = [g for g in T_95 if g <= grados]
    return T_95[max(claves)] if grados <= 30 else 1.96


def resumir(resultados: list[Resultado]) -> Resumen:
    if not resultados:
        raise ValueError('no hay ninguna fila con respuestas')
    puntajes = [r.sus for r in resultados]
    n = len(puntajes)
    promedio = statistics.fmean(puntajes)
    if n < 2:
        return Resumen(n, promedio, statistics.median(puntajes), None, None)
    desviacion = statistics.stdev(puntajes)
    margen = t_95(n - 1) * desviacion / n ** 0.5
    return Resumen(n, promedio, statistics.median(puntajes), desviacion, (promedio - margen, promedio + margen))


def decimal(valor: float) -> str:
    return f'{valor:.1f}'.replace('.', ',')


def informe(resultados: list[Resultado], resumen: Resumen) -> str:
    lineas = ['| Participante | Perfil | Escenario | SUS | Nota |', '|---|---|---|---|---|']
    lineas += [f'| {r.participante} | {r.perfil} | {r.escenario} | {decimal(r.sus)} | {nota(r.sus)} |'
               for r in resultados]
    lineas += ['', f'- Participantes: **{resumen.cantidad}**',
               f'- Promedio: **{decimal(resumen.promedio)}** (nota {nota(resumen.promedio)}) · '
               f'mediana {decimal(resumen.mediana)}']
    if resumen.intervalo:
        bajo, alto = resumen.intervalo
        lineas.append(f'- Desviación estándar: {decimal(resumen.desviacion)} · '
                      f'intervalo de confianza del 95 %: {decimal(bajo)} a {decimal(alto)}')
    else:
        lineas.append('- Con una sola persona no hay intervalo de confianza.')
    estado = 'cumple' if resumen.cumple_meta else 'no cumple'
    lineas.append(f'- Meta de LOST (SUS ≥ {decimal(META_LOST)}): **{estado}**. '
                  f'Promedio de la industria: {decimal(PROMEDIO_INDUSTRIA)}.')
    return '\n'.join(lineas) + '\n'


def main(argumentos: list[str], salida=sys.stdout) -> int:
    if len(argumentos) != 1:
        print(__doc__, file=salida)
        return 2
    resultados = evaluar(leer_csv(argumentos[0]))
    if not resultados:
        print('Todavía no hay respuestas en el archivo: llena p1..p10 de al menos una persona.', file=salida)
        return 1
    salida.write(informe(resultados, resumir(resultados)))
    return 0


if __name__ == '__main__':
    sys.stdout.reconfigure(encoding='utf-8')
    sys.exit(main(sys.argv[1:]))
