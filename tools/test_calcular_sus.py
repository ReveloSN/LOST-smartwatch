"""Pruebas de calcular_sus.py. Se ejecutan con:

    python -m unittest discover -s tools -p "test_*.py"
"""
import io
import os
import tempfile
import unittest

import calcular_sus as sus


def fila(participante, respuestas, perfil='Sebastián', escenario='Calle cerrada'):
    return {'participante': participante, 'perfil': perfil, 'escenario': escenario,
            **{f'p{i}': str(r) for i, r in enumerate(respuestas, start=1)}}


class PuntajeTest(unittest.TestCase):
    def test_la_mejor_respuesta_posible_da_100(self):
        self.assertEqual(sus.puntaje([5, 1, 5, 1, 5, 1, 5, 1, 5, 1]), 100.0)

    def test_la_peor_respuesta_posible_da_0(self):
        self.assertEqual(sus.puntaje([1, 5, 1, 5, 1, 5, 1, 5, 1, 5]), 0.0)

    def test_responder_todo_neutral_da_50(self):
        self.assertEqual(sus.puntaje([3] * 10), 50.0)

    def test_las_impares_suman_respuesta_menos_uno_y_las_pares_cinco_menos_respuesta(self):
        # Impares: 4,4,4,4,4 → 3·5 = 15. Pares: 2,2,2,2,2 → 3·5 = 15. (15 + 15) · 2,5 = 75.
        self.assertEqual(sus.puntaje([4, 2, 4, 2, 4, 2, 4, 2, 4, 2]), 75.0)

    def test_rechaza_respuestas_fuera_de_1_a_5(self):
        with self.assertRaises(ValueError):
            sus.puntaje([6, 1, 5, 1, 5, 1, 5, 1, 5, 1])

    def test_rechaza_si_no_son_10_respuestas(self):
        with self.assertRaises(ValueError):
            sus.puntaje([5, 1, 5])


class NotaTest(unittest.TestCase):
    def test_la_meta_de_lost_80_es_una_a_menos(self):
        self.assertEqual(sus.nota(80.0), 'A−')

    def test_el_promedio_de_la_industria_68_es_una_c(self):
        self.assertEqual(sus.nota(68.0), 'C')

    def test_debajo_de_51_7_es_f(self):
        self.assertEqual(sus.nota(40.0), 'F')


class ResumenTest(unittest.TestCase):
    def test_promedio_y_cumplimiento_de_la_meta(self):
        filas = [fila('P01', [5, 1, 5, 1, 5, 1, 5, 1, 5, 1]), fila('P02', [4, 2, 4, 2, 4, 2, 4, 2, 4, 2])]
        resumen = sus.resumir(sus.evaluar(filas))
        self.assertEqual(resumen.promedio, 87.5)
        self.assertTrue(resumen.cumple_meta)

    def test_con_una_sola_persona_no_hay_intervalo_de_confianza(self):
        resumen = sus.resumir(sus.evaluar([fila('P01', [3] * 10)]))
        self.assertIsNone(resumen.intervalo)

    def test_el_intervalo_de_confianza_usa_t_de_student(self):
        filas = [fila(f'P0{i}', r) for i, r in enumerate([[3] * 10, [4, 2] * 5, [5, 1] * 5], start=1)]
        resumen = sus.resumir(sus.evaluar(filas))
        # Puntajes 50, 75, 100: media 75, desviación 25, t(2 gl, 95 %) = 4,303 → ± 62,1.
        bajo, alto = resumen.intervalo
        self.assertAlmostEqual(alto - resumen.promedio, 4.303 * 25 / 3 ** 0.5, places=1)
        self.assertAlmostEqual(bajo, 75 - 62.1, places=0)

    def test_ignora_las_filas_sin_respuestas(self):
        filas = [fila('P01', [3] * 10), {'participante': 'P02', 'perfil': '', 'escenario': '',
                                         **{f'p{i}': '' for i in range(1, 11)}}]
        self.assertEqual(len(sus.evaluar(filas)), 1)

    def test_una_fila_a_medias_dice_cual_participante_y_cual_pregunta(self):
        incompleta = fila('P07', [3] * 10)
        incompleta['p4'] = ''
        with self.assertRaisesRegex(ValueError, 'P07.*p4'):
            sus.evaluar([incompleta])


class CsvTest(unittest.TestCase):
    def test_lee_la_plantilla_vacia_sin_errores(self):
        plantilla = os.path.join(os.path.dirname(__file__), '..', 'docs', 'metricas', 'plantilla-sus.csv')
        self.assertEqual(sus.evaluar(sus.leer_csv(plantilla)), [])

    def test_con_la_plantilla_vacia_avisa_en_vez_de_fallar(self):
        plantilla = os.path.join(os.path.dirname(__file__), '..', 'docs', 'metricas', 'plantilla-sus.csv')
        salida = io.StringIO()
        self.assertEqual(sus.main([plantilla], salida), 1)
        self.assertIn('Todavía no hay respuestas', salida.getvalue())

    def test_informe_en_markdown_con_la_tabla_y_el_resumen(self):
        with tempfile.NamedTemporaryFile('w', suffix='.csv', delete=False, encoding='utf-8', newline='') as f:
            f.write('participante,perfil,escenario,p1,p2,p3,p4,p5,p6,p7,p8,p9,p10\n')
            f.write('P01,Don Jorge,Hospital,5,1,5,1,5,1,5,1,5,1\n')
            ruta = f.name
        salida = io.StringIO()
        sus.main([ruta], salida)
        os.remove(ruta)
        texto = salida.getvalue()
        self.assertIn('| P01 | Don Jorge | Hospital | 100,0 | A+ |', texto)
        self.assertIn('Promedio', texto)


if __name__ == '__main__':
    unittest.main()
