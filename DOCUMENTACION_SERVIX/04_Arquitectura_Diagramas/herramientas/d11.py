from lib import D


def build():
    d = D(11, '11_Flujo_CICD', 'Flujo de integración continua (CI/CD)',
          'Pull Request → política de rama main → pipeline ServiX-CI → publicación de resultados y APK debug', 1920, 1130)
    # fila 1
    d.v('s0', 60, 130, 250, 80, 'Desarrollador crea\nrama feature/*', 'pill')
    d.v('s1', 380, 130, 250, 80, 'Commit y push\nal repositorio', 'comp')
    d.v('s2', 700, 130, 250, 80, 'Abre Pull Request\nhacia main', 'comp')
    d.v('s3', 1020, 130, 300, 80, 'Política de rama main\n1 revisor mínimo +\nvalidación de build', 'light')
    d.e('f0', 's0', 's1', 'r', 'l')
    d.e('f1', 's1', 's2', 'r', 'l')
    d.e('f2', 's2', 's3', 'r', 'l')
    # pipeline
    d.v('pipe', 60, 290, 1800, 330, 'Pipeline ServiX-CI   ·   agente Microsoft-hosted ubuntu-latest   ·   JDK 21   ·   sin secretos', 'container')
    A = [('1  Configurar JDK 21\nJavaToolInstaller@0', 'comp'),
         ('2  Configurar Android SDK\nplatforms;android-36.1', 'comp'),
         ('3  Caché de Gradle\nCache@2', 'comp'),
         ('4  Permisos de gradlew\nchmod +x ./gradlew', 'comp'),
         ('5  Resolver dependencias\n:app:dependencies', 'comp')]
    for i, (t, k) in enumerate(A):
        d.v(f'a{i}', 90 + i * 360, 350, 300, 90, t, k, 'pipe')
    B = [('6  Pruebas unitarias\ntestDebugUnitTest', 'comp', 1530),
         ('7  Compilar APK debug\nassembleDebug', 'comp', 1170),
         ('8  Publicar resultados de pruebas\nPublishTestResults@2\ncondición: succeededOrFailed()', 'ok', 810),
         ('9  Publicar APK\nPublishBuildArtifacts@1\nartefacto servix-apk-debug', 'ok', 450)]
    for i, (t, k, x) in enumerate(B):
        d.v(f'b{i}', x, 490, 300, 90, t, k, 'pipe')
    for i in range(4):
        d.e(f'pa{i}', f'a{i}', f'a{i + 1}', 'r', 'l')
    d.e('pab', 'a4', 'b0', 'b', 't')
    for i in range(3):
        d.e(f'pb{i}', f'b{i}', f'b{i + 1}', 'l', 'r')
    d.e('trig', 's3', 'pipe', 'b', 't', 'dispara el pipeline\n(PR hacia main)', 'blue', f0=.5, f1=(1170 - 60) / 1800)
    # decisiones
    d.v('d1', 450, 670, 300, 130, '¿Pruebas y\ncompilación OK?', 'decision')
    d.v('d2', 860, 670, 300, 130, '¿Aprobado por\nel revisor?', 'decision')
    d.v('mg', 1260, 695, 250, 80, 'Completar PR:\nmerge a main', 'comp')
    d.v('tm', 1590, 695, 250, 80, 'Pipeline se ejecuta\nde nuevo en main', 'comp')
    d.v('end', 1590, 860, 250, 70, 'APK debug publicado\nservix-apk-debug', 'pill')
    d.v('fix', 450, 870, 300, 70, 'Corregir el código y\nvolver a hacer push', 'err')
    d.e('g0', 'b3', 'd1', 'b', 't')
    d.e('g1', 'd1', 'd2', 'r', 'l', 'Sí')
    d.e('g2', 'd2', 'mg', 'r', 'l', 'Sí')
    d.e('g3', 'mg', 'tm', 'r', 'l')
    d.e('g4', 'tm', 'end', 'b', 't')
    d.e('n1', 'd1', 'fix', 'b', 't', 'No', 'dep')
    d.e('n2', 'd2', 'fix', 'b', 'r', 'Cambios solicitados', 'dep')
    d.e('loop', 'fix', 's1', 'l', 'b', 'Reintento', 'dep', via=[(30, 905), (30, 250), (505, 250)], lx=-0.75)
    d.legend(40, 975, 1840, [
        ('v', 'pill', 'Inicio / fin'), ('v', 'comp', 'Actividad / paso de pipeline'), ('v', 'light', 'Política'),
        ('v', 'ok', 'Publicación de resultados'), ('v', 'decision', 'Decisión'), ('v', 'err', 'Corrección'),
        ('e', 'flow', 'Flujo'), ('e', 'blue', 'Disparador'), ('e', 'dep', 'Retorno / reintento')])
    return d
