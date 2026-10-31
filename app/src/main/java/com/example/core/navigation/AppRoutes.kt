package com.example.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.ui.graphics.vector.ImageVector

object AppRoutes {
    const val HOME = "home"
    const val CLIENTES = "clientes"
    const val CLIENTE_DETALLE = "cliente_detalle"
    const val CLIENTE_DETALLE_ROUTE = "cliente_detalle/{clienteId}"
    const val SERVICIOS = "servicios"
    const val SERVICIO_DETALLE = "servicio_detalle"
    const val SERVICIO_DETALLE_ROUTE = "servicio_detalle/{servicioId}"
    const val HISTORIAL = "historial"
    const val CHAT = "chat"
    const val PERFIL = "perfil"
    const val AUTH = "auth"
    const val CLIENTE_PORTAL = "cliente_portal"

    fun clienteDetalle(clienteId: String): String = "$CLIENTE_DETALLE/$clienteId"
    fun servicioDetalle(servicioId: String): String = "$SERVICIO_DETALLE/$servicioId"
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    data object Home : BottomNavItem(AppRoutes.HOME, "Inicio", Icons.Default.Home, "nav_home")
    data object Clientes : BottomNavItem(AppRoutes.CLIENTES, "Clientes", Icons.Default.People, "nav_clientes")
    data object Servicios : BottomNavItem(AppRoutes.SERVICIOS, "Servicios", Icons.Default.Build, "nav_servicios")
    data object Historial : BottomNavItem(AppRoutes.HISTORIAL, "Historial", Icons.Default.History, "nav_historial")
    data object Chat : BottomNavItem(AppRoutes.CHAT, "Chat IA", Icons.Default.SmartToy, "nav_chat")
    data object Perfil : BottomNavItem(AppRoutes.PERFIL, "Perfil", Icons.Default.Person, "nav_perfil")

    companion object {
        val items = listOf(Home, Clientes, Servicios, Historial, Chat, Perfil)
    }
}
