package com.example

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.app.MainActivity
import com.example.core.di.AppContainer
import com.example.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ServiX", appName)
  }

  @Test
  fun `main activity launches without crash`() {
    val scenario = ActivityScenario.launch(MainActivity::class.java)
    scenario.onActivity { activity ->
      assertNotNull(activity)
      assertNotNull(AppContainer.authRepository)
      assertNotNull(AppContainer.clienteRepository)
      assertNotNull(AppContainer.servicioRepository)
    }
    scenario.close()
  }
}
