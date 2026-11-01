from lib import D


def build():
    d = D(2, '02_Arquitectura_Capas', 'Arquitectura por capas',
          'Dependencias dirigidas hacia el dominio · Una sola actividad con Jetpack Compose y Material 3', 1700, 1040)
    X, W = 380, 900
    bx = [400, 695, 990]
    # Núcleo
    d.v('core', 40, 120, 290, 670, 'Núcleo  (core/)', 'container_ext')
    d.v('c1', 60, 170, 250, 120, 'AppContainer\nInyección manual de\ndependencias (sin Hilt)', 'dark')
    d.v('c2', 60, 310, 250, 100, 'theme\nColor · Theme · Type', 'ext')
    d.v('c3', 60, 430, 250, 100, 'navigation\nAppRoutes · BottomNavItem', 'ext')
    d.v('c4', 60, 550, 250, 120, 'utils\nValidators · Formatters\nImageUtils · MakeWebhookConfig', 'ext')
    d.v('c5', 60, 690, 250, 80, 'MainActivity · ServiXApplication\n(app/)', 'ext')
    # Presentación
    d.v('pres', X, 120, W, 190, 'Presentación  (presentation/ · ui/)', 'container')
    d.v('p1', bx[0], 170, 270, 120, 'Pantallas\nHome · Clientes · Servicios\nHistorial · Perfil · Chat\nClientePortal · AuthScreen', 'comp', 'pres')
    d.v('p2', bx[1], 170, 270, 120, 'Componentes y diálogos\nTarjetas, chips, stepper,\nformularios, búsqueda global,\ncentro de notificaciones', 'comp', 'pres')
    d.v('p3', bx[2], 170, 270, 120, 'ViewModel\nMainViewModel\nMainViewModelFactory\nAuthUiState', 'comp', 'pres')
    # Dominio
    d.v('dom', X, 360, W, 190, 'Dominio  (domain/)', 'container')
    d.v('d1', bx[0], 410, 270, 120, 'Casos de uso\nClientes · Servicios · Auth\nAtención · Notificaciones\nChat IA · Configuración', 'light', 'dom')
    d.v('d2', bx[1], 410, 270, 120, 'Interfaces de repositorio\nAuth · Cliente · Servicio\nNotificación · Actividad · Atención\nConfig · AiChat · MakeWebhook', 'light', 'dom')
    d.v('d3', bx[2], 410, 270, 120, 'Modelos\nCliente · Servicio · Notificacion\nActividad · SolicitudAtencion\nUserProfile · UserRole · enums', 'light', 'dom')
    # Datos
    d.v('dat', X, 600, W, 190, 'Datos  (data/)', 'container')
    d.v('t1', bx[0], 650, 270, 120, 'data/firebase\nFirebase*Repository\nDTO de Firestore\n(Auth y Firestore)', 'comp', 'dat')
    d.v('t2', bx[1], 650, 270, 120, 'data/ai · data/network\nGeminiAiChatRepository\nOkHttpMakeWebhookRepository', 'comp', 'dat')
    d.v('t3', bx[2], 650, 270, 120, 'data/mock\nRepositorios en memoria:\npruebas y respaldo si Firebase\nno se inicializa', 'ext', 'dat')
    # externos
    d.v('fbx', 1380, 600, 280, 110, 'Firebase\nAuthentication · Firestore\nAI Logic (Gemini)', 'light')
    d.v('mk', 1380, 727, 280, 64, 'Make (opcional)', 'opt')
    d.v('flujo', X, 828, W, 44, 'Flujo típico:  Screen → MainViewModel → UseCase → Repository (interfaz) → implementación Firebase / OkHttp', 'note')
    # aristas
    d.e('a1', 'pres', 'dom', 'b', 't', 'usa casos de uso', 'flow', f0=.3, f1=.3)
    d.e('a2', 'dat', 'dom', 't', 'b', 'implementa interfaces', 'impl', f0=.3, f1=.3)
    d.e('a3', 'core', 'dom', 'r', 'l', '', 'dep', f0=(455 - 120) / 670, f1=(455 - 360) / 190)
    d.e('a4', 'dat', 'fbx', 'r', 'l', 'SDK Firebase', 'blue', f0=(665 - 600) / 190, f1=(665 - 600) / 110)
    d.e('a5', 'dat', 'mk', 'r', 'l', 'Webhook POST', 'opt', f0=(759 - 600) / 190, f1=.5)
    d.legend(40, 900, 1620, [
        ('v', 'comp', 'Paquete / módulo'), ('v', 'light', 'Dominio / servicio externo'), ('v', 'ext', 'Soporte / respaldo'),
        ('v', 'opt', 'Integración opcional'), ('e', 'flow', 'Dependencia directa'),
        ('e', 'impl', 'Implementa interfaz'), ('e', 'dep', 'Ensamblaje (DI)')])
    return d
