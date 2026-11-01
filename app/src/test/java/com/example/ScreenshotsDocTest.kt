package com.example

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.app.ServiXApp
import com.example.core.di.AppContainer
import com.example.domain.model.UserRole
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Genera capturas de documentacion con repositorios mock (datos ficticios). */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class ScreenshotsDocTest {

  @get:Rule val rule = createComposeRule()

  @Before
  fun setup() {
    AppContainer.useMockRepositories()
  }

  private fun shot(name: String) {
    rule.waitForIdle()
    rule.onRoot().captureRoboImage(filePath = "src/test/screenshots/doc/$name.png")
  }

  private fun tap(tag: String, scroll: Boolean = false) {
    val node = rule.onNodeWithTag(tag)
    if (scroll) node.performScrollTo()
    node.performClick()
    rule.waitForIdle()
  }

  private fun tagPrefix(p: String) = SemanticsMatcher("tag starts with $p") {
    it.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(p) == true
  }

  private fun startAdmin() = rule.setContent { ServiXApp() }

  private fun startLoggedOut() {
    runBlocking { AppContainer.authRepository.signOut() }
    rule.setContent { ServiXApp() }
  }

  private fun startCliente() {
    runBlocking {
      AppContainer.authRepository.signIn("mariana.rios@example.com", "demo1234", UserRole.CLIENTE)
    }
    rule.setContent { ServiXApp() }
  }

  @Test fun login() { startLoggedOut(); shot("01_login") }

  @Test fun registro() {
    startLoggedOut()
    tap("auth_toggle_mode_button", scroll = true)
    shot("02_registro")
  }

  @Test fun home() { startAdmin(); shot("03_home") }

  @Test fun clientes() { startAdmin(); tap("nav_clientes"); shot("04_clientes") }

  @Test fun clienteDetalle() {
    startAdmin(); tap("nav_clientes")
    rule.onAllNodes(tagPrefix("client_card_"))[0].performClick(); rule.waitForIdle()
    shot("05_cliente_detalle")
  }

  @Test fun servicios() { startAdmin(); tap("nav_servicios"); shot("06_servicios") }

  @Test fun servicioDetalle() {
    startAdmin(); tap("nav_servicios"); tap("service_card_srv-101", scroll = true)
    shot("07_servicio_detalle")
  }

  @Test fun historial() { startAdmin(); tap("nav_historial"); shot("08_historial") }

  @Test fun perfil() { startAdmin(); tap("nav_perfil"); shot("09_perfil") }

  @Test fun chatAdmin() { startAdmin(); tap("nav_chat"); shot("10_chat_ia_admin") }

  @Test fun chatCliente() {
    startCliente(); tap("tab_chat_ia"); shot("11_chat_ia_cliente")
  }

  @Test fun notificaciones() {
    startAdmin(); tap("header_notification_button"); shot("12_notificaciones")
  }

  @Test fun nuevoCliente() {
    startAdmin(); tap("nav_clientes"); tap("add_client_fab"); shot("13_nuevo_cliente")
  }

  @Test fun nuevoServicio() {
    startAdmin(); tap("nav_servicios"); tap("add_service_fab"); shot("14_nuevo_servicio")
  }

  @Test fun portalCliente() { startCliente(); shot("15_portal_cliente") }

  @Test fun escalamientosAdmin() {
    startAdmin()
    rule.onNodeWithTag("escalamientos_admin_section").performScrollTo()
    shot("16_escalamientos_admin")
  }
}
