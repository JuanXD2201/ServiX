"""Generador de .drawio (mxGraph) con rejilla, estilos ServiX y enrutado ortogonal."""
import xml.etree.ElementTree as ET
from PIL import ImageFont

PAL = dict(dark='#091E42', blue='#0066F5', light='#00A3FF', bg='#E0F2FE',
           amber='#F59E0B', amberbg='#FFFBEB', gray='#475569', line='#94A3B8',
           pale='#F1F5F9', ink='#0F172A', white='#FFFFFF', green='#15803D')
VERSION = 'v1.0'
FECHA = '2026-11-01'

BASE = 'fontFamily=Arial;fontSize=13;html=0;fontColor=#091E42;'
RB = 'rounded=1;absoluteArcSize=1;arcSize=12;'
VS = {
    'comp': BASE + RB + 'fillColor=#FFFFFF;strokeColor=#0066F5;strokeWidth=1.5;',
    'dark': BASE + RB + 'fillColor=#091E42;strokeColor=#091E42;strokeWidth=1.5;fontColor=#FFFFFF;fontStyle=1;',
    'blue': BASE + RB + 'fillColor=#0066F5;strokeColor=#0066F5;strokeWidth=1.5;fontColor=#FFFFFF;',
    'light': BASE + RB + 'fillColor=#E0F2FE;strokeColor=#0066F5;strokeWidth=1.5;',
    'ext': BASE + RB + 'fillColor=#F1F5F9;strokeColor=#64748B;strokeWidth=1.5;',
    'opt': BASE + RB + 'fillColor=#FFFBEB;strokeColor=#F59E0B;strokeWidth=2;dashed=1;dashPattern=6 4;',
    'res': BASE + RB + 'fillColor=#FFFFFF;strokeColor=#94A3B8;strokeWidth=1.5;dashed=1;dashPattern=3 3;fontColor=#64748B;',
    'ok': BASE + RB + 'fillColor=#DCFCE7;strokeColor=#15803D;strokeWidth=1.5;',
    'err': BASE + RB + 'fillColor=#FEE2E2;strokeColor=#B91C1C;strokeWidth=1.5;',
    'pill': BASE + 'rounded=1;absoluteArcSize=1;arcSize=100;fillColor=#091E42;strokeColor=#091E42;fontColor=#FFFFFF;fontStyle=1;strokeWidth=1.5;',
    'decision': BASE + 'rhombus;fillColor=#FFFBEB;strokeColor=#F59E0B;strokeWidth=2;',
    'usecase': BASE + 'ellipse;fillColor=#FFFFFF;strokeColor=#0066F5;strokeWidth=1.5;',
    'usecase_opt': BASE + 'ellipse;fillColor=#FFFBEB;strokeColor=#F59E0B;strokeWidth=1.5;dashed=1;dashPattern=6 4;',
    'db': BASE + 'shape=cylinder3;boundedLbl=1;size=12;fillColor=#E0F2FE;strokeColor=#0066F5;strokeWidth=1.5;',
    'actor': BASE + 'shape=umlActor;verticalLabelPosition=bottom;verticalAlign=top;fillColor=#E0F2FE;strokeColor=#091E42;strokeWidth=2;fontStyle=1;fontSize=14;',
    'container': BASE + 'swimlane;horizontal=1;startSize=34;fillColor=#E0F2FE;swimlaneFillColor=#F8FAFC;strokeColor=#0066F5;strokeWidth=1.5;fontStyle=1;fontSize=14;',
    'container_dark': BASE + 'swimlane;horizontal=1;startSize=34;fillColor=#091E42;swimlaneFillColor=#F8FAFC;strokeColor=#091E42;strokeWidth=1.5;fontStyle=1;fontSize=14;fontColor=#FFFFFF;',
    'container_ext': BASE + 'swimlane;horizontal=1;startSize=34;fillColor=#E2E8F0;swimlaneFillColor=#FFFFFF;strokeColor=#64748B;strokeWidth=1.5;fontStyle=1;fontSize=14;',
    'container_opt': BASE + 'swimlane;horizontal=1;startSize=34;fillColor=#FEF3C7;swimlaneFillColor=#FFFFFF;strokeColor=#F59E0B;strokeWidth=1.5;fontStyle=1;fontSize=14;dashed=1;dashPattern=6 4;',
    'group': BASE + RB + 'fillColor=#F8FAFC;strokeColor=#94A3B8;strokeWidth=1.5;dashed=1;dashPattern=6 4;verticalAlign=top;align=left;spacingLeft=12;spacingTop=6;fontStyle=1;fontColor=#475569;',
    'frag': BASE + 'fillColor=none;strokeColor=#475569;strokeWidth=1.2;verticalAlign=top;align=left;spacingLeft=8;spacingTop=3;fontSize=12;fontStyle=1;fontColor=#475569;',
    'frag_tab': BASE + 'fillColor=#FFFFFF;strokeColor=#475569;strokeWidth=1.2;fontSize=12;fontStyle=1;fontColor=#475569;',
    'bar': BASE + 'fillColor=#E0F2FE;strokeColor=#0066F5;strokeWidth=1.5;',
    'class_head': BASE + 'swimlane;horizontal=1;startSize=34;fillColor=#0066F5;swimlaneFillColor=#FFFFFF;strokeColor=#0066F5;strokeWidth=1.5;fontStyle=1;fontSize=14;fontColor=#FFFFFF;',
    'enum_head': BASE + 'swimlane;horizontal=1;startSize=48;fillColor=#F59E0B;swimlaneFillColor=#FFFFFF;strokeColor=#B45309;strokeWidth=1.5;fontStyle=1;fontSize=14;fontColor=#091E42;',
    'attrs': BASE + 'text;fontSize=12;align=left;verticalAlign=top;spacingLeft=10;spacingTop=4;',
    'sep': 'shape=line;strokeColor=#0066F5;strokeWidth=1;',
    'text': BASE + 'text;',
    'note': BASE + 'rounded=1;absoluteArcSize=1;arcSize=8;fillColor=#FFFBEB;strokeColor=#F59E0B;strokeWidth=1;fontSize=12;align=left;spacingLeft=10;fontColor=#475569;',
    'band': 'fillColor=#091E42;strokeColor=none;',
    'accent': 'fillColor=#F59E0B;strokeColor=none;',
    'rule': 'shape=line;strokeColor=#CBD5E1;strokeWidth=1;',
}
EBASE = ('rounded=1;arcSize=16;html=0;fontFamily=Arial;fontSize=12;fontColor=#091E42;'
         'labelBackgroundColor=#FFFFFF;strokeWidth=1.5;exitPerimeter=0;entryPerimeter=0;')
ES = {
    'flow': EBASE + 'endArrow=block;endFill=1;strokeColor=#091E42;',
    'blue': EBASE + 'endArrow=block;endFill=1;strokeColor=#0066F5;',
    'dep': EBASE + 'endArrow=open;endFill=0;dashed=1;dashPattern=6 4;strokeColor=#475569;',
    'impl': EBASE + 'endArrow=block;endFill=0;dashed=1;dashPattern=6 4;strokeColor=#0066F5;endSize=10;',
    'opt': EBASE + 'endArrow=block;endFill=1;dashed=1;dashPattern=6 4;strokeColor=#F59E0B;strokeWidth=2;',
    'res': EBASE + 'endArrow=open;endFill=0;dashed=1;dashPattern=3 3;strokeColor=#94A3B8;',
    'plain': EBASE + 'endArrow=none;strokeColor=#0066F5;',
    'plain_dash': EBASE + 'endArrow=none;dashed=1;dashPattern=6 4;strokeColor=#64748B;',
    'assoc': EBASE + 'endArrow=open;endFill=0;strokeColor=#091E42;',
    'comp_agg': EBASE + 'endArrow=none;startArrow=diamond;startFill=1;startSize=12;strokeColor=#091E42;',
    'msg': EBASE + 'rounded=0;endArrow=block;endFill=1;strokeColor=#091E42;labelBackgroundColor=none;verticalAlign=bottom;',
    'ret': EBASE + 'rounded=0;endArrow=open;endFill=0;dashed=1;dashPattern=6 4;strokeColor=#475569;labelBackgroundColor=none;verticalAlign=bottom;fontColor=#475569;',
    'life': 'endArrow=none;dashed=1;dashPattern=4 4;strokeColor=#94A3B8;strokeWidth=1.2;html=0;',
    'selfmsg': EBASE + 'rounded=0;endArrow=block;endFill=1;strokeColor=#091E42;labelBackgroundColor=none;align=left;',
    'gen': EBASE + 'endArrow=block;endFill=0;endSize=12;strokeColor=#091E42;',
    'tag': EBASE + 'endArrow=none;dashed=1;dashPattern=3 3;strokeColor=#F59E0B;',
}

_fonts = {}


def tw(text, size=13, bold=False):
    """Ancho en px de la linea mas ancha (Arial)."""
    key = (size, bold)
    if key not in _fonts:
        _fonts[key] = ImageFont.truetype('arialbd.ttf' if bold else 'arial.ttf', size)
    return max((_fonts[key].getlength(l) for l in str(text).split('\n')), default=0)


SIDE = {'l': (0, .5), 'r': (1, .5), 't': (.5, 0), 'b': (.5, 1)}


class D:
    def __init__(self, num, fname, title, subtitle, W, H, total=11):
        self.num, self.fname, self.title, self.W, self.H = num, fname, title, W, H
        self.cells = []
        self.abs = {}
        self.parent = {}
        self.n = 0
        self._chrome(subtitle, total)

    def _nid(self, p):
        self.n += 1
        return f'{p}{self.n}'

    def v(self, id, x, y, w, h, text='', kind='comp', parent=None, extra=''):
        id = id or self._nid('v')
        style = VS.get(kind, kind) + extra
        self.abs[id] = (x, y, w, h)
        self.parent[id] = parent
        self.cells.append(('v', dict(id=id, x=x, y=y, w=w, h=h, text=text, style=style, parent=parent)))
        return id

    def t(self, id, x, y, w, h, text, size=12, color=None, bold=False, align='left', valign='middle', extra='', parent=None):
        st = 'text;fontFamily=Arial;html=0;fontSize=%d;align=%s;verticalAlign=%s;' % (size, align, valign)
        st += 'fontColor=%s;' % (color or PAL['ink'])
        if bold:
            st += 'fontStyle=1;'
        return self.v(id, x, y, w, h, text, st + extra, parent)

    def pt(self, id, side, f=None):
        x, y, w, h = self.abs[id]
        fx, fy = SIDE[side]
        if f is not None:
            if side in 'lr':
                fy = f
            else:
                fx = f
        return (x + fx * w, y + fy * h), (fx, fy)

    @staticmethod
    def route(p0, s0, p1, s1, mid=None):
        h0, h1 = s0 in 'lr', s1 in 'lr'
        if h0 and h1:
            if abs(p0[1] - p1[1]) < 1:
                return []
            mx = mid if mid is not None else (p0[0] + p1[0]) / 2
            return [(mx, p0[1]), (mx, p1[1])]
        if not h0 and not h1:
            if abs(p0[0] - p1[0]) < 1:
                return []
            my = mid if mid is not None else (p0[1] + p1[1]) / 2
            return [(p0[0], my), (p1[0], my)]
        if h0 and not h1:
            return [(p1[0], p0[1])]
        return [(p0[0], p1[1])]

    def e(self, id, src, dst, s0, s1, label='', kind='flow', f0=None, f1=None, via=None, mid=None,
          lx=0.0, extra=''):
        id = id or self._nid('e')
        p0, (ex, ey) = self.pt(src, s0, f0)
        p1, (nx, ny) = self.pt(dst, s1, f1)
        pts = via if via is not None else self.route(p0, s0, p1, s1, mid)
        style = ES.get(kind, kind) + f'exitX={ex:g};exitY={ey:g};entryX={nx:g};entryY={ny:g};' + extra
        self.cells.append(('e', dict(id=id, src=src, dst=dst, text=label, style=style, pts=pts, lx=lx)))
        return id

    def pe(self, id, p0, p1, label='', kind='flow', via=None, lx=0.0, extra=''):
        """Arista libre entre dos puntos absolutos (secuencias, leyendas)."""
        id = id or self._nid('e')
        self.cells.append(('e', dict(id=id, src=None, dst=None, text=label, style=ES.get(kind, kind) + extra,
                                     pts=via or [], lx=lx, p0=p0, p1=p1)))
        return id

    def _chrome(self, subtitle, total):
        W, H = self.W, self.H
        self.v('hdr', 0, 0, W, 84, '', 'band')
        self.v('hdr2', 0, 84, W, 4, '', 'accent')
        self.t('ttl', 40, 14, W - 300, 36, self.title, 26, '#FFFFFF', True)
        self.t('sub', 40, 50, W - 300, 24, subtitle, 13, '#BFDBFE')
        self.t('brand', W - 240, 22, 200, 40, 'ServiX', 28, '#00A3FF', True, 'right')
        self.v('frule', 40, H - 46, W - 80, 2, '', 'rule')
        self.t('foot', 40, H - 40, W - 300, 28,
               f'ServiX  |  {self.title}  |  {VERSION}  |  {FECHA}', 12, PAL['gray'])
        self.t('pg', W - 240, H - 40, 200, 28, f'Diagrama {self.num} de {total}', 12, PAL['gray'], False, 'right')

    def legend(self, x, y, w, items, title='Leyenda'):
        """items: ('v', kind, texto) | ('e', kind, texto). Fluye en filas."""
        cx, cy, rowh = 14, 34, 28
        placed = []
        for it in items:
            sw = 44 if it[0] == 'e' else 34
            iw = sw + 10 + tw(it[2], 12) + 26
            if cx + iw > w - 8 and cx > 14:
                cx, cy = 14, cy + rowh
            placed.append((it, cx, cy, sw))
            cx += iw
        h = cy + rowh + 8
        self.v('leg', x, y, w, h, '', VS['comp'].replace('strokeColor=#0066F5', 'strokeColor=#CBD5E1').replace('strokeWidth=1.5', 'strokeWidth=1'))
        self.t('leg_t', x + 12, y + 4, 200, 24, title, 12, PAL['gray'], True)
        for i, (it, ix, iy, sw) in enumerate(placed):
            ax, ay = x + ix, y + iy
            if it[0] == 'v':
                self.v(f'leg_s{i}', ax, ay + 3, sw, 18, '', it[1])
            else:
                self.pe(f'leg_e{i}', (ax, ay + 12), (ax + sw, ay + 12), '', it[1])
            self.t(f'leg_x{i}', ax + sw + 8, ay, tw(it[2], 12) + 12, 24, it[2], 12, PAL['ink'])
        return h

    def to_xml(self):
        mxfile = ET.Element('mxfile', host='app.diagrams.net', agent='ServiX', version='24.7.0', type='device')
        dg = ET.SubElement(mxfile, 'diagram', id=f'sx{self.num}', name=self.title)
        g = ET.SubElement(dg, 'mxGraphModel', dx='1422', dy='800', grid='1', gridSize='10', guides='1',
                          tooltips='1', connect='1', arrows='1', fold='1', page='1', pageScale='1',
                          pageWidth=str(self.W), pageHeight=str(self.H), math='0', shadow='0')
        root = ET.SubElement(g, 'root')
        ET.SubElement(root, 'mxCell', id='0')
        ET.SubElement(root, 'mxCell', id='1', parent='0')
        for kind, c in self.cells:
            if kind == 'v':
                par = c['parent'] or '1'
                x, y = c['x'], c['y']
                if c['parent']:
                    px, py = self.abs[c['parent']][:2]
                    x, y = x - px, y - py
                cell = ET.SubElement(root, 'mxCell', id=c['id'], value=c['text'], style=c['style'],
                                     vertex='1', parent=par)
                ET.SubElement(cell, 'mxGeometry', x=f'{x:g}', y=f'{y:g}', width=f'{c["w"]:g}',
                              height=f'{c["h"]:g}', **{'as': 'geometry'})
            else:
                at = dict(id=c['id'], value=c['text'], style=c['style'], edge='1', parent='1')
                if c['src']:
                    at['source'] = c['src']
                    at['target'] = c['dst']
                cell = ET.SubElement(root, 'mxCell', **at)
                gat = {'relative': '1', 'as': 'geometry'}
                if c['lx']:
                    gat['x'] = f'{c["lx"]:g}'
                geo = ET.SubElement(cell, 'mxGeometry', **gat)
                if c['src'] is None:
                    ET.SubElement(geo, 'mxPoint', x=f'{c["p0"][0]:g}', y=f'{c["p0"][1]:g}', **{'as': 'sourcePoint'})
                    ET.SubElement(geo, 'mxPoint', x=f'{c["p1"][0]:g}', y=f'{c["p1"][1]:g}', **{'as': 'targetPoint'})
                if c['pts']:
                    arr = ET.SubElement(geo, 'Array', **{'as': 'points'})
                    for px, py in c['pts']:
                        ET.SubElement(arr, 'mxPoint', x=f'{px:g}', y=f'{py:g}')
        ET.indent(mxfile)
        return ET.tostring(mxfile, encoding='unicode')

    def save(self, outdir):
        import os
        os.makedirs(outdir, exist_ok=True)
        p = os.path.join(outdir, self.fname + '.drawio')
        with open(p, 'w', encoding='utf-8') as f:
            f.write(self.to_xml())
        return p


class Seq:
    """Diagrama de secuencia sobre D: lineas de vida, barras, mensajes, fragmentos."""
    def __init__(s, d, parts, bottom, top=110, bw=210, bh=64):
        s.d, s.cx, s.top, s.bh, s.bottom = d, {}, top, bh, bottom
        s.frags, s.bars, s.msgs, s.notes = [], [], [], []
        for id, label, kind, cx in parts:
            d.v('p_' + id, cx - bw / 2, top, bw, bh, label, kind)
            s.cx[id] = cx
        for id in s.cx:
            d.pe('l_' + id, (s.cx[id], top + bh), (s.cx[id], bottom), '', 'life')

    def bar(s, who, y0, y1):
        s.bars.append((who, y0, y1))

    def msg(s, a, b, y, label='', kind='msg'):
        s.msgs.append((a, b, y, label, kind))

    def selfmsg(s, a, y, label):
        s.msgs.append((a, a, y, label, 'selfmsg'))

    def frag(s, a, b, y0, y1, label, dividers=()):
        s.frags.append((a, b, y0, y1, label, dividers))

    def note(s, x, y, w, h, text):
        s.notes.append((x, y, w, h, text))

    def flush(s):
        d, n = s.d, 0
        for i, (who, y0, y1) in enumerate(s.bars):
            d.v(f'bar{i}', s.cx[who] - 7, y0, 14, y1 - y0, '', 'bar')
        for a, b, y0, y1, label, divs in s.frags:
            x0, x1 = s.cx[a] - 70 if isinstance(a, str) else a, s.cx[b] + 70 if isinstance(b, str) else b
            n += 1
            d.v(f'fr{n}', x0, y0, x1 - x0, y1 - y0, '', 'frag')
            d.v(f'frl{n}', x0, y0, tw(label, 12, True) + 20, 22, label, 'frag_tab')
            for k, (dy, txt) in enumerate(divs):
                d.pe(f'frd{n}_{k}', (x0, dy), (x1, dy), '', 'plain_dash')
                d.v(f'frdt{n}_{k}', x0, dy, tw(txt, 12, True) + 20, 22, txt, 'frag_tab')
        for i, (a, b, y, label, kind) in enumerate(s.msgs):
            if a == b:
                x = s.cx[a] + 7
                d.pe(f'm{i}', (x, y), (x, y + 30), '', 'selfmsg', via=[(x + 46, y), (x + 46, y + 30)])
                d.t(f'mt{i}', x + 56, y - 6, 360, 42, label, 12, PAL['ink'], False, 'left')
            else:
                sg = 1 if s.cx[b] > s.cx[a] else -1
                d.pe(f'm{i}', (s.cx[a] + 7 * sg, y), (s.cx[b] - 7 * sg, y), label, kind)
        for i, (x, y, w, h, text) in enumerate(s.notes):
            d.v(f'nt{i}', x, y, w, h, text, 'note')
