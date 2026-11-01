from lib import D


def build():
    d = D(4, '04_Casos_Uso', 'Diagrama de casos de uso',
          'Actores Cliente y Administrador · Casos de uso comunes mediante el actor genérico Usuario', 1700, 1040)
    # actores
    d.v('usr', 835, 140, 60, 80, 'Usuario autenticado', 'actor', extra='verticalLabelPosition=top;verticalAlign=bottom;')
    d.v('cli', 110, 575, 60, 90, 'Cliente', 'actor')
    d.v('adm', 1500, 575, 60, 90, 'Administrador', 'actor')
    d.v('sys', 260, 250, 1180, 640, 'ServiX (App Android)', 'container', extra='align=left;spacingLeft=14;')
    S = 'sys'
    # comunes (fila superior)
    d.v('u1', 290, 320, 220, 64, 'Iniciar sesión', 'usecase', S)
    d.v('u2', 540, 320, 220, 64, 'Registrarse', 'usecase', S)
    d.v('u3', 980, 320, 220, 64, 'Recuperar contraseña', 'usecase', S)
    d.v('u4', 735, 438, 260, 64, 'Chatear con el asistente de IA', 'usecase', S)
    d.v('u5', 580, 608, 240, 64, 'Clasificar intención\ndel mensaje', 'usecase', S)
    d.v('u6', 910, 608, 240, 64, 'Escalar a administrador\n(solicitud de atención)', 'usecase', S)
    # cliente
    cl = ['Ver mis servicios', 'Consultar estado\nde una orden', 'Solicitar nuevo servicio', 'Solicitar atención humana']
    for i, t in enumerate(cl):
        d.v(f'c{i}', 290, 450 + i * 100, 250, 64, t, 'usecase', S)
    # administrador
    ad = ['Gestionar clientes', 'Gestionar servicios\n(aviso opcional a Make)', 'Consultar historial\ny notificaciones',
          'Atender solicitudes\nde atención', 'Configurar perfil y\nobjetivos semanales']
    for i, t in enumerate(ad):
        d.v(f'a{i}', 1190, 430 + i * 90, 240, 64, t, 'usecase_opt' if i == 1 and False else 'usecase', S)
    # generalización de actores
    d.e('g1', 'cli', 'usr', 't', 'l', '', 'gen', via=[(140, 180)])
    d.e('g2', 'adm', 'usr', 't', 'r', '', 'gen', via=[(1530, 180)])
    # usuario -> comunes
    for i, k in enumerate(['u1', 'u2', 'u3']):
        d.e(f'ua{i}', 'usr', k, 'b', 't', '', 'plain', mid=302)
    d.e('ua4', 'usr', 'u4', 'b', 't', '', 'plain')
    # asociaciones
    for i in range(4):
        d.e(f'ca{i}', 'cli', f'c{i}', 'r', 'l', '', 'plain', mid=230)
    for i in range(5):
        d.e(f'aa{i}', 'adm', f'a{i}', 'l', 'r', '', 'plain', mid=1470)
    # include / extend
    d.e('inc', 'u4', 'u5', 'l', 't', '«include»', 'dep')
    d.e('ext', 'u6', 'u4', 't', 'r', '«extend»', 'dep')
    d.v('n1', 580, 710, 570, 110, 'Escalamiento: solo si el usuario es CLIENTE y la intención es queja,\ncancelación, problema, error de la app o solicitud de atención humana.\n\nSi la consulta a Gemini falla, se muestra un error explícito:\nno se simulan respuestas de IA.', 'note', S)
    d.legend(40, 910, 1620, [
        ('v', 'usecase', 'Caso de uso'), ('e', 'plain', 'Asociación'), ('e', 'dep', '«include» / «extend»'),
        ('e', 'gen', 'Generalización de actor'), ('v', 'note', 'Nota')])
    return d
