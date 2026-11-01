import importlib
import os
import sys
import xml.dom.minidom

sys.path.insert(0, os.path.dirname(__file__))
from render import render

OUT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
only = sys.argv[1:]
mods = [f'd{n:02d}' for n in range(1, 12)]
for m in mods:
    if only and m[1:] not in only:
        continue
    try:
        mod = importlib.import_module(m)
    except ModuleNotFoundError:
        continue
    d = mod.build()
    dd = os.path.join(OUT, 'Drawio')
    path = d.save(dd)
    xml.dom.minidom.parse(path)  # bien formado
    for sub in ('PNG', 'SVG', 'PDF'):
        os.makedirs(os.path.join(OUT, sub), exist_ok=True)
    stem = os.path.join(OUT, '_tmp', d.fname)
    os.makedirs(os.path.dirname(stem), exist_ok=True)
    size, warns = render(path, stem)
    for sub in ('png', 'svg', 'pdf'):
        dst = os.path.join(OUT, sub.upper(), d.fname + '.' + sub)
        if os.path.exists(dst):
            os.remove(dst)
        os.replace(stem + '.' + sub, dst)
    print(d.fname, size, 'OK' if not warns else '')
    for w in warns:
        print('   WARN', w)
