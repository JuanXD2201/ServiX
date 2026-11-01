from lib import D, Seq


def build():
    H = 1480
    d = D(7, '07_Secuencia_Inicio_Sesion', 'Secuencia: inicio de sesión',
          'Validación del rol almacenado en Firestore frente al rol elegido en pantalla (rol mínimo: CLIENTE)', 1760, H)
    s = Seq(d, [('U', 'Usuario', 'dark', 150), ('V', 'AuthScreen /\nMainViewModel', 'comp', 440),
                ('UC', 'SignInUseCase', 'light', 730), ('R', 'FirebaseAuthRepository', 'comp', 1020),
                ('FA', 'Firebase\nAuthentication', 'light', 1310), ('FS', 'Cloud Firestore\nusuarios/{uid}', 'db', 1600)],
            bottom=1340)
    s.bar('V', 240, 1325)
    s.bar('UC', 322, 1262)
    s.bar('R', 582, 1175)
    s.bar('FA', 647, 722)
    s.bar('FA', 1010, 1040)
    s.bar('FS', 777, 857)
    s.frag('V', 'UC', 450, 530, 'alt  [datos inválidos]')
    s.frag('UC', 'FA', 975, 1200, 'alt  [requestedRole ≠ storedRole]', dividers=[(1105, '[roles coinciden]')])
    s.msg('U', 'V', 250, 'Elige rol (Cliente o\nAdministrador), correo y clave')
    s.msg('V', 'UC', 330, 'invoke(email, password,\nrequestedRole)')
    s.selfmsg('UC', 385, 'Valida: correo no vacío y\ncontraseña de 6+ caracteres')
    s.msg('UC', 'V', 505, 'AuthResult.Error(mensaje)', 'ret')
    s.msg('UC', 'R', 590, 'signIn(email, password,\nrequestedRole)')
    s.msg('R', 'FA', 655, 'signInWithEmailAndPassword')
    s.msg('FA', 'R', 715, 'Usuario autenticado (uid)', 'ret')
    s.msg('R', 'FS', 785, 'get usuarios/{uid}\n(campo role)')
    s.msg('FS', 'R', 850, 'role almacenado\n(ausente ⇒ CLIENTE)', 'ret')
    s.selfmsg('R', 905, 'storedRole =\nUserRole.fromString(role)')
    s.msg('R', 'FA', 1022, 'signOut()')
    s.msg('R', 'UC', 1095, 'AuthResult.Error\n«Cuenta registrada como X.\nSelecciona el perfil correcto»', 'ret')
    s.msg('R', 'UC', 1170, 'AuthResult.Success(\nAuthUser(role = storedRole))', 'ret')
    s.msg('UC', 'V', 1250, 'AuthResult', 'ret')
    s.msg('V', 'U', 1310, 'Muestra el error, o navega al\nportal del cliente / panel\nde administración', 'ret')
    s.flush()
    d.legend(40, 1352, 1680, [
        ('v', 'light', 'Componente de dominio / servicio'), ('v', 'bar', 'Activación'),
        ('e', 'msg', 'Llamada'), ('e', 'ret', 'Retorno'), ('v', 'frag', 'Fragmento alt')])
    return d
