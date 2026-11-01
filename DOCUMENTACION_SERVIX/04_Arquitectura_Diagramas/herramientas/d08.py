from lib import D, Seq


def build():
    H = 1590
    d = D(8, '08_Secuencia_Operaciones', 'Secuencia: alta de servicio',
          'Operación principal del administrador: persistencia, actividad, notificación urgente y aviso opcional a Make', 1860, H)
    s = Seq(d, [('A', 'Administrador', 'dark', 150), ('UI', 'ServiceFormDialog /\nMainViewModel', 'comp', 410),
                ('UC', 'AddServicioUseCase', 'light', 670),
                ('RP', 'Repositorios Firebase\n(Servicio · Cliente ·\nActividad · Notificación)', 'comp', 930),
                ('FS', 'Cloud Firestore', 'db', 1190),
                ('WH', 'OkHttpMake-\nWebhookRepository', 'comp', 1450),
                ('MK', 'Make (opcional)', 'opt', 1710)], bottom=1445)
    s.bar('UI', 240, 1425)
    s.bar('UC', 317, 1365)
    for y0, y1 in [(387, 527), (577, 650), (702, 780), (872, 945)]:
        s.bar('RP', y0, y1)
    for y0, y1 in [(447, 470), (767, 790), (922, 945)]:
        s.bar('FS', y0, y1)
    s.bar('WH', 1067, 1220)
    s.bar('MK', 1142, 1220)
    s.frag('UC', 'FS', 820, 965, 'alt  [prioridad = URGENTE]')
    s.frag('UC', 'MK', 1000, 1295, 'opt  [URL del webhook configurada]', dividers=[(1245, '[URL vacía: se omite el envío y se registra una advertencia en el log]')])
    s.msg('A', 'UI', 250, 'Completa el formulario\ny guarda el servicio')
    s.msg('UI', 'UC', 325, 'invoke(clienteId, nombre,\nprecio, prioridad, estado…)')
    s.msg('UC', 'RP', 395, 'addServicio(…)')
    s.msg('RP', 'FS', 455, 'set usuarios/{uid}/\nservicios/{id}')
    s.msg('RP', 'UC', 520, 'Servicio creado', 'ret')
    s.msg('UC', 'RP', 585, 'getClienteById(clienteId)')
    s.msg('RP', 'UC', 645, 'Cliente', 'ret')
    s.msg('UC', 'RP', 710, 'logActividad(SERVICIO_CREADO,\ntítulo, descripción)')
    s.msg('RP', 'FS', 775, 'set …/actividades/{id}')
    s.msg('UC', 'RP', 880, 'addNotificacion(\nSERVICIO_URGENTE)')
    s.msg('RP', 'FS', 935, 'set …/notificaciones/{id}')
    s.msg('UC', 'WH', 1075, 'sendServicioEvent(SERVICIO_CREADO | SERVICIO_COMPLETADO,\nservicio, cliente, técnico, usuarioId)')
    s.msg('WH', 'MK', 1150, 'POST JSON (en segundo plano,\nDispatchers.IO)')
    s.msg('MK', 'WH', 1210, 'Código HTTP (solo se escribe en el log)', 'ret')
    s.msg('UC', 'UI', 1350, 'Servicio', 'ret')
    s.msg('UI', 'A', 1410, 'Muestra el servicio creado\nen la lista', 'ret')
    s.flush()
    d.legend(40, 1460, 1780, [
        ('v', 'light', 'Caso de uso'), ('v', 'opt', 'Integración opcional'), ('v', 'bar', 'Activación'),
        ('e', 'msg', 'Llamada'), ('e', 'ret', 'Retorno'), ('v', 'frag', 'Fragmento alt / opt')])
    return d
