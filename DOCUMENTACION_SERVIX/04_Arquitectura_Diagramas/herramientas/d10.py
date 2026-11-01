from lib import D


def build():
    d = D(10, '10_Despliegue', 'Diagrama de despliegue',
          'Dispositivo Android, servicios en la nube, integración opcional y cadena de entrega en Azure DevOps', 1800, 1040)
    # Firebase
    d.v('fb', 40, 120, 560, 330, 'Google Firebase  «entorno en la nube»', 'container')
    d.v('auth', 70, 170, 500, 70, 'Firebase Authentication\nCorreo y contraseña', 'light', 'fb')
    d.v('fs', 70, 258, 500, 96, 'Cloud Firestore\nusuarios/{uid}/…  ·  solicitudes_atencion\nReglas desplegadas: firestore.rules', 'db', 'fb')
    d.v('ai', 70, 372, 500, 62, 'Firebase AI Logic  →  Gemini Developer API\nModelo gemini-2.5-flash', 'light', 'fb')
    # Make y reservado
    d.v('make', 760, 120, 340, 100, 'Make (opcional)\nEscenario receptor del webhook HTTPS', 'opt')
    d.v('res', 760, 250, 340, 70, 'Retell AI · n8n  (reservado)\nSin despliegue ni implementación', 'res')
    # Dispositivo
    d.v('dev', 1360, 120, 400, 320, 'Dispositivo Android  «device»', 'container')
    d.v('apk', 1380, 170, 360, 70, 'ServiX  (APK debug)\ncom.example.servix', 'dark', 'dev')
    d.v('ui', 1380, 255, 360, 70, 'Jetpack Compose · MainViewModel\nCasos de uso · repositorios', 'comp', 'dev')
    d.v('sdk', 1380, 340, 360, 70, 'SDK de Firebase · OkHttp\n(comunicación HTTPS)', 'comp', 'dev')
    # Desarrollo
    d.v('pc', 40, 680, 180, 90, 'Equipo de\ndesarrollo\nAndroid Studio · Git', 'ext')
    d.v('gh', 300, 680, 200, 90, 'GitHub\nRepositorio\nmain · develop · feature/*', 'ext')
    d.v('az', 600, 620, 1160, 220, 'Azure DevOps  «servicio en la nube»', 'container_ext')
    d.v('rp', 630, 680, 230, 100, 'Azure Repos\nmain con política:\n1 revisor mínimo', 'comp', 'az')
    d.v('pl', 920, 680, 230, 100, 'Azure Pipelines\nServiX-CI\n(azure-pipelines.yml)', 'comp', 'az')
    d.v('ag', 1210, 680, 230, 100, 'Agente alojado\nubuntu-latest · JDK 21\nAndroid SDK 36.1 · Gradle', 'light', 'az')
    d.v('ar', 1500, 680, 230, 100, 'Artefacto servix-apk-debug\n+ resultados de pruebas', 'ok', 'az')
    # aristas
    yd = lambda y: (y - 120) / 320
    d.e('e1', 'dev', 'fb', 'l', 'r', 'HTTPS · SDK de Firebase', 'blue', f0=yd(344), f1=(344 - 120) / 330,
        extra='startArrow=block;startFill=1;')
    d.e('e2', 'dev', 'make', 'l', 'r', 'POST webhook\n(solo si hay URL)', 'opt', f0=yd(170), f1=.5)
    d.e('e3', 'pc', 'gh', 'r', 'l', 'push', 'flow')
    d.e('e4', 'gh', 'az', 'r', 'l', 'importación\no push', 'flow', f0=.5, f1=(725 - 620) / 220)
    d.e('e5', 'rp', 'pl', 'r', 'l', 'dispara', 'flow')
    d.e('e6', 'pl', 'ag', 'r', 'l', 'ejecuta', 'flow')
    d.e('e7', 'ag', 'ar', 'r', 'l', 'publica', 'flow')
    d.e('e8', 'ar', 'dev', 't', 'b', 'instalación manual\ndel APK debug', 'dep', f0=(1620 - 1500) / 230, f1=(1620 - 1360) / 400)
    d.legend(40, 880, 1720, [
        ('v', 'light', 'Servicio / entorno de ejecución'), ('v', 'dark', 'Aplicación desplegada'),
        ('v', 'opt', 'Integración opcional'), ('v', 'res', 'Reservado (no implementado)'),
        ('v', 'ext', 'Herramienta de desarrollo'), ('v', 'ok', 'Artefacto publicado'),
        ('e', 'blue', 'Comunicación en ejecución'), ('e', 'flow', 'Flujo de entrega'), ('e', 'dep', 'Acción manual')])
    return d
