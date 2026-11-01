from lib import D


def build():
    d = D(1, '01_Contexto', 'Contexto del sistema',
          'Actores, sistema ServiX y servicios externos con los que interactúa', 1700, 1000)
    # actores
    d.v('cli', 90, 295, 60, 90, 'Cliente', 'actor')
    d.v('adm', 90, 515, 60, 90, 'Administrador', 'actor')
    # sistema
    d.v('app', 480, 250, 340, 400, 'ServiX (App Android)', 'container_dark')
    d.t('app_t', 500, 296, 300, 330,
        'Kotlin · Jetpack Compose · Material 3\n\n'
        '•  Autenticación con dos roles\n   (CLIENTE y ADMINISTRADOR)\n'
        '•  Gestión de clientes y servicios\n'
        '•  Historial y notificaciones\n'
        '•  Chat con asistente de IA\n'
        '•  Solicitudes de atención humana',
        13, '#091E42', False, 'left', 'top', parent='app')
    # firebase
    d.v('fb', 1120, 250, 520, 400, 'Google Firebase (proyecto de ServiX)', 'container')
    d.v('auth', 1160, 300, 440, 96, 'Firebase Authentication\nCorreo y contraseña', 'light')
    d.v('fs', 1160, 416, 440, 96, 'Cloud Firestore\nusuarios/{uid}/… y solicitudes_atencion\nProtegido por firestore.rules', 'db')
    d.v('ai', 1160, 532, 440, 96, 'Firebase AI Logic\nGemini Developer API · gemini-2.5-flash', 'light')
    # make / reservado
    d.v('make', 480, 116, 340, 74, 'Make (opcional)\nEscenario receptor del webhook', 'opt')
    d.v('ret', 480, 730, 340, 80, 'Retell AI · n8n  (reservado)\nInterfaces definidas, sin implementación', 'res')
    # dev
    d.v('dev', 1120, 700, 520, 120, 'Desarrollo y entrega', 'container_ext')
    d.v('gh', 1150, 746, 220, 58, 'GitHub\nRepositorio de código', 'ext')
    d.v('az', 1400, 746, 220, 58, 'Azure DevOps\nRepos + Pipelines (CI)', 'ext')
    # aristas
    d.e('e1', 'cli', 'app', 'r', 'l', 'Inicia sesión, consulta sus\nservicios y usa el chat', f1=(340 - 250) / 400)
    d.e('e2', 'adm', 'app', 'r', 'l', 'Gestiona clientes y servicios,\natiende escalamientos', f1=(560 - 250) / 400)
    d.e('e3', 'app', 'auth', 'r', 'l', 'Inicia sesión y\nvalida el rol', f0=(348 - 250) / 400)
    d.e('e4', 'app', 'fs', 'r', 'l', 'Lee y escribe datos\n(en tiempo real)', f0=(464 - 250) / 400)
    d.e('e5', 'app', 'ai', 'r', 'l', 'Consultas del\nchat de IA', f0=(580 - 250) / 400)
    d.e('e6', 'app', 'make', 't', 'b', 'Evento de servicio (webhook HTTPS)', 'opt')
    d.e('e7', 'app', 'ret', 'b', 't', 'Sin implementar', 'res')
    d.e('e8', 'gh', 'az', 'r', 'l', '', 'flow')
    d.legend(40, 864, 1620, [
        ('v', 'dark', 'Sistema ServiX'), ('v', 'light', 'Servicio Firebase'),
        ('v', 'opt', 'Integración opcional'), ('v', 'res', 'Reservado (no implementado)'),
        ('v', 'ext', 'Herramienta de desarrollo'), ('e', 'flow', 'Interacción'),
        ('e', 'opt', 'Webhook opcional'), ('e', 'res', 'Interfaz reservada')])
    return d
