"""Genera los recorridos GPX de la demo de LOST.

Camina en metros sobre un plano local (x = este, y = norte) a 1.4 m/s y guarda un punto
cada 5 segundos, igual que los recorridos sintéticos de las pruebas del motor (TripFixtures).

Uso: python generate_routes.py   (escribe los .gpx en esta misma carpeta)
"""
import math
import os
from datetime import datetime, timedelta, timezone

SPEED_MPS = 1.4
SAMPLE_SECONDS = 5
EARTH_RADIUS = 6_371_000.0
CASA = (4.6097, -74.0817)
HOSPITAL = (4.6280, -74.0640)
START = datetime(2026, 10, 12, 11, 40, tzinfo=timezone.utc)  # 6:40 a. m. en Bogotá


class Walk:
    def __init__(self, origin):
        self.origin = origin
        self.x, self.y, self.t = 0.0, 0.0, 0.0
        self.points = [(0.0, 0.0, 0.0)]

    def to(self, x, y):
        distance = math.hypot(x - self.x, y - self.y)
        steps = max(1, math.ceil(distance / (SPEED_MPS * SAMPLE_SECONDS)))
        sx, sy = self.x, self.y
        for i in range(1, steps + 1):
            f = i / steps
            self.x, self.y = sx + (x - sx) * f, sy + (y - sy) * f
            self.t += distance / steps / SPEED_MPS
            self.points.append((self.x, self.y, self.t))
        return self

    def square(self, side, times):
        x, y = self.x, self.y
        for _ in range(times):
            self.to(x + side, y).to(x + side, y - side).to(x, y - side).to(x, y)
        return self

    def gpx(self, name):
        meters_per_degree = math.radians(1) * EARTH_RADIUS
        lat0, lon0 = self.origin
        scale = math.cos(math.radians(lat0))
        rows = []
        for x, y, t in self.points:
            lat = lat0 + y / meters_per_degree
            lon = lon0 + x / (meters_per_degree * scale)
            time = (START + timedelta(seconds=t)).strftime('%Y-%m-%dT%H:%M:%SZ')
            rows.append(f'      <trkpt lat="{lat:.7f}" lon="{lon:.7f}"><time>{time}</time></trkpt>')
        body = '\n'.join(rows)
        return (
            '<?xml version="1.0" encoding="UTF-8"?>\n'
            '<gpx version="1.1" creator="LOST" xmlns="http://www.topografix.com/GPX/1/1">\n'
            f'  <trk>\n    <name>{name}</name>\n    <trkseg>\n{body}\n    </trkseg>\n  </trk>\n</gpx>\n'
        )


ROUTES = {
    'demo_normal.gpx': ('Casa a la U (ruta de siempre)', Walk(CASA).to(600, 0).to(600, 500)),
    'demo_voluntary_detour.gpx': ('Desvío voluntario a una tienda', Walk(CASA).to(300, 0).to(300, -400)),
    'demo_disorientation.gpx': (
        'Calle cerrada, vueltas y regreso a la ruta',
        Walk(CASA).to(300, 0).to(300, -150).square(60, 3).to(330, -120).to(280, -170).to(300, -300)
        .to(300, 0).to(600, 0).to(600, 500),
    ),
    'demo_new_place.gpx': (
        'Hospital: vueltas cerca del ascensor',
        Walk(HOSPITAL).to(0, 60).to(40, 60).to(40, 20).to(0, 20).to(0, 60).to(40, 60).to(40, 20).to(0, 20)
        .to(0, 60).to(40, 60).to(40, 20).to(0, 20).to(0, 0).to(0, 300),
    ),
}

if __name__ == '__main__':
    here = os.path.dirname(os.path.abspath(__file__))
    for file_name, (title, walk) in ROUTES.items():
        with open(os.path.join(here, file_name), 'w', encoding='utf-8', newline='\n') as f:
            f.write(walk.gpx(title))
        print(f'{file_name}: {len(walk.points)} puntos, {walk.t / 60:.1f} min')
