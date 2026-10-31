"""Genera los GPX de la demo de LOST sobre calles reales.

Todo se arma alrededor de UNA constante, CENTER (hoy: Universidad Nacional de Colombia, Bogotá).
Para mover la demo a otro campus o barrio basta con cambiar CENTER y volver a ejecutar:

    python tools/generate_osm_routes.py

Las rutas siguen calles reales gracias a OSRM con perfil a pie (routing.openstreetmap.de) y el
hospital del escenario "lugar nuevo" se busca con Nominatim cerca de CENTER. Se respeta la política
de uso de OpenStreetMap: User-Agent propio, pocas peticiones y una pausa entre ellas.
Igual que antes, se camina a 1.4 m/s y se guarda un punto cada 5 segundos.
"""
import json
import math
import os
import time
import urllib.parse
import urllib.request
from datetime import datetime, timedelta, timezone

CENTER = (4.6380, -74.0840)  # Universidad Nacional de Colombia, Bogotá (lat, lon)

SPEED_MPS = 1.4
SAMPLE_SECONDS = 5
EARTH_RADIUS = 6_371_000.0
START = datetime(2026, 10, 12, 11, 40, tzinfo=timezone.utc)  # 6:40 a. m. en Bogotá
USER_AGENT = 'LOST-smartwatch-demo-generator/1.0 (student project; github.com/ReveloSN/LOST-smartwatch)'
OSRM = 'https://routing.openstreetmap.de/routed-foot/route/v1/foot/'
NOMINATIM = 'https://nominatim.openstreetmap.org/search'
OUT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'sample-routes')


def offset(east_m, north_m, origin=CENTER):
    lat0, lon0 = origin
    lat = lat0 + math.degrees(north_m / EARTH_RADIUS)
    lon = lon0 + math.degrees(east_m / (EARTH_RADIUS * math.cos(math.radians(lat0))))
    return lat, lon


def get_json(url):
    request = urllib.request.Request(url, headers={'User-Agent': USER_AGENT})
    with urllib.request.urlopen(request, timeout=30) as response:
        data = json.load(response)
    time.sleep(1.1)  # política de uso: como mucho una petición por segundo
    return data


def osrm(*waypoints):
    """Ruta a pie por calles reales que pasa por todos los puntos, como lista de (lat, lon)."""
    coords = ';'.join(f'{lon:.6f},{lat:.6f}' for lat, lon in waypoints)
    data = get_json(f'{OSRM}{coords}?overview=full&geometries=geojson')
    if data.get('code') != 'Ok':
        raise RuntimeError(f'OSRM: {data.get("code")}')
    return [(lat, lon) for lon, lat in data['routes'][0]['geometry']['coordinates']]


def nearest_hospital():
    south, west = offset(-2500, -2500)
    north, east = offset(2500, 2500)
    query = urllib.parse.urlencode({
        'q': 'hospital', 'format': 'jsonv2', 'limit': 10, 'bounded': 1,
        'viewbox': f'{west},{north},{east},{south}',
    })
    results = get_json(f'{NOMINATIM}?{query}')
    if not results:
        return offset(-900, -900), 'Hospital'
    # Nominatim no siempre devuelve el mismo orden: se elige el más cercano a CENTER (determinista).
    best = min(results, key=lambda r: distance((float(r['lat']), float(r['lon'])), CENTER))
    return (float(best['lat']), float(best['lon'])), best.get('name') or 'Hospital'


def distance(a, b):
    lat = math.radians((a[0] + b[0]) / 2)
    dx = math.radians(b[1] - a[1]) * math.cos(lat) * EARTH_RADIUS
    dy = math.radians(b[0] - a[0]) * EARTH_RADIUS
    return math.hypot(dx, dy)


def point_at(path, fraction):
    """Punto de la ruta a cierta fracción de su largo."""
    total = sum(distance(a, b) for a, b in zip(path, path[1:]))
    walked = 0.0
    for a, b in zip(path, path[1:]):
        step = distance(a, b)
        if walked + step >= total * fraction:
            f = (total * fraction - walked) / step if step else 0
            return a[0] + (b[0] - a[0]) * f, a[1] + (b[1] - a[1]) * f
        walked += step
    return path[-1]


def split_at(path, fraction):
    """Parte la ruta en dos en cierta fracción de su largo (el punto de corte queda en ambas)."""
    total = sum(distance(a, b) for a, b in zip(path, path[1:]))
    walked = 0.0
    for i, (a, b) in enumerate(zip(path, path[1:])):
        step = distance(a, b)
        if walked + step >= total * fraction:
            cut = point_at(path, fraction)
            return path[:i + 1] + [cut], [cut] + path[i + 1:]
        walked += step
    return path, [path[-1]]


def walk(path):
    """Camina la ruta a 1.4 m/s y guarda un punto cada 5 s: [(lat, lon, segundos)]."""
    spacing = SPEED_MPS * SAMPLE_SECONDS
    points, t, carry = [(path[0][0], path[0][1], 0.0)], 0.0, 0.0
    for a, b in zip(path, path[1:]):
        step = distance(a, b)
        d = spacing - carry
        while d <= step:
            f = d / step
            t += SAMPLE_SECONDS
            points.append((a[0] + (b[0] - a[0]) * f, a[1] + (b[1] - a[1]) * f, t))
            d += spacing
        carry = step - (d - spacing)
    return points


def gpx(name, points):
    rows = '\n'.join(
        f'      <trkpt lat="{lat:.7f}" lon="{lon:.7f}"><time>'
        f'{(START + timedelta(seconds=s)).strftime("%Y-%m-%dT%H:%M:%SZ")}</time></trkpt>'
        for lat, lon, s in points
    )
    return (
        '<?xml version="1.0" encoding="UTF-8"?>\n'
        '<gpx version="1.1" creator="LOST" xmlns="http://www.topografix.com/GPX/1/1">\n'
        f'  <trk>\n    <name>{name}</name>\n    <trkseg>\n{rows}\n    </trkseg>\n  </trk>\n</gpx>\n'
    )


def block_loops(center, side_m, times):
    corners = [offset(-side_m, side_m, center), offset(side_m, side_m, center),
               offset(side_m, -side_m, center), offset(-side_m, -side_m, center)]
    return corners * times + [corners[0]]


def build_routes():
    home = offset(900, -650)
    university = CENTER
    normal = osrm(home, university)

    # Desvío voluntario: sale de la ruta y camina estable hacia una tienda, sin volver sobre sus pasos.
    branch = point_at(normal, 0.35)
    shop = offset(-500, -400, branch)
    voluntary = osrm(home, branch, shop)

    # Manzana real que nunca toca la ruta (siempre a más de 100 m): cada vuelta mide ~360 m.
    # Los tramos "en ruta" son cortes exactos de la ruta de siempre, para que volver a ella se note.
    before, after = split_at(normal, 0.4)
    closed_street = after[0]
    block = offset(140, 140, closed_street)
    lost = before + osrm(closed_street, *block_loops(block, 40, 3), closed_street)[1:] + after[1:]

    hospital, hospital_name = nearest_hospital()
    entrance = offset(0, -250, hospital)
    new_place = osrm(entrance, *block_loops(offset(0, -120, hospital), 35, 3), hospital)
    return {
        'demo_normal.gpx': ('Casa a la U (ruta de siempre)', normal),
        'demo_voluntary_detour.gpx': ('Desvío voluntario a una tienda', voluntary),
        'demo_disorientation.gpx': ('Calle cerrada, vueltas y regreso a la ruta', lost),
        'demo_new_place.gpx': (f'{hospital_name}: vueltas y llegada al consultorio', new_place),
    }


if __name__ == '__main__':
    for file_name, (title, path) in build_routes().items():
        points = walk(path)
        with open(os.path.join(OUT_DIR, file_name), 'w', encoding='utf-8', newline='\n') as f:
            f.write(gpx(title, points))
        print(f'{file_name}: {len(points)} puntos, {points[-1][2] / 60:.1f} min · fin {points[-1][0]:.5f},{points[-1][1]:.5f}')
