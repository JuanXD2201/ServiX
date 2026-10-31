package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.app.ServiXApp
import com.example.core.di.AppContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ServixRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    AppContainer.useMockRepositories()
  }

  @Test
  fun testAppLaunchAndDisplayHome() {
    composeTestRule.setContent {
      ServiXApp()
    }

    composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottom_nav_bar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nav_home").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nav_clientes").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nav_servicios").assertIsDisplayed()
  }

  @Test
  fun testNavigationToClientes() {
    composeTestRule.setContent {
      ServiXApp()
    }

    composeTestRule.onNodeWithTag("nav_clientes").performClick()
    composeTestRule.onNodeWithTag("clientes_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("add_client_fab").assertIsDisplayed()
  }

  @Test
  fun testNavigationToServicios() {
    composeTestRule.setContent {
      ServiXApp()
    }

    composeTestRule.onNodeWithTag("nav_servicios").performClick()
    composeTestRule.onNodeWithTag("servicios_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("add_service_fab").assertIsDisplayed()
  }

  @Test
  fun testNavigationToHistorial() {
    composeTestRule.setContent {
      ServiXApp()
    }

    composeTestRule.onNodeWithTag("nav_historial").performClick()
    composeTestRule.onNodeWithTag("historial_screen").assertIsDisplayed()
  }

  @Test
  fun testNavigationToPerfil() {
    composeTestRule.setContent {
      ServiXApp()
    }

    composeTestRule.onNodeWithTag("nav_perfil").performClick()
    composeTestRule.onNodeWithTag("perfil_screen").assertIsDisplayed()
  }
}
