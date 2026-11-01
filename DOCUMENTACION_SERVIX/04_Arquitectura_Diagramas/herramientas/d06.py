from lib import D

LH = 14.6


def ent(d, id, x, y, w, name, fields, kind='class_head', parent=None):
    body = len(fields) * LH + 14
    h = 34 + body
    d.v(id, x, y, w, h, name, kind, parent)
    d.v(id + '_f', x, y + 34, w, body, '\n'.join(fields), 'attrs', id)
    return h


def build():
    d = D(6, '06_Entidad_Relacion', 'Modelo de datos de Cloud Firestore',
          'Colecciones, documentos y relaciones lógicas (Firestore no impone claves foráneas)', 1700, 1040)
    ent(d, 'usr', 60, 130, 300, 'usuarios/{uid}', [
        'PK  uid: String (= id del documento)', 'email: String', 'displayName: String', 'phone: String',
        'photoUrl: String?', 'role: String  (CLIENTE | ADMINISTRADOR)', 'createdAt: Long'])
    ent(d, 'sol', 60, 450, 300, 'solicitudes_atencion/{id}', [
        'PK  id: String', 'FK  clienteId: String  (= uid)', 'clienteNombre: String', 'clienteCorreo: String',
        'fechaHora: Long', 'motivo: String', 'mensaje: String', 'prioridad: String', 'estado: String',
        'respuestaAdmin: String', 'atendidoPor: String', 'fechaResolucion: Long?'])
    d.v('sub', 470, 120, 1160, 620, 'usuarios/{uid}/…   (subcolecciones del usuario)', 'container',
        extra='align=left;spacingLeft=14;')
    ent(d, 'cl', 500, 175, 290, 'clientes/{clienteId}', [
        'PK  clienteId: String', 'nombre: String', 'documento: String', 'telefono: String', 'correo: String',
        'direccion: String', 'notas: String', 'fechaRegistro: Long', 'activo: Boolean', 'esFavorito: Boolean',
        'fotoBase64: String?'], parent='sub')
    ent(d, 'sv', 880, 175, 290, 'servicios/{servicioId}', [
        'PK  servicioId: String', 'FK  clienteId: String', 'nombre: String', 'descripcion: String',
        'categoria: String', 'precio: Double', 'fecha: String', 'fechaEntrega: String',
        'estado: String', 'prioridad: String', 'observaciones: String', 'esFavorito: Boolean',
        'timestamp: Long'], parent='sub')
    ent(d, 'nt', 1260, 175, 290, 'notificaciones/{id}', [
        'PK  id: String', 'tipo: String', 'titulo: String', 'descripcion: String', 'fecha: Long',
        'leida: Boolean', 'FK  referenciaId: String?', 'tipoReferencia: String'], parent='sub')
    ent(d, 'ac', 1260, 440, 290, 'actividades/{id}', [
        'PK  id: String', 'tipo: String', 'titulo: String', 'descripcion: String', 'fecha: Long'], parent='sub')
    ent(d, 'ap', 500, 490, 290, 'configuracion/app_config', [
        'monedaSimbolo: String', 'notificacionesActivas: Boolean', 'sonidos: Boolean',
        'modoOscuroForzado: Boolean', 'autoguardadoLocal: Boolean', 'onboardingVisto: Boolean',
        'objetivoServiciosSemanal: Int', 'objetivoIngresosSemanal: Double'], parent='sub')
    ent(d, 'up', 880, 490, 290, 'configuracion/user_profile', [
        'nombre: String', 'rol: String', 'especialidad: String', 'empresa: String', 'telefono: String',
        'correo: String', 'ciudad: String', 'descripcion: String', 'fotoPerfilBase64: String?'], parent='sub')
    d.e('r0', 'usr', 'sub', 'r', 'l', '1 : N\ncontiene', 'assoc', f0=(212 - 130) / d.abs['usr'][3], f1=(212 - 120) / 620)
    d.e('r1', 'sv', 'cl', 'l', 'r', 'clienteId\nN : 1', 'assoc', f0=(250 - 175) / d.abs['sv'][3], f1=(250 - 175) / d.abs['cl'][3])
    d.e('r2', 'nt', 'sv', 'l', 'r', 'referenciaId\n(lógica)', 'dep', f0=(250 - 175) / d.abs['nt'][3], f1=(250 - 175) / d.abs['sv'][3])
    d.e('r3', 'sol', 'usr', 't', 'b', 'clienteId = uid\nN : 1', 'assoc')
    d.v('rules', 60, 770, 1570, 120,
        'Reglas de acceso (firestore.rules)\n'
        '•  usuarios/{uid}: lee el dueño o un administrador. Crea solo el dueño, con role CLIENTE o ADMINISTRADOR. El role no cambia tras la creación (salvo por un administrador). Nadie borra.\n'
        '•  Subcolecciones del usuario: lectura y escritura del dueño o de un administrador (solo clientes, servicios, notificaciones, actividades, configuracion).\n'
        '•  solicitudes_atencion: lee un administrador o el cliente dueño (clienteId = uid). Crea cualquier autenticado con clienteId = uid. Actualiza y borra solo un administrador.\n'
        '•  Cualquier otra ruta (incluidas colecciones raíz heredadas): denegada.', 'note')
    d.legend(40, 910, 1620, [
        ('v', 'blue', 'Colección / documento'), ('e', 'assoc', 'Relación estructural (contiene / clave)'),
        ('e', 'dep', 'Referencia lógica sin integridad'), ('v', 'note', 'Reglas de seguridad')])
    return d
