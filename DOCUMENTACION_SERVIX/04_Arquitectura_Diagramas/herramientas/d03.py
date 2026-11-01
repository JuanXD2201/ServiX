from lib import D


def build():
    d = D(3, '03_Componentes', 'Diagrama de componentes',
          'De la interfaz de usuario a los servicios externos, con los repositorios como contrato', 1980, 1030)
    cols = dict(ui=(40, 240), vm=(360, 240), uc=(680, 280), rp=(1040, 260), im=(1380, 280), ex=(1740, 200))
    Y0, H0 = 120, 660
    d.v('ui', cols['ui'][0], Y0, cols['ui'][1], H0, 'Presentación', 'container')
    d.v('vm', cols['vm'][0], Y0, cols['vm'][1], H0, 'ViewModel', 'container')
    d.v('uc', cols['uc'][0], Y0, cols['uc'][1], H0, 'Casos de uso (domain)', 'container')
    d.v('rp', cols['rp'][0], Y0, cols['rp'][1], H0, 'Interfaces de repositorio', 'container')
    d.v('im', cols['im'][0], Y0, cols['im'][1], H0, 'Implementaciones (data)', 'container')
    d.v('ex', cols['ex'][0], Y0, cols['ex'][1], H0, 'Servicios externos', 'container_ext')
    ui = [('AuthScreen\nInicio de sesión, registro,\nrecuperación', 'comp'),
          ('Pantallas de administración\nHome · Clientes · Servicios\nHistorial · Perfil', 'comp'),
          ('ClientePortalScreen\nPortal del cliente', 'comp'),
          ('ChatScreen\nChat con la IA', 'comp'),
          ('Components · Dialogs\nUI reutilizable', 'ext')]
    for i, (t, k) in enumerate(ui):
        d.v(f'ui{i}', 60, 170 + i * 120, 200, 100, t, k, 'ui')
    d.v('vm0', 380, 170, 200, 330, 'MainViewModel\n\nEstado de la UI y\norquestación de los\ncasos de uso', 'blue', 'vm')
    d.v('vm1', 380, 530, 200, 90, 'AuthUiState\nEstado de autenticación', 'comp', 'vm')
    d.v('vm2', 380, 650, 200, 110, 'MainViewModelFactory\nConstruye el ViewModel\ncon sus dependencias', 'comp', 'vm')
    uc = ['Autenticación\nSignIn · SignUp · SignOut · Reset', 'Clientes\nAdd · Update · Delete · Restaurar',
          'Servicios\nAdd · Update · Estado · Duplicar', 'Notificaciones y actividad\nMarcar leída · Eliminar',
          'Atención\nCrear · Tomar · Responder · Resolver', 'Chat IA\nSendAiChatMessageUseCase',
          'Configuración\nPerfil · Objetivos · Config']
    for i, t in enumerate(uc):
        d.v(f'uc{i}', 700, 170 + i * 84, 240, 70, t, 'light', 'uc')
    rp = ['AuthRepository', 'ClienteRepository', 'ServicioRepository', 'NotificationRepository\nActividadRepository',
          'AtencionRepository', 'ConfigRepository', 'AiChatRepository', 'MakeWebhookRepository',
          'RetellAI / N8nWebhook\n(reservados)']
    im = ['FirebaseAuthRepository', 'FirebaseClienteRepository', 'FirebaseServicioRepository',
          'FirebaseNotification / Actividad\nRepository', 'FirebaseAtencionRepository', 'FirebaseConfigRepository',
          'GeminiAiChatRepository', 'OkHttpMakeWebhookRepository', 'Sin implementación']
    for i in range(9):
        y = 170 + i * 56
        res = i == 8
        d.v(f'rp{i}', 1060, y, 220, 48, rp[i], 'res' if res else 'comp', 'rp', extra='fontSize=12;')
        d.v(f'im{i}', 1400, y, 240, 48, im[i], 'res' if res else 'comp', 'im', extra='fontSize=12;')
        d.e(f'ri{i}', f'im{i}', f'rp{i}', 'l', 'r', '', 'res' if res else 'impl')
    d.v('im9', 1400, 674, 240, 90, 'Mock*Repository\nMismas interfaces; respaldo\nsi Firebase no se inicializa', 'ext', 'im', extra='fontSize=12;')
    d.v('xa', 1760, 170, 160, 48, 'Firebase\nAuthentication', 'light', 'ex', extra='fontSize=12;')
    d.v('xf', 1760, 226, 160, 272, 'Cloud Firestore\n\nusuarios/{uid}/…\nsolicitudes_atencion', 'db', 'ex', extra='fontSize=12;')
    d.v('xi', 1760, 506, 160, 48, 'Firebase AI Logic\n(Gemini)', 'light', 'ex', extra='fontSize=12;')
    d.v('xm', 1760, 562, 160, 48, 'Make (opcional)', 'opt', 'ex', extra='fontSize=12;')

    def gap(a, b, y, lab):
        d.e(None, a, b, 'r', 'l', lab, 'flow', f0=(y - Y0) / H0, f1=(y - Y0) / H0)
    gap('ui', 'vm', 450, 'eventos\ny estado')
    gap('vm', 'uc', 450, 'invoca')
    gap('uc', 'rp', 450, 'usa')
    ymid = lambda i: 170 + i * 56 + 24
    d.e('x0', 'im0', 'xa', 'r', 'l', '', 'blue')
    for i in range(1, 6):
        d.e(f'x{i}', f'im{i}', 'xf', 'r', 'l', '', 'blue', f1=(ymid(i) - 226) / 272)
    d.e('x6', 'im6', 'xi', 'r', 'l', '', 'blue', f1=(ymid(6) - 506) / 48)
    d.e('x7', 'im7', 'xm', 'r', 'l', '', 'opt', f1=(ymid(7) - 562) / 48)
    d.v('app', 40, 812, 1620, 62, 'AppContainer (core/di)  ·  Crea los repositorios (Firebase o Mock), ensambla los casos de uso y los entrega a MainViewModelFactory', 'dark')
    d.e('d1', 'app', 'vm2', 't', 'b', '', 'dep', f0=(480 - 40) / 1620, f1=.5)
    d.e('d2', 'app', 'im', 't', 'b', '', 'dep', f0=(1520 - 40) / 1620, f1=(1520 - 1380) / 280)
    d.legend(40, 905, 1900, [
        ('v', 'comp', 'Componente'), ('v', 'light', 'Caso de uso / servicio'), ('v', 'blue', 'ViewModel'),
        ('v', 'res', 'Reservado (sin implementar)'), ('v', 'opt', 'Opcional'), ('e', 'flow', 'Invoca / usa'),
        ('e', 'impl', 'Implementa interfaz'), ('e', 'blue', 'Acceso a servicio'), ('e', 'dep', 'Inyección de dependencias')])
    return d
