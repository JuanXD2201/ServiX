from lib import D

LH = 14.6


def cls(d, id, x, y, w, name, attrs, methods=None, enum=False):
    head = 48 if enum else 34
    title = ('«enumeration»\n' + name) if enum else name
    body = len(attrs) * LH + 14
    mh = (len(methods) * LH + 12) if methods else 0
    h = head + body + mh
    d.v(id, x, y, w, h, title, 'enum_head' if enum else 'class_head')
    d.v(id + '_a', x, y + head, w, body, '\n'.join(attrs), 'attrs', id)
    if methods:
        d.v(id + '_s', x, y + head + body, w, 2, '', 'sep', id)
        d.v(id + '_m', x, y + head + body + 2, w, mh, '\n'.join(methods), 'attrs', id)
    return h


def build():
    d = D(5, '05_Clases', 'Diagrama de clases del dominio',
          'Modelos de domain/model con sus atributos, enumeraciones y relaciones por identificador', 1900, 1130)
    W = 330
    y1 = 140
    h_cli = cls(d, 'cli', 40, y1, W, 'Cliente', [
        '+ clienteId: String', '+ nombre: String', '+ documento: String', '+ telefono: String', '+ correo: String',
        '+ direccion: String', '+ notas: String', '+ fechaRegistro: Long', '+ activo: Boolean',
        '+ esFavorito: Boolean', '+ fotoBase64: String?'],
        ['+ iniciales: String', '+ calcularSegmentos(servicios): List<SegmentoCliente>'])
    h_srv = cls(d, 'srv', 470, y1, W, 'Servicio', [
        '+ servicioId: String', '+ clienteId: String', '+ nombre: String', '+ descripcion: String',
        '+ categoria: String', '+ precio: Double', '+ fecha: String', '+ fechaEntrega: String',
        '+ estado: EstadoServicio', '+ prioridad: PrioridadServicio', '+ observaciones: String',
        '+ esFavorito: Boolean', '+ timestamp: Long'])
    h_not = cls(d, 'not', 900, y1, W, 'Notificacion', [
        '+ id: String', '+ tipo: TipoNotificacion', '+ titulo: String', '+ descripcion: String',
        '+ fecha: Long', '+ leida: Boolean', '+ referenciaId: String?', '+ tipoReferencia: String'])
    h_sol = cls(d, 'sol', 1330, y1, W, 'SolicitudAtencion', [
        '+ id: String', '+ clienteId: String', '+ clienteNombre: String', '+ clienteCorreo: String',
        '+ fechaHora: Long', '+ motivo: String', '+ mensaje: String', '+ prioridad: PrioridadAtencion',
        '+ estado: EstadoAtencion', '+ respuestaAdmin: String', '+ atendidoPor: String',
        '+ fechaResolucion: Long?'])
    row2 = y1 + max(h_cli, h_srv, h_not, h_sol) + 80
    e1 = cls(d, 'seg', 125, row2, 160, 'SegmentoCliente', ['VIP', 'FRECUENTE', 'NUEVO', 'PENDIENTE', 'INACTIVO'], enum=True)
    e2 = cls(d, 'est', 470, row2, 160, 'EstadoServicio', ['PENDIENTE', 'EN_PROCESO', 'COMPLETADO', 'CANCELADO'], enum=True)
    e3 = cls(d, 'pri', 640, row2, 160, 'PrioridadServicio', ['BAJA', 'MEDIA', 'ALTA', 'URGENTE'], enum=True)
    e4 = cls(d, 'tno', 945, row2, 240, 'TipoNotificacion', [
        'SERVICIO_HOY', 'SERVICIO_URGENTE', 'SERVICIO_PENDIENTE', 'SERVICIO_COMPLETADO', 'NUEVO_CLIENTE',
        'RECORDATORIO_SEGUIMIENTO', 'SOLICITUD_ATENCION'], enum=True)
    e5 = cls(d, 'eat', 1330, row2, 160, 'EstadoAtencion', ['PENDIENTE', 'EN_ATENCION', 'RESUELTO'], enum=True)
    e6 = cls(d, 'pat', 1500, row2, 160, 'PrioridadAtencion', ['NORMAL', 'IMPORTANTE', 'URGENTE'], enum=True)
    row3 = row2 + max(e1, e2, e3, e4, e5, e6) + 70
    h_act = cls(d, 'act', 40, row3, W, 'Actividad', [
        '+ id: String', '+ tipo: TipoActividad', '+ titulo: String', '+ descripcion: String', '+ fecha: Long'])
    cls(d, 'tac', 450, row3, 240, 'TipoActividad', [
        'CLIENTE_CREADO', 'CLIENTE_EDITADO', 'CLIENTE_ELIMINADO', 'SERVICIO_CREADO', 'SERVICIO_ACTUALIZADO',
        'SERVICIO_COMPLETADO', 'SERVICIO_CANCELADO'], enum=True)
    cls(d, 'upr', 790, row3, W, 'UserProfile', [
        '+ nombre: String', '+ rol: String', '+ especialidad: String', '+ empresa: String', '+ telefono: String',
        '+ correo: String', '+ ciudad: String', '+ descripcion: String', '+ fotoPerfilBase64: String?'])
    cls(d, 'aut', 1220, row3, W, 'AuthUser', [
        '+ uid: String', '+ email: String', '+ displayName: String', '+ phone: String', '+ photoUrl: String?',
        '+ role: UserRole = CLIENTE', '+ createdAt: Long'])
    cls(d, 'rol', 1650, row3, 210, 'UserRole', ['CLIENTE', 'ADMINISTRADOR'],
        ['+ displayName: String', '+ fromString(String): UserRole'], enum=True)

    def fy(id, yy):
        x, y, w, h = d.abs[id]
        return (yy - y) / h
    # Servicio -> Cliente
    d.e('r1', 'srv', 'cli', 'l', 'r', '', 'assoc', f0=fy('srv', 200), f1=fy('cli', 200))
    d.t('m1', 374, 178, 30, 20, '1', 12, '#091E42', True)
    d.t('m2', 438, 178, 30, 20, '*', 14, '#091E42', True)
    d.t('m3', 372, 204, 98, 20, 'pertenece a', 12, '#091E42', False, 'center')
    # Notificacion -> Servicio (referencia lógica)
    d.e('r2', 'not', 'srv', 'l', 'r', 'referencia\n(lógica)', 'dep', f0=fy('not', 200), f1=fy('srv', 200))
    # Solicitud -> Cliente (por clienteId)
    d.e('r3', 'sol', 'cli', 't', 't', 'clienteId (referencia por id)', 'dep', f0=.5, f1=.5,
        via=[(1495, 112), (205, 112)])
    # Cliente -> SegmentoCliente
    d.e('r4', 'cli', 'seg', 'b', 't', '«usa»', 'dep', f0=(205 - 40) / W, f1=.5)
    # Servicio -> enums
    d.e('r5', 'srv', 'est', 'b', 't', 'estado', 'assoc', f0=(550 - 470) / W, f1=.5)
    d.e('r6', 'srv', 'pri', 'b', 't', 'prioridad', 'assoc', f0=(720 - 470) / W, f1=.5)
    d.e('r7', 'not', 'tno', 'b', 't', 'tipo', 'assoc', f0=.5, f1=.5)
    d.e('r8', 'sol', 'eat', 'b', 't', 'estado', 'assoc', f0=(1410 - 1330) / W, f1=.5)
    d.e('r9', 'sol', 'pat', 'b', 't', 'prioridad', 'assoc', f0=(1580 - 1330) / W, f1=.5)
    d.e('r10', 'act', 'tac', 'r', 'l', 'tipo', 'assoc', f0=fy('act', row3 + 60), f1=fy('tac', row3 + 60))
    d.e('r11', 'aut', 'rol', 'r', 'l', 'role', 'assoc', f0=fy('aut', row3 + 60), f1=fy('rol', row3 + 60))
    d.v('nota', 40, row3 + 160, 370, 60, 'Los enums mostrados se guardan en Firestore\ncomo String (nombre de la constante).', 'note')
    d.legend(40, row3 + 250, 1820, [
        ('v', 'blue', 'Clase (data class)'), ('v', 'rounded=1;absoluteArcSize=1;arcSize=6;fillColor=#F59E0B;strokeColor=#B45309;strokeWidth=1.5;', 'Enumeración'),
        ('e', 'assoc', 'Asociación / atributo tipado'), ('e', 'dep', 'Referencia por identificador o dependencia')])
    return d
