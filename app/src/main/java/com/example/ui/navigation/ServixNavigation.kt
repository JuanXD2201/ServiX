package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ServixScreen(
  val route: String,
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  data object Inicio : ServixScreen(
    route = "inicio",
    title = "Inicio",
    selectedIcon = Icons.Filled.Home,
    unselectedIcon = Icons.Outlined.Home
  )

  data object Clientes : ServixScreen(
    route = "clientes",
    title = "Clientes",
    selectedIcon = Icons.Filled.People,
    unselectedIcon = Icons.Outlined.People
  )

  data object Servicios : ServixScreen(
    route = "servicios",
    title = "Servicios",
    selectedIcon = Icons.Filled.Handyman,
    unselectedIcon = Icons.Outlined.Handyman
  )

  data object Historial : ServixScreen(
    route = "historial",
    title = "Historial",
    selectedIcon = Icons.Filled.History,
    unselectedIcon = Icons.Outlined.History
  )

  data object Perfil : ServixScreen(
    route = "perfil",
    title = "Perfil",
    selectedIcon = Icons.Filled.Person,
    unselectedIcon = Icons.Outlined.Person
  )

  companion object {
    val bottomNavItems = listOf(
      Inicio,
      Clientes,
      Servicios,
      Historial,
      Perfil
    )
  }
}
