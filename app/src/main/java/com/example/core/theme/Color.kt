package com.example.core.theme

import androidx.compose.ui.graphics.Color

// Paleta Oficial ServiX basada en la identidad del logotipo
// Colores principales extraídos directamente del isotipo oficial 3D:
val ServixBluePrimary = Color(0xFF0066F5)      // Azul Rey Eléctrico vibrante
val ServixBlueDark = Color(0xFF091E42)         // Azul Marino Profundo (Base del isotipo y palabra SERVI)
val ServixBlueDeep = Color(0xFF06142E)         // Azul Medianoche Ejecutivo
val ServixBlueSecondary = Color(0xFF00A3FF)    // Azul Cian Eléctrico (Cinta interna y la 'X')
val ServixBlueLight = Color(0xFF38BDF8)        // Celeste Tecnológico / Acentos de brillo
val ServixBlueVeryLight = Color(0xFFE0F2FE)    // Hielo Claro para contenedores
val ServixWhite = Color(0xFFFFFFFF)
val ServixBackground = Color(0xFFF8FAFC)       // Fondo blanco hielo moderno y limpio
val ServixTextPrimary = Color(0xFF0B1B36)      // Texto principal grafito azulado marino
val ServixTextSecondary = Color(0xFF475569)    // Texto secundario pizarra suave
val ServixBorder = Color(0xFFCBD5E1)           // Borde sutil neutro
val ServixBlueAccent = Color(0xFF00A3FF)       // Acento cian vibrante
val ServixBlueSurface = Color(0xFFF0F7FF)      // Superficie con tinte azulado premium

// Modo Oscuro
val ServixDarkBackground = Color(0xFF081226)
val ServixDarkSurface = Color(0xFF0D1B36)
val ServixDarkSurfaceVariant = Color(0xFF13254A)
val ServixDarkBorder = Color(0xFF1E3A5F)
val ServixDarkTextPrimary = Color(0xFFF8FAFC)
val ServixDarkTextSecondary = Color(0xFF94A3B8)
val ServixDarkBluePrimary = Color(0xFF38BDF8)
val ServixDarkBlueSecondary = Color(0xFF60A5FA)
val ServixDarkBlueContainer = Color(0xFF0A2554)

// Compatibilidad
val ServixTealPrimary = ServixBluePrimary
val ServixPurpleSecondary = ServixBlueSecondary

// Colores de Estados Operativos (modernos y acordes al estilo visual del logotipo)
object ServixStatusColors {
    val PendientePrimary = Color(0xFFF59E0B)
    val PendienteContainer = Color(0xFFFEF3C7)
    val PendienteText = Color(0xFFB45309)

    val EnProcesoPrimary = Color(0xFF0070F3)
    val EnProcesoContainer = Color(0xFFE0F2FE)
    val EnProcesoText = Color(0xFF0369A1)

    val CompletadoPrimary = Color(0xFF10B981)
    val CompletadoContainer = Color(0xFFD1FAE5)
    val CompletadoText = Color(0xFF065F46)

    val CanceladoPrimary = Color(0xFFEF4444)
    val CanceladoContainer = Color(0xFFFEE2E2)
    val CanceladoText = Color(0xFF991B1B)
}
