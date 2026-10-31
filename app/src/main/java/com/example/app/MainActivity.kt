package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.presentation.viewmodel.MainViewModel
import com.example.presentation.viewmodel.MainViewModelFactory
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.domain.model.EstadoServicio
import com.example.domain.model.UserRole
import com.example.presentation.components.OnboardingView
import com.example.presentation.screens.ChatScreen
import com.example.presentation.screens.ClienteDetalleScreen
import com.example.presentation.screens.ClientePortalScreen
import com.example.presentation.screens.ClientesScreen
import com.example.presentation.screens.HistorialScreen
import com.example.presentation.screens.HomeScreen
import com.example.presentation.screens.PerfilScreen
import com.example.presentation.screens.ServicioDetalleScreen
import com.example.presentation.screens.ServiciosScreen
import com.example.ui.screens.AuthScreen
import com.example.core.di.AppContainer
import com.example.core.navigation.AppRoutes
import com.example.core.navigation.BottomNavItem
import com.example.core.theme.ServiXTheme
import com.example.core.theme.ServixBluePrimary
import com.google.firebase.FirebaseApp
import android.util.Log
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    try {
      if (FirebaseApp.getApps(this).isEmpty()) {
        FirebaseApp.initializeApp(this)
      }
      AppContainer.init(applicationContext)
    } catch (e: Exception) {
      Log.e("MainActivity", "Firebase initialization in MainActivity: ${e.message}", e)
      AppContainer.useMockRepositories()
    }
    setContent {
      ServiXApp()
    }
  }
}

@Composable
fun ServiXApp(viewModel: MainViewModel = viewModel(factory = MainViewModelFactory())) {
  val clientes by viewModel.clientes.collectAsState()
  val servicios by viewModel.servicios.collectAsState()
  val actividades by viewModel.actividades.collectAsState()
  val notificaciones by viewModel.notificaciones.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val appConfig by viewModel.appConfig.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val chatMessages by viewModel.chatMessages.collectAsState()
  val isChatLoading by viewModel.isChatLoading.collectAsState()
  val solicitudes by viewModel.solicitudes.collectAsState()
  val chatEstado by viewModel.chatEstado.collectAsState()

  val isCliente = currentUser?.role == UserRole.CLIENTE

  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination

  var serviceFilterStatus by remember { mutableStateOf<EstadoServicio?>(null) }
  var forceShowOnboarding by remember { mutableStateOf(false) }

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  val bottomNavItems = BottomNavItem.items

  val showBottomBar = !isCliente && bottomNavItems.any { it.route == currentDestination?.route }
  val isDark = appConfig.modoOscuroForzado || (appConfig.modoOscuro && isSystemInDarkTheme())

  ServiXTheme(darkTheme = isDark) {
    Surface(
      modifier = Modifier.fillMaxSize(),
      color = MaterialTheme.colorScheme.background
    ) {
      if (!appConfig.onboardingVisto || forceShowOnboarding) {
        OnboardingView(
          onComplete = {
            viewModel.marcarOnboardingVisto(true)
            forceShowOnboarding = false
          }
        )
      } else {
        Scaffold(
          contentWindowInsets = WindowInsets.safeDrawing,
          snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
          bottomBar = {
            if (showBottomBar) {
              NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav_bar")
              ) {
                bottomNavItems.forEach { item ->
                  val selected = currentDestination?.route == item.route
                  NavigationBarItem(
                    selected = selected,
                    onClick = {
                      if (item == BottomNavItem.Servicios) {
                        serviceFilterStatus = null
                      }
                      navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                          saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                      }
                    },
                    icon = {
                      val showBadge = item == BottomNavItem.Home && solicitudes.any { it.estado == com.example.domain.model.EstadoAtencion.PENDIENTE }
                      if (showBadge) {
                        BadgedBox(
                          badge = {
                            val count = solicitudes.count { it.estado == com.example.domain.model.EstadoAtencion.PENDIENTE }
                            Badge(containerColor = Color(0xFFEF4444), contentColor = Color.White) {
                              Text(if (count > 9) "9+" else count.toString())
                            }
                          }
                        ) {
                          Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                          )
                        }
                      } else {
                        Icon(
                          imageVector = item.icon,
                          contentDescription = item.title
                        )
                      }
                    },
                    label = { Text(item.title) },
                    colors = NavigationBarItemDefaults.colors(
                      selectedIconColor = ServixBluePrimary,
                      selectedTextColor = ServixBluePrimary,
                      indicatorColor = ServixBluePrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag(item.testTag)
                  )
                }
              }
            }
          }
        ) { innerPadding ->
        val isUserLoggedIn = currentUser != null
        LaunchedEffect(currentUser) {
          val currentRoute = navController.currentDestination?.route
          if (currentUser != null) {
            val targetRoute = if (currentUser?.role == UserRole.CLIENTE) AppRoutes.CLIENTE_PORTAL else BottomNavItem.Home.route
            if (currentRoute == AppRoutes.AUTH) {
              navController.navigate(targetRoute) {
                popUpTo(AppRoutes.AUTH) { inclusive = true }
              }
            }
          } else if (currentRoute != null && currentRoute != AppRoutes.AUTH) {
            navController.navigate(AppRoutes.AUTH) {
              popUpTo(0) { inclusive = true }
            }
          }
        }

        val startDest = when {
          currentUser == null -> AppRoutes.AUTH
          currentUser?.role == UserRole.CLIENTE -> AppRoutes.CLIENTE_PORTAL
          else -> BottomNavItem.Home.route
        }

        NavHost(
          navController = navController,
          startDestination = startDest,
          modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
              HomeScreen(
                clientes = clientes,
                servicios = servicios,
                actividades = actividades,
                notificaciones = notificaciones,
                solicitudes = solicitudes,
                userProfile = userProfile,
                appConfig = appConfig,
                onNavigateToClientes = {
                  navController.navigate(BottomNavItem.Clientes.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                  }
                },
                onNavigateToServicios = { filter ->
                  serviceFilterStatus = filter
                  navController.navigate(BottomNavItem.Servicios.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                  }
                },
                onNavigateToServiceDetail = { servicioId ->
                  navController.navigate(AppRoutes.servicioDetalle(servicioId))
                },
                onNavigateToClienteDetail = { clienteId ->
                  navController.navigate(AppRoutes.clienteDetalle(clienteId))
                },
                onNavigateToPerfil = {
                  navController.navigate(BottomNavItem.Perfil.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                  }
                },
                onNavigateToChat = {
                  navController.navigate(BottomNavItem.Chat.route)
                },
                onTomarSolicitud = { id ->
                  viewModel.tomarSolicitudAtencion(id)
                  scope.launch { snackbarHostState.showSnackbar("Caso asignado a tu bandeja.") }
                },
                onResponderSolicitud = { id, resp ->
                  viewModel.responderSolicitudAtencion(id, resp)
                  scope.launch { snackbarHostState.showSnackbar("Respuesta enviada al cliente.") }
                },
                onResolverSolicitud = { id, notas ->
                  viewModel.resolverSolicitudAtencion(id, notas)
                  scope.launch { snackbarHostState.showSnackbar("Solicitud resuelta con éxito.") }
                },
                onAddCliente = { nom, doc, tel, cor, dir, not ->
                  viewModel.addCliente(nom, doc, tel, cor, dir, not)
                  scope.launch { snackbarHostState.showSnackbar("Cliente \"$nom\" registrado con éxito") }
                },
                onAddServicio = { cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt ->
                  viewModel.addServicio(cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt)
                  scope.launch { snackbarHostState.showSnackbar("Servicio \"$nom\" creado con éxito") }
                },
                onUpdateServicio = {
                  viewModel.updateServicio(it)
                  scope.launch { snackbarHostState.showSnackbar("Servicio actualizado") }
                },
                onUpdateServicioEstado = { id, est ->
                  viewModel.updateServicioEstado(id, est)
                  scope.launch { snackbarHostState.showSnackbar("Estado cambiado a ${est.displayName}") }
                },
                onDeleteServicio = {
                  viewModel.deleteServicio(it)
                  scope.launch { snackbarHostState.showSnackbar("Servicio eliminado") }
                },
                onToggleClientFavorite = {
                  viewModel.toggleClienteFavorito(it)
                },
                onToggleServiceFavorite = {
                  viewModel.toggleServicioFavorito(it)
                },
                onDuplicateService = {
                  val nuevo = viewModel.duplicarServicio(it)
                  if (nuevo != null) {
                    scope.launch { snackbarHostState.showSnackbar("Servicio duplicado con éxito") }
                  }
                },
                onMarkNotificationRead = {
                  viewModel.marcarNotificacionLeida(it)
                },
                onMarkAllNotificationsRead = {
                  viewModel.marcarTodasNotificacionesLeidas()
                },
                onDeleteNotification = {
                  viewModel.deleteNotificacion(it)
                },
                onUpdateGoals = { srv, rev ->
                  viewModel.updateObjetivos(srv, rev)
                  scope.launch { snackbarHostState.showSnackbar("Metas semanales actualizadas") }
                }
              )
            }

            composable(BottomNavItem.Clientes.route) {
              ClientesScreen(
                clientes = clientes,
                servicios = servicios,
                onClientClick = { clienteId ->
                  navController.navigate(AppRoutes.clienteDetalle(clienteId))
                },
                onAddClient = { nom, doc, tel, cor, dir, not ->
                  viewModel.addCliente(nom, doc, tel, cor, dir, not)
                  scope.launch { snackbarHostState.showSnackbar("Cliente \"$nom\" agregado con éxito") }
                },
                onUpdateClient = {
                  viewModel.updateCliente(it)
                  scope.launch { snackbarHostState.showSnackbar("Cliente actualizado") }
                },
                onDeleteClient = {
                  viewModel.deleteCliente(it)
                  scope.launch { snackbarHostState.showSnackbar("Cliente eliminado") }
                },
                onToggleFavorite = {
                  viewModel.toggleClienteFavorito(it)
                },
                onAddService = { cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt ->
                  viewModel.addServicio(cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt)
                  scope.launch { snackbarHostState.showSnackbar("Servicio \"$nom\" creado con éxito") }
                }
              )
            }

            composable(BottomNavItem.Servicios.route) {
              ServiciosScreen(
                servicios = servicios,
                clientes = clientes,
                appConfig = appConfig,
                initialFilterStatus = serviceFilterStatus,
                onServiceClick = { servicioId ->
                  navController.navigate(AppRoutes.servicioDetalle(servicioId))
                },
                onAddServicio = { cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt ->
                  viewModel.addServicio(cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt)
                  scope.launch { snackbarHostState.showSnackbar("Servicio \"$nom\" registrado") }
                },
                onUpdateServicio = {
                  viewModel.updateServicio(it)
                  scope.launch { snackbarHostState.showSnackbar("Servicio actualizado") }
                },
                onUpdateServicioEstado = { id, est ->
                  viewModel.updateServicioEstado(id, est)
                  scope.launch { snackbarHostState.showSnackbar("Estado cambiado a ${est.displayName}") }
                },
                onDeleteServicio = {
                  viewModel.deleteServicio(it)
                  scope.launch { snackbarHostState.showSnackbar("Servicio eliminado") }
                },
                onToggleFavorite = {
                  viewModel.toggleServicioFavorito(it)
                },
                onDuplicateService = {
                  viewModel.duplicarServicio(it)
                  scope.launch { snackbarHostState.showSnackbar("Servicio duplicado con éxito") }
                },
                onNavigateToClienteDetail = { clienteId ->
                  navController.navigate(AppRoutes.clienteDetalle(clienteId))
                }
              )
            }

            composable(BottomNavItem.Historial.route) {
              HistorialScreen(
                servicios = servicios,
                clientes = clientes,
                appConfig = appConfig,
                onServiceClick = { servicioId ->
                  navController.navigate(AppRoutes.servicioDetalle(servicioId))
                },
                onUpdateServicioEstado = { id, est ->
                  viewModel.updateServicioEstado(id, est)
                  scope.launch { snackbarHostState.showSnackbar("Estado cambiado a ${est.displayName}") }
                },
                onDeleteServicio = {
                  viewModel.deleteServicio(it)
                  scope.launch { snackbarHostState.showSnackbar("Servicio eliminado") }
                }
              )
            }

            composable(BottomNavItem.Chat.route) {
              ChatScreen(
                messages = chatMessages,
                isLoading = isChatLoading,
                chatEstado = chatEstado,
                currentUserRole = currentUser?.role,
                onSendMessage = { viewModel.sendChatMessage(it) },
                onClearChat = { viewModel.clearChat() },
                onSolicitarAtencionHumana = { motivo, mensaje, prioridad ->
                  viewModel.solicitarAtencionHumana(motivo, mensaje, prioridad)
                  scope.launch { snackbarHostState.showSnackbar("Atención humana solicitada") }
                },
                onResolverChat = {
                  viewModel.clearChat()
                  scope.launch { snackbarHostState.showSnackbar("Atención finalizada") }
                }
              )
            }

            composable(BottomNavItem.Perfil.route) {
              PerfilScreen(
                userProfile = userProfile,
                appConfig = appConfig,
                onUpdateProfile = {
                  viewModel.updateProfile(it)
                  scope.launch { snackbarHostState.showSnackbar("Perfil profesional guardado") }
                },
                onUpdateConfig = {
                  viewModel.updateConfig(it)
                  scope.launch { snackbarHostState.showSnackbar("Preferencias guardadas") }
                },
                onResetData = {
                  viewModel.resetData()
                  scope.launch { snackbarHostState.showSnackbar("Datos reiniciados con éxito") }
                },
                onShowOnboarding = {
                  forceShowOnboarding = true
                },
                onSignOut = {
                  viewModel.signOut {
                    navController.navigate(AppRoutes.AUTH) {
                      popUpTo(0) { inclusive = true }
                    }
                  }
                },
                onTestMakeWebhook = {
                  viewModel.testMakeConnection { ok, msg ->
                    scope.launch { snackbarHostState.showSnackbar(msg) }
                  }
                }
              )
            }

            composable(AppRoutes.CLIENTE_PORTAL) {
              ClientePortalScreen(
                currentUser = currentUser,
                allServicios = servicios,
                allClientes = clientes,
                solicitudes = solicitudes,
                chatMessages = chatMessages,
                chatEstado = chatEstado,
                isChatLoading = isChatLoading,
                onSendMessage = { viewModel.sendChatMessage(it) },
                onClearChat = { viewModel.clearChat() },
                onSolicitarAtencionHumana = { motivo, mensaje, prioridad ->
                  viewModel.solicitarAtencionHumana(motivo, mensaje, prioridad)
                  scope.launch { snackbarHostState.showSnackbar("Solicitud radicada con éxito") }
                },
                onSolicitarServicioCliente = { nom, cat, desc, pri, fec, obs ->
                  viewModel.solicitarServicioCliente(nom, cat, desc, pri, fec, obs) {
                    scope.launch { snackbarHostState.showSnackbar("¡Orden solicitada con éxito!") }
                  }
                },
                onSignOut = {
                  viewModel.signOut {
                    navController.navigate(AppRoutes.AUTH) {
                      popUpTo(0) { inclusive = true }
                    }
                  }
                }
              )
            }

            composable(AppRoutes.AUTH) {
              AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                  val target = if (viewModel.currentUser.value?.role == UserRole.CLIENTE) {
                    AppRoutes.CLIENTE_PORTAL
                  } else {
                    BottomNavItem.Home.route
                  }
                  navController.navigate(target) {
                    popUpTo(AppRoutes.AUTH) { inclusive = true }
                  }
                }
              )
            }

            composable(
              route = AppRoutes.CLIENTE_DETALLE_ROUTE,
              arguments = listOf(navArgument("clienteId") { type = NavType.StringType })
            ) { backStackEntry ->
              val clienteId = backStackEntry.arguments?.getString("clienteId") ?: ""
              val cliente = clientes.find { it.clienteId == clienteId }
              ClienteDetalleScreen(
                cliente = cliente,
                servicios = servicios,
                allClientes = clientes,
                appConfig = appConfig,
                onBack = { navController.popBackStack() },
                onNavigateToServiceDetail = { servicioId ->
                  navController.navigate(AppRoutes.servicioDetalle(servicioId))
                },
                onUpdateCliente = {
                  viewModel.updateCliente(it)
                  scope.launch { snackbarHostState.showSnackbar("Cliente actualizado") }
                },
                onDeleteCliente = {
                  viewModel.deleteCliente(it)
                  scope.launch { snackbarHostState.showSnackbar("Cliente y servicios eliminados") }
                },
                onToggleFavorite = {
                  viewModel.toggleClienteFavorito(it)
                },
                onToggleServiceFavorite = {
                  viewModel.toggleServicioFavorito(it)
                },
                onDuplicateService = {
                  viewModel.duplicarServicio(it)
                  scope.launch { snackbarHostState.showSnackbar("Servicio duplicado con éxito") }
                },
                onAddServicio = { cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt ->
                  viewModel.addServicio(cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt)
                  scope.launch { snackbarHostState.showSnackbar("Servicio asignado a cliente") }
                },
                onUpdateServicio = {
                  viewModel.updateServicio(it)
                  scope.launch { snackbarHostState.showSnackbar("Servicio actualizado") }
                },
                onUpdateServicioEstado = { id, est ->
                  viewModel.updateServicioEstado(id, est)
                  scope.launch { snackbarHostState.showSnackbar("Estado cambiado a ${est.displayName}") }
                },
                onDeleteServicio = {
                  viewModel.deleteServicio(it)
                  scope.launch { snackbarHostState.showSnackbar("Servicio eliminado") }
                }
              )
            }

            composable(
              route = AppRoutes.SERVICIO_DETALLE_ROUTE,
              arguments = listOf(navArgument("servicioId") { type = NavType.StringType })
            ) { backStackEntry ->
              val servicioId = backStackEntry.arguments?.getString("servicioId") ?: ""
              val servicio = servicios.find { it.servicioId == servicioId }
              val cliente = clientes.find { it.clienteId == servicio?.clienteId }
              ServicioDetalleScreen(
                servicio = servicio,
                cliente = cliente,
                allClientes = clientes,
                appConfig = appConfig,
                onBack = { navController.popBackStack() },
                onNavigateToClient = { cliId ->
                  navController.navigate(AppRoutes.clienteDetalle(cliId))
                },
                onUpdateServicio = {
                  viewModel.updateServicio(it)
                  scope.launch { snackbarHostState.showSnackbar("Servicio actualizado") }
                },
                onUpdateServicioEstado = { id, est ->
                  viewModel.updateServicioEstado(id, est)
                  scope.launch { snackbarHostState.showSnackbar("Estado cambiado a ${est.displayName}") }
                },
                onDeleteServicio = {
                  viewModel.deleteServicio(it)
                  scope.launch { snackbarHostState.showSnackbar("Servicio eliminado") }
                },
                onToggleFavorite = {
                  viewModel.toggleServicioFavorito(it)
                },
                onDuplicateService = {
                  viewModel.duplicarServicio(it)
                  scope.launch { snackbarHostState.showSnackbar("Servicio duplicado con éxito") }
                },
                onSendMakeReport = { srvId ->
                  viewModel.sendMakeReport(srvId) { ok, msg ->
                    scope.launch { snackbarHostState.showSnackbar(msg) }
                  }
                }
              )
            }
          }
        }
      }
    }
  }
}
