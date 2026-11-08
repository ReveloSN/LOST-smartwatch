"""Genera los GPX de la demo de LOST sobre calles reales.

La historia: la casa queda en Catambuco y la rutina es ir a la Universidad de Nariño (sede Torobajo,
Pasto) de lunes a viernes en la mañana. El trayecto real se hace así: 250 m a pie hasta la Panamericana,
bus hasta Torobajo y los últimos ~450 m a pie hasta el campus (≈ 35 min, saliendo a las 6:40).
Para mover la demo a otro barrio o campus basta con cambiar HOME y UNIVERSITY y volver a ejecutar:

    python tools/generate_osm_routes.py

Las rutas siguen calles reales gracias a OSRM (routing.openstreetmap.de: perfil en carro para el bus y a
pie para caminar) y el escenario "lugar nuevo" pasa en la Fundación Hospital San Pedro (HOSPITAL).
Se respeta la política de uso de OpenStreetMap: User-Agent propio, pocas peticiones y una pausa entre ellas.
Se guarda un punto cada 5 segundos; a pie se va a 1.4 m/s y en bus a 6.5 m/s (promedio con paradas).
"""
import json
import math
import os
import time
import urllib.request
from datetime import datetime, timedelta, timezone

HOME = (1.1663690, -77.2992956)  # Catambuco, Pasto (lat, lon)
UNIVERSITY = (1.2325340, -77.2933885)  # Universidad de Nariño, sede Torobajo
CENTER = UNIVERSITY  # alrededor de aquí se arman las historias que pasan a pie
HOSPITAL = (1.2246245, -77.2920710)  # Fundación Hospital San Pedro, a ~1 km de la universidad
HOSPITAL_NAME = 'Fundación Hospital San Pedro'

WALK_MPS = 1.4
BUS_MPS = 6.5
WALK_TO_STOP_M = 250  # de la casa al paradero de la Panamericana
WALK_FROM_STOP_M = 600  # el bus deja de andar 600 m (por la vía) antes del campus: ahí está el paradero
SAMPLE_SECONDS = 5
MIN_BLOCK_AREA_M2 = 1500
CLOSED_STREET_LAPS = 2  # la alerta llega en la primera vuelta; 2 bastan y acortan la demo
EARTH_RADIUS = 6_371_000.0
START = datetime(2026, 10, 12, 11, 40, tzinfo=timezone.utc)  # 6:40 a. m. en Colombia
USER_AGENT = 'LOST-smartwatch-demo-generator/1.0 (student project; github.com/ReveloSN/LOST-smartwatch)'
OSRM_FOOT = 'https://routing.openstreetmap.de/routed-foot/route/v1/foot/'
OSRM_CAR = 'https://routing.openstreetmap.de/routed-car/route/v1/driving/'
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


def osrm(*waypoints, base=OSRM_FOOT):
    """Ruta por calles reales que pasa por todos los puntos (a pie por defecto), como lista de (lat, lon)."""
    coords = ';'.join(f'{lon:.6f},{lat:.6f}' for lat, lon in waypoints)
    data = get_json(f'{base}{coords}?overview=full&geometries=geojson')
    if data.get('code') != 'Ok':
        raise RuntimeError(f'OSRM: {data.get("code")}')
    return [(lat, lon) for lon, lat in data['routes'][0]['geometry']['coordinates']]


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


def length(path):
    return sum(distance(a, b) for a, b in zip(path, path[1:]))


def travel(legs):
    """Recorre los tramos [(ruta, m/s)] seguidos y guarda un punto cada 5 s: [(lat, lon, segundos)]."""
    marks, t = [], 0.0  # (lat, lon, segundo en que se pasa por ese vértice)
    for path, speed in legs:
        for point in path:
            if marks:
                t += distance(marks[-1][:2], point) / speed
            marks.append((point[0], point[1], t))
    points, next_t = [], 0.0
    for (lat_a, lon_a, t_a), (lat_b, lon_b, t_b) in zip(marks, marks[1:]):
        while next_t <= t_b:
            f = (next_t - t_a) / (t_b - t_a) if t_b > t_a else 0.0
            points.append((lat_a + (lat_b - lat_a) * f, lon_a + (lon_b - lon_a) * f, next_t))
            next_t += SAMPLE_SECONDS
    if points[-1][2] < marks[-1][2]:
        points.append((marks[-1][0], marks[-1][1], next_t))  # la llegada exacta, en el siguiente muestreo
    return points


def min_distance_to(path, others):
    return min(distance(p, q) for p in path for q in others)


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


def perpendicular_offsets(a, b, meters):
    """Los dos puntos a [meters] de [a], a cada lado de la dirección a → b."""
    east = distance((a[0], a[1]), (a[0], b[1])) * (1 if b[1] > a[1] else -1)
    north = distance((a[0], a[1]), (b[0], a[1])) * (1 if b[0] > a[0] else -1)
    norm = math.hypot(east, north) or 1.0
    return [offset(-north / norm * meters * side, east / norm * meters * side, a) for side in (1, -1)]


def area(path):
    """Área que encierra una vuelta (m²): una manzana real encierra algo; ir y volver por la misma calle, no."""
    origin = path[0]
    xy = [(distance(origin, (origin[0], p[1])) * (1 if p[1] > origin[1] else -1),
           distance(origin, (p[0], origin[1])) * (1 if p[0] > origin[0] else -1)) for p in path]
    return abs(sum(a[0] * b[1] - b[0] * a[1] for a, b in zip(xy, xy[1:] + xy[:1]))) / 2


def real_block(centers, accept=lambda loops: True, laps=3):
    """De varias manzanas candidatas, la de menor perímetro que de verdad es una manzana.

    Las calles de Pasto no forman una cuadrícula: un cuadrado de esquinas cualquiera puede terminar en una
    caminata larga o en ir y volver por la misma calle. Se pide un área mínima y se elige la vuelta más corta,
    que es como da vueltas alguien que se desorienta.
    """
    candidates = []
    for center in centers:
        for half_side in (30, 40):
            lap = osrm(*block_loops(center, half_side, 1))
            if area(lap) >= MIN_BLOCK_AREA_M2 and accept(lap):
                candidates.append((length(lap), center, half_side))
    if not candidates:
        raise RuntimeError('No encontré una manzana real cerca')
    _, center, half_side = min(candidates)
    return osrm(*block_loops(center, half_side, laps))


def closed_street_walk(normal, from_stop):
    """La caminata del paradero al campus con la calle cerrada: sale de la ruta, da 2 vueltas a una manzana
    real que nunca toca la ruta (siempre a más de 100 m), vuelve al mismo punto y sigue. De los lugares
    posibles para la calle cerrada se queda con el de la manzana más corta."""
    options = []
    for fraction in (0.1, 0.25, 0.5):
        before, after = split_at(from_stop, fraction)
        closed_street = after[0]
        toward = after[min(5, len(after) - 1)]
        centers = [p for meters in (120, 160, 200, 240, 280)
                   for p in perpendicular_offsets(closed_street, toward, meters)] + around(closed_street, 200)
        try:
            loops = real_block(centers, accept=lambda lap: min_distance_to(lap, normal) > 100, laps=CLOSED_STREET_LAPS)
        except RuntimeError:
            continue
        options.append((length(loops), before, after, loops))
    if not options:
        raise RuntimeError('No encontré una manzana real a más de 100 m de la ruta')
    _, before, after, loops = min(options, key=lambda option: option[0])
    closed_street = after[0]
    detour = osrm(closed_street, loops[0])[:-1] + loops + osrm(loops[-1], closed_street)[1:]
    return before + detour[1:] + after[1:]


def steady_walk_away(start, normal):
    """Una caminata de ~400 m que se aleja de la ruta sin zigzaguear ni volver a acercarse: así se ve un
    desvío voluntario. De varias direcciones se queda con la calle más recta que siempre se aleja."""
    options = []
    for degrees in range(0, 360, 30):
        for meters in (400, 500):
            east, north = meters * math.sin(math.radians(degrees)), meters * math.cos(math.radians(degrees))
            walk = osrm(start, offset(east, north, start))
            away = [min_distance_to([p], normal) for p in walk]
            steady = sum(1 for a, b in zip(away, away[1:]) if b >= a - 2) / max(1, len(away) - 1)
            straight = distance(walk[0], walk[-1]) / max(1.0, length(walk))
            if away[-1] >= 150:
                options.append((straight * steady, walk))
    return max(options, key=lambda option: option[0])[1]


def around(point, meters):
    return [offset(east, north, point) for east, north in
            [(0, -meters), (0, meters), (meters, 0), (-meters, 0),
             (meters * 0.7, -meters * 0.7), (-meters * 0.7, -meters * 0.7),
             (meters * 0.7, meters * 0.7), (-meters * 0.7, meters * 0.7)]]


def build_routes():
    road = osrm(HOME, UNIVERSITY, base=OSRM_CAR)
    first_stop = point_at(road, WALK_TO_STOP_M / length(road))
    _, rest = split_at(road, WALK_TO_STOP_M / length(road))
    bus, _ = split_at(rest, 1 - WALK_FROM_STOP_M / length(rest))
    last_stop = bus[-1]
    to_stop = osrm(HOME, first_stop)
    from_stop = osrm(last_stop, UNIVERSITY)
    normal = [(to_stop, WALK_MPS), (bus, BUS_MPS), (from_stop, WALK_MPS)]
    normal_path = to_stop + bus + from_stop

    # Desvío voluntario: se baja del bus y camina estable hacia una papelería, sin volver sobre sus pasos.
    voluntary = normal[:2] + [(steady_walk_away(last_stop, normal_path), WALK_MPS)]

    # Calle cerrada: al bajarse del bus no puede seguir por su calle, da 2 vueltas a una manzana y vuelve.
    lost = normal[:2] + [(closed_street_walk(normal_path, from_stop), WALK_MPS)]

    # Lugar nuevo: llega al hospital por primera vez, da 3 vueltas a una manzana vecina y llega al consultorio.
    entrance = offset(0, -250, HOSPITAL)
    loops = real_block(around(HOSPITAL, 140))
    new_place = [(osrm(entrance, loops[0])[:-1] + loops + osrm(loops[-1], HOSPITAL)[1:], WALK_MPS)]
    return {
        'demo_normal.gpx': ('Casa en Catambuco a la Universidad de Nariño (ruta de siempre)', normal),
        'demo_voluntary_detour.gpx': ('Se baja del bus y camina a una papelería', voluntary),
        'demo_disorientation.gpx': ('Calle cerrada cerca de la U: vueltas y regreso a la ruta', lost),
        'demo_new_place.gpx': (f'{HOSPITAL_NAME}: vueltas y llegada al consultorio', new_place),
    }


if __name__ == '__main__':
    for file_name, (title, path) in build_routes().items():
        points = travel(path)
        with open(os.path.join(OUT_DIR, file_name), 'w', encoding='utf-8', newline='\n') as f:
            f.write(gpx(title, points))
        print(f'{file_name}: {len(points)} puntos, {points[-1][2] / 60:.1f} min · fin {points[-1][0]:.5f},{points[-1][1]:.5f}')
