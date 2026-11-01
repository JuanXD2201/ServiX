from lib import D, Seq


def build():
    H = 1990
    d = D(9, '09_Secuencia_Chat_IA', 'Secuencia: chat con asistente de IA',
          'Clasificación de intención, escalamiento a un administrador o consulta a Gemini vía Firebase AI Logic', 2000, H)
    cx = [140 + 245 * i for i in range(8)]
    s = Seq(d, [('U', 'Usuario', 'dark', cx[0]), ('V', 'ChatScreen /\nMainViewModel', 'comp', cx[1]),
                ('UC', 'SendAiChat-\nMessageUseCase', 'light', cx[2]),
                ('CS', 'CrearSolicitud-\nAtencionUseCase', 'light', cx[3]),
                ('RP', 'Repositorios Firebase\n(Atención · Notificación\n· Actividad)', 'comp', cx[4]),
                ('FS', 'Cloud Firestore', 'db', cx[5]),
                ('G', 'GeminiAiChat-\nRepository', 'comp', cx[6]),
                ('AI', 'Firebase AI Logic\ngemini-2.5-flash', 'light', cx[7])], bottom=1780, bw=200)
    s.bar('V', 240, 1745)
    s.bar('UC', 317, 1695)
    s.bar('CS', 520, 800)
    for y0, y1 in [(578, 640), (700, 780)]:
        s.bar('RP', y0, y1)
    for y0, y1 in [(600, 622), (722, 745)]:
        s.bar('FS', y0, y1)
    s.bar('G', 1107, 1590)
    s.bar('AI', 1180, 1560)
    s.frag(cx[1] - 60, cx[7] + 70, 440, 1670, 'alt  [escala: CLIENTE con queja, cancelación, problema, error de app o petición humana]',
           dividers=[(870, '[no escala]')])
    s.frag(cx[2] - 60, cx[7] + 55, 1050, 1665, 'alt  [consulta a Gemini]', dividers=[(1405, '[error o sin respuesta]')])
    s.msg('U', 'V', 250, 'Escribe un mensaje')
    s.msg('V', 'UC', 320, 'invoke(mensaje, historial,\nisClient, cliente, servicios)')
    s.selfmsg('UC', 372, 'clasificarIntencion(texto)\n→ IntencionUsuario')
    s.msg('UC', 'CS', 530, 'invoke(motivo,\nprioridad, mensaje)')
    s.msg('CS', 'RP', 585, 'crearSolicitud(…)')
    s.msg('RP', 'FS', 610, 'set solicitudes_atencion/{id}')
    s.msg('CS', 'RP', 705, 'logActividad(…) ·\naddNotificacion(SOLICITUD_ATENCION)')
    s.msg('RP', 'FS', 735, 'set …/actividades · …/notificaciones')
    s.msg('CS', 'UC', 790, 'SolicitudAtencion', 'ret')
    s.msg('UC', 'V', 840, 'AiChatOutcome(fueEscalado = true;\nradicado y prioridad)', 'ret')
    s.selfmsg('UC', 925, 'buildSystemContext: solo los datos\ndel propio cliente (o métricas del admin)')
    s.msg('UC', 'G', 1115, 'getAiResponse(mensaje,\nhistorial ≤ 6, contexto)')
    s.msg('G', 'AI', 1190, 'generateContent(…)')
    s.msg('AI', 'G', 1300, 'texto de la respuesta', 'ret')
    s.msg('G', 'UC', 1350, 'Result.success(texto)', 'ret')
    s.msg('UC', 'V', 1390, 'AiChatOutcome(texto)', 'ret')
    s.msg('AI', 'G', 1470, 'error / sin respuesta', 'ret')
    s.msg('G', 'UC', 1520, 'Result.failure', 'ret')
    s.msg('UC', 'V', 1600, 'Mensaje de error explícito\n(sin respuesta simulada)', 'ret')
    s.msg('V', 'U', 1705, 'Muestra la respuesta,\nel radicado o el error', 'ret')
    s.flush()
    d.v('nota', cx[1] - 100, 1800, 1350, 60, 'El administrador atiende las solicitudes desde su panel (EscalamientosAdminSection):\nPENDIENTE → EN_ATENCION → RESUELTO. Los escalamientos se crean solo para el rol CLIENTE.', 'note')
    d.legend(40, 1880, 1920, [
        ('v', 'light', 'Caso de uso'), ('v', 'bar', 'Activación'), ('e', 'msg', 'Llamada'),
        ('e', 'ret', 'Retorno'), ('v', 'frag', 'Fragmento alt')])
    return d
