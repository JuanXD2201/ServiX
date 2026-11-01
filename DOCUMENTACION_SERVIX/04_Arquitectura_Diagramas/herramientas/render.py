"""Renderizador propio de .drawio (mxGraph XML) -> PNG 2x, SVG, PDF con matplotlib.
Soporta: rect, rounded, ellipse, rhombus, cylinder3, umlActor, swimlane, text, line,
aristas poligonales con waypoints, flechas (block/open/classic/diamond), dashed, etiquetas."""
import html as _html
import math
import re
import sys
import xml.etree.ElementTree as ET

import matplotlib
matplotlib.use('Agg')
from matplotlib.figure import Figure
from matplotlib.backends.backend_agg import FigureCanvasAgg
from matplotlib.patches import Rectangle, FancyBboxPatch, Ellipse, Polygon, PathPatch
from matplotlib.path import Path
from matplotlib import rcParams

rcParams['font.family'] = ['Arial', 'Calibri', 'DejaVu Sans']
rcParams['svg.fonttype'] = 'path'
rcParams['pdf.fonttype'] = 42
rcParams['lines.scale_dashes'] = False
PT = 0.75  # px -> pt a 96 dpi


def parse_style(s):
    d = {}
    for part in (s or '').split(';'):
        if not part:
            continue
        if '=' in part:
            k, v = part.split('=', 1)
            d[k] = v
        else:
            d.setdefault('_shape', part)
    return d


def col(v, default=None):
    if v is None:
        return default
    if v in ('none', ''):
        return None
    return v


def num(st, k, d):
    try:
        return float(st.get(k, d))
    except ValueError:
        return d


def dashes(st):
    if st.get('dashed') != '1':
        return '-'
    pat = [float(p) for p in st.get('dashPattern', '6 4').split()]
    if len(pat) == 1:
        pat *= 2
    return (0, tuple(p * PT for p in pat))


def clean(v):
    v = v or ''
    if '<' in v:
        v = re.sub(r'<br\s*/?>', '\n', v)
        v = re.sub(r'<[^>]+>', '', v)
    return _html.unescape(v)


class Cell:
    pass


def load(path):
    root = ET.parse(path).getroot()
    dg = root.find('diagram')
    gm = dg.find('mxGraphModel')
    W, H = float(gm.get('pageWidth')), float(gm.get('pageHeight'))
    cells, order = {}, []
    for c in gm.find('root').findall('mxCell'):
        o = Cell()
        o.id = c.get('id')
        o.style = parse_style(c.get('style'))
        o.value = clean(c.get('value')) if o.style.get('html') == '1' else (c.get('value') or '')
        o.vertex = c.get('vertex') == '1'
        o.edge = c.get('edge') == '1'
        o.parent = c.get('parent')
        o.source, o.target = c.get('source'), c.get('target')
        g = c.find('mxGeometry')
        o.geo = g
        cells[o.id] = o
        order.append(o)
    # absolutas
    def absxy(o):
        g = o.geo
        x, y = float(g.get('x', 0)), float(g.get('y', 0))
        p = cells.get(o.parent)
        while p is not None and p.vertex:
            x += float(p.geo.get('x', 0))
            y += float(p.geo.get('y', 0))
            p = cells.get(p.parent)
        return x, y
    for o in order:
        if o.vertex:
            x, y = absxy(o)
            o.box = (x, y, float(o.geo.get('width')), float(o.geo.get('height')))
    return W, H, cells, order


# ------------------------------------------------------------------ texto
class Painter:
    def __init__(self, W, H):
        self.W, self.H = W, H
        self.fig = Figure(figsize=(W / 96, H / 96), dpi=96)
        FigureCanvasAgg(self.fig)
        self.ax = self.fig.add_axes([0, 0, 1, 1])
        self.ax.set_xlim(0, W)
        self.ax.set_ylim(H, 0)
        self.ax.axis('off')
        self.ax.add_patch(Rectangle((0, 0), W, H, fc='white', ec='none', zorder=0))
        self.z = 1
        self.texts = []   # (text artist, owner, bbox_limit)

    def zz(self):
        self.z += 1
        return self.z

    def text(self, s, x, y, st, ha='center', va='center', owner=None, limit=None, bg=None, size=None, color=None):
        if not s:
            return
        fs = size or num(st, 'fontSize', 12)
        fstyle = int(num(st, 'fontStyle', 0))
        t = self.ax.text(x, y, s, ha=ha, va=va, fontsize=fs * PT, color=color or col(st.get('fontColor'), '#000000'),
                         fontweight='bold' if fstyle & 1 else 'normal',
                         fontstyle='italic' if fstyle & 2 else 'normal',
                         multialignment=ha, linespacing=1.2, zorder=self.zz() + 1000 if bg else self.zz(),
                         clip_on=False,
                         bbox=dict(boxstyle='square,pad=0.12', fc=bg, ec='none') if bg else None)
        self.texts.append((t, owner, limit))
        return t

    def shape_text(self, o, box, st):
        x, y, w, h = box
        s = o.value
        if not s:
            return
        al = st.get('align', 'center')
        va = st.get('verticalAlign', 'middle')
        sp = num(st, 'spacing', 2)
        sl, sr = num(st, 'spacingLeft', sp), num(st, 'spacingRight', sp)
        stp, sb = num(st, 'spacingTop', sp), num(st, 'spacingBottom', sp)
        if al == 'left':
            tx, ha = x + sl, 'left'
        elif al == 'right':
            tx, ha = x + w - sr, 'right'
        else:
            tx, ha = x + w / 2 + (sl - sr) / 2, 'center'
        below = st.get('verticalLabelPosition') == 'bottom'
        if below:
            ty, tva = y + h + 4, 'top'
        elif st.get('verticalLabelPosition') == 'top':
            ty, tva = y - 4, 'bottom'
        elif va == 'top':
            ty, tva = y + stp, 'top'
        elif va == 'bottom':
            ty, tva = y + h - sb, 'bottom'
        else:
            ty, tva = y + h / 2 + (stp - sb) / 2, 'center'
        lim = None if below or st.get('verticalLabelPosition') == 'top' or st.get('_shape') == 'text' else (x, y, w, h)
        return self.text(s, tx, ty, st, ha, tva, o, lim)


def rounded_poly(pts, r):
    """Polilinea con esquinas redondeadas (Bezier cuadratica)."""
    if len(pts) < 3 or r <= 0:
        return Path(pts, [Path.MOVETO] + [Path.LINETO] * (len(pts) - 1))
    verts, codes = [pts[0]], [Path.MOVETO]
    for i in range(1, len(pts) - 1):
        p0, p1, p2 = pts[i - 1], pts[i], pts[i + 1]
        d1, d2 = math.dist(p0, p1), math.dist(p1, p2)
        rr = min(r, d1 / 2, d2 / 2)
        if rr < 0.5:
            verts.append(p1); codes.append(Path.LINETO)
            continue
        a = (p1[0] + (p0[0] - p1[0]) * rr / d1, p1[1] + (p0[1] - p1[1]) * rr / d1)
        b = (p1[0] + (p2[0] - p1[0]) * rr / d2, p1[1] + (p2[1] - p1[1]) * rr / d2)
        verts += [a, p1, b]
        codes += [Path.LINETO, Path.CURVE3, Path.CURVE3]
    verts.append(pts[-1]); codes.append(Path.LINETO)
    return Path(verts, codes)


def draw_vertex(P, o):
    st, (x, y, w, h) = o.style, o.box
    ax = P.ax
    shape = st.get('shape') or st.get('_shape', 'rect')
    fill = col(st.get('fillColor'), '#FFFFFF')
    stroke = col(st.get('strokeColor'), '#000000')
    sw = num(st, 'strokeWidth', 1) * PT
    ls = dashes(st)
    kw = dict(fc=fill if fill else 'none', ec=stroke if stroke else 'none', lw=sw if stroke else 0, ls=ls, zorder=P.zz())

    if shape == 'text':
        P.shape_text(o, o.box, st); return
    if shape == 'line':
        ax.plot([x, x + w], [y + h / 2] * 2, color=stroke or '#000', lw=sw, zorder=P.zz(), solid_capstyle='butt'); return
    if shape == 'swimlane':
        ss = num(st, 'startSize', 23)
        body = col(st.get('swimlaneFillColor'))
        if st.get('rounded') == '1':
            pass
        ax.add_patch(Rectangle((x, y), w, h, fc=body or 'none', ec='none', zorder=P.zz()))
        ax.add_patch(Rectangle((x, y), w, ss, fc=fill or 'none', ec='none', zorder=P.zz()))
        ax.add_patch(Rectangle((x, y), w, h, fc='none', ec=stroke or 'none', lw=sw, ls=ls, zorder=P.zz()))
        ax.plot([x, x + w], [y + ss] * 2, color=stroke or '#000', lw=sw, zorder=P.zz(), solid_capstyle='butt')
        s2 = dict(st); s2['verticalAlign'] = 'middle'
        P.shape_text(o, (x, y, w, ss), s2); return
    if shape == 'ellipse':
        ax.add_patch(Ellipse((x + w / 2, y + h / 2), w, h, **kw))
        P.shape_text(o, (x + w * .1, y + h * .1, w * .8, h * .8), st); return
    if shape == 'rhombus':
        ax.add_patch(Polygon([(x + w / 2, y), (x + w, y + h / 2), (x + w / 2, y + h), (x, y + h / 2)], closed=True, **kw))
        P.shape_text(o, (x + w * .2, y + h * .2, w * .6, h * .6), st); return
    if shape == 'cylinder3':
        d = num(st, 'size', 15)
        z = P.zz()
        ax.add_patch(Ellipse((x + w / 2, y + h - d), w, 2 * d, **{**kw, 'zorder': z}))
        ax.add_patch(Rectangle((x, y + d), w, h - 2 * d, fc=kw['fc'], ec='none', zorder=z + .1))
        ax.plot([x, x], [y + d, y + h - d], color=stroke, lw=sw, zorder=z + .2)
        ax.plot([x + w, x + w], [y + d, y + h - d], color=stroke, lw=sw, zorder=z + .2)
        ax.add_patch(Ellipse((x + w / 2, y + d), w, 2 * d, **{**kw, 'zorder': z + .3}))
        P.shape_text(o, (x, y + 2 * d, w, h - 3 * d), st); return
    if shape == 'umlActor':
        r = min(w * .26, h * .1)
        cx = x + w / 2
        z = P.zz()
        ax.add_patch(Ellipse((cx, y + r), 2 * r, 2 * r, fc=fill, ec=stroke, lw=sw, zorder=z))
        by = y + h * .62
        for (a, b) in [((cx, y + 2 * r), (cx, by)), ((x, y + h * .36), (x + w, y + h * .36)),
                       ((cx, by), (x, y + h)), ((cx, by), (x + w, y + h))]:
            ax.plot([a[0], b[0]], [a[1], b[1]], color=stroke, lw=sw, zorder=z, solid_capstyle='round')
        P.shape_text(o, o.box, st); return
    # rect / rounded
    if st.get('rounded') == '1':
        if st.get('absoluteArcSize') == '1':
            r = min(num(st, 'arcSize', 20) / 2, w / 2, h / 2)
        else:
            r = min(w, h) * num(st, 'arcSize', 15) / 100
        ax.add_patch(FancyBboxPatch((x + r, y + r), w - 2 * r, h - 2 * r, boxstyle=f'round,pad={r},rounding_size={r}', **kw))
    else:
        ax.add_patch(Rectangle((x, y), w, h, **kw))
    P.shape_text(o, o.box, st)


def arrow(P, tip, frm, kind, fill, color, lw, size):
    """Dibuja marcador en 'tip' apuntando desde 'frm'; devuelve punto donde termina la linea."""
    if kind in (None, 'none'):
        return tip
    dx, dy = tip[0] - frm[0], tip[1] - frm[1]
    L = math.hypot(dx, dy) or 1
    ux, uy = dx / L, dy / L
    nx, ny = -uy, ux
    n = max(size * 1.9, 9) * (1.0 if kind != 'diamond' else 1.15)
    wd = n * .42
    base = (tip[0] - ux * n, tip[1] - uy * n)
    z = P.zz()
    ec = color
    if kind in ('block', 'classic'):
        pts = [tip, (base[0] + nx * wd, base[1] + ny * wd), (base[0] - nx * wd, base[1] - ny * wd)]
        if kind == 'classic':
            pts.insert(2, (tip[0] - ux * n * .75, tip[1] - uy * n * .75))
        P.ax.add_patch(Polygon(pts, closed=True, fc=color if fill else 'white', ec=ec, lw=lw, joinstyle='miter', zorder=z))
        return (base[0] + ux * .5, base[1] + uy * .5) if fill else base
    if kind in ('open', 'openThin'):
        P.ax.plot([base[0] + nx * wd, tip[0], base[0] - nx * wd], [base[1] + ny * wd, tip[1], base[1] - ny * wd],
                  color=color, lw=lw, zorder=z, solid_joinstyle='miter')
        return tip
    if kind in ('diamond', 'diamondThin'):
        mid = (tip[0] - ux * n / 2, tip[1] - uy * n / 2)
        far = (tip[0] - ux * n, tip[1] - uy * n)
        pts = [tip, (mid[0] + nx * wd, mid[1] + ny * wd), far, (mid[0] - nx * wd, mid[1] - ny * wd)]
        P.ax.add_patch(Polygon(pts, closed=True, fc=color if fill else 'white', ec=ec, lw=lw, zorder=z))
        return far
    if kind == 'oval':
        r = n * .3
        P.ax.add_patch(Ellipse((tip[0] - ux * r, tip[1] - uy * r), 2 * r, 2 * r, fc=color if fill else 'white', ec=ec, lw=lw, zorder=z))
        return (tip[0] - ux * 2 * r, tip[1] - uy * 2 * r)
    return tip


def edge_points(o, cells):
    st = o.style
    g = o.geo
    pts = []
    arr = g.find("Array[@as='points']") if g is not None else None
    if arr is not None:
        pts = [(float(p.get('x')), float(p.get('y'))) for p in arr.findall('mxPoint')]

    def end(cid, pref, other):
        c = cells.get(cid)
        if c is not None and c.vertex:
            x, y, w, h = c.box
            if f'{pref}X' in st:
                return (x + float(st[f'{pref}X']) * w, y + float(st[f'{pref}Y']) * h)
            cx, cy = x + w / 2, y + h / 2
            return (cx, cy)
        return None
    p0 = end(o.source, 'exit', None)
    p1 = end(o.target, 'entry', None)
    if p0 is None or p1 is None:
        for mp in g.findall('mxPoint'):
            q = (float(mp.get('x')), float(mp.get('y')))
            if mp.get('as') == 'sourcePoint' and p0 is None:
                p0 = q
            if mp.get('as') == 'targetPoint' and p1 is None:
                p1 = q
    return [p0] + pts + [p1]


def point_at(pts, t):
    seg = [math.dist(pts[i], pts[i + 1]) for i in range(len(pts) - 1)]
    tot = sum(seg) or 1
    d, acc = t * tot, 0
    for i, s in enumerate(seg):
        if acc + s >= d or i == len(seg) - 1:
            f = (d - acc) / s if s else 0
            return (pts[i][0] + (pts[i + 1][0] - pts[i][0]) * f, pts[i][1] + (pts[i + 1][1] - pts[i][1]) * f)
        acc += s


def draw_edge(P, o, cells):
    st = o.style
    pts = [p for p in edge_points(o, cells)]
    # quitar duplicados consecutivos
    clean_pts = [pts[0]]
    for p in pts[1:]:
        if math.dist(p, clean_pts[-1]) > 0.5:
            clean_pts.append(p)
    pts = clean_pts
    color = col(st.get('strokeColor'), '#000000')
    lw = num(st, 'strokeWidth', 1) * PT
    sw = num(st, 'strokeWidth', 1)
    end_k, start_k = st.get('endArrow', 'classic'), st.get('startArrow', 'none')
    end_f, start_f = st.get('endFill', '1') == '1', st.get('startFill', '1') == '1'
    line = list(pts)
    if end_k != 'none':
        line[-1] = arrow(P, pts[-1], pts[-2], end_k, end_f, color, lw, num(st, 'endSize', 6) * 1.0)
    if start_k != 'none':
        line[0] = arrow(P, pts[0], pts[1], start_k, start_f, color, lw, num(st, 'startSize', 6) * 1.0)
    r = num(st, 'arcSize', 20) / 2 if st.get('rounded') == '1' else 0
    P.ax.add_patch(PathPatch(rounded_poly(line, r), fc='none', ec=color, lw=lw, ls=dashes(st), zorder=P.zz(),
                             capstyle='butt', joinstyle='round'))
    if o.value:
        t = (float(o.geo.get('x', 0)) + 1) / 2
        px, py = point_at(pts, t)
        va = st.get('verticalAlign', 'middle')
        al = st.get('align', 'center')
        gap = 4
        if va == 'bottom':
            py -= gap
            tva = 'bottom'
        elif va == 'top':
            py += gap
            tva = 'top'
        else:
            tva = 'center'
        bg = col(st.get('labelBackgroundColor'))
        P.text(o.value, px, py, st, al, tva, None, None, bg=bg)


def render(path, stem, report=None):
    W, H, cells, order = load(path)
    P = Painter(W, H)
    for o in order:
        if o.vertex:
            draw_vertex(P, o)
        elif o.edge:
            draw_edge(P, o, cells)
    # comprobaciones de texto
    fig = P.fig
    fig.canvas.draw()
    rend = fig.canvas.get_renderer()
    inv = P.ax.transData.inverted()
    warns = []
    boxes = []
    for t, owner, lim in P.texts:
        bb = t.get_window_extent(rend)
        (x0, y0), (x1, y1) = inv.transform((bb.x0, bb.y0)), inv.transform((bb.x1, bb.y1))
        ty0, ty1 = min(y0, y1), max(y0, y1)
        boxes.append((x0, ty0, x1, ty1, t.get_text()[:30].replace('\n', ' / ')))
        if x0 < -1 or x1 > W + 1 or ty0 < -1 or ty1 > H + 1:
            warns.append(f'FUERA DE PAGINA: "{t.get_text()[:40]}"')
        if lim and owner is not None:
            bx, by, bw, bh = lim
            if x0 < bx - 1.5 or x1 > bx + bw + 1.5 or ty0 < by - 1.5 or ty1 > by + bh + 1.5:
                warns.append(f'DESBORDA {owner.id}: "{t.get_text()[:40]}" texto=({x0:.0f},{ty0:.0f},{x1:.0f},{ty1:.0f}) caja=({bx:.0f},{by:.0f},{bx+bw:.0f},{by+bh:.0f})')
    for i in range(len(boxes)):
        for j in range(i + 1, len(boxes)):
            a, b = boxes[i], boxes[j]
            if a[0] < b[2] - 1 and b[0] < a[2] - 1 and a[1] < b[3] - 1 and b[1] < a[3] - 1:
                warns.append(f'SOLAPE TEXTO: "{a[4]}" con "{b[4]}"')
    meta = dict(Title=stem.split('/')[-1].split('\\')[-1], Creator='Renderizador ServiX')
    fig.savefig(stem + '.png', dpi=192, metadata={'Software': 'Renderizador ServiX', 'Title': meta['Title']})
    fig.savefig(stem + '.svg', metadata={'Creator': 'Renderizador ServiX', 'Title': meta['Title'], 'Date': None})
    fig.savefig(stem + '.pdf', metadata={'Creator': 'Renderizador ServiX', 'Title': meta['Title'], 'Producer': 'matplotlib'})
    return (W, H), warns


if __name__ == '__main__':
    size, w = render(sys.argv[1], sys.argv[2])
    print(size, *w, sep='\n')
