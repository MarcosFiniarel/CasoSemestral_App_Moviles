package com.example.casosemestral.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/*
TONOS PURPURAS
// 1. TEMA OSCURO (Inverso profundo)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD4C5F9),         // Lila suave para destacar en oscuro
    onPrimary = Color(0xFF2C194D),       // Texto oscuro sobre el primario
    primaryContainer = Color(0xFF43306B),// Púrpura medio para contenedores (Snackbar de éxito)
    onPrimaryContainer = Color(0xFFEFE8FF), // Texto claro para el contenedor

    secondary = Color(0xFFE3D8C8),       // Beige crema suave
    tertiary = Color(0xFFC7B1E3),        // Lila apagado complementario

    background = Color(0xFF1E162B),      // Fondo general púrpura/negro muy profundo
    surface = Color(0xFF281F38),         // Superficie de tarjetas y contenedores
    onBackground = Color(0xFFEFECE6),    // Texto general crema claro
    onSurface = Color(0xFFEFECE6),       // Texto en superficies

    // ATRIBUTOS DE ERROR/ELIMINAR (Para botones de borrado y textos de validación)
    error = Color(0xFFF2B8B5),           // Tono púrpura/rosa claro vibrante para destacar en fondo oscuro
    onError = Color(0xFF3F1D38),         // Texto oscuro sobre el color de error
    errorContainer = Color(0xFF3F1D38),  // Contenedor púrpura muy oscuro/vino
    onErrorContainer = Color(0xFFF7D6EE) // Texto lila claro para el contenedor de error
)

// 2. TEMA CLARO (Tonos crema, beige y púrpuras oscuros)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5B3E88),         // Púrpura oscuro elegante para botones principales y barras
    onPrimary = Color(0xFFFFFFFF),       // Texto blanco sobre el púrpura
    primaryContainer = Color(0xFFE4D5F2),// Lila crema suave para contenedores (Snackbar de éxito)
    onPrimaryContainer = Color(0xFF251342), // Texto púrpura muy oscuro

    secondary = Color(0xFF9E8A78),       // Beige oscuro / topo
    tertiary = Color(0xFF745388),        // Púrpura intermedio

    background = Color(0xFFF9F6F0),      // Fondo crema cálido
    surface = Color(0xFFFFFFFF),         // Superficie blanca/crema para tarjetas
    onBackground = Color(0xFF2C241D),    // Texto general
    onSurface = Color(0xFF2C241D),       // Texto en superficies

    // ATRIBUTOS DE ERROR/ELIMINAR (Para validaciones de formularios y botones de eliminar)
    error = Color(0xFF6A1B4D),           // Púrpura/vino profundo para destacar bordes y textos de campos faltantes
    onError = Color(0xFFFFFFFF),         // Texto blanco sobre el color de error
    errorContainer = Color(0xFF3D2C4C),  // Fondo del Snackbar o tarjetas de error
    onErrorContainer = Color(0xFFF0E6FF) // Texto claro sobre el contenedor de error
)*/

// TONOS BEIGE
// 1. TEMA OSCURO
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD8C7B5),         // Beige claro suave para destacar en los fondos oscuros
    onPrimary = Color(0xFF2C241E),       // Texto oscuro sobre el primario
    primaryContainer = Color(0xFF45382E),// Contenedor café medio (Snackbar de éxito)
    onPrimaryContainer = Color(0xFFF4ECE1), // Texto claro para el contenedor

    secondary = Color(0xFFB5A493),       // Taupe / beige apagado
    tertiary = Color(0xFF8F7E6D),        // Acento neutro intermedio

    background = Color(0xFF191614),      // Fondo general café/negro ultra oscuro y suave (descanso visual)
    surface = Color(0xFF24201D),         // Superficie de tarjetas y contenedores
    onBackground = Color(0xFFE6DFD5),    // Texto general crema suave
    onSurface = Color(0xFFE6DFD5),       // Texto en superficies

    error = Color(0xFFE2B2A6),           // Tono terracota suave para errores en oscuro
    onError = Color(0xFF421D16),         // Texto oscuro sobre error
    errorContainer = Color(0xFF4A2520),  // Contenedor de error oscuro
    onErrorContainer = Color(0xFFF7D8D2) // Texto claro para error
)

// 2. TEMA CLARO
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5E4E42),         // Beige oscuro / topo elegante para botones y barras
    onPrimary = Color(0xFFFFFFFF),       // Texto blanco sobre el beige oscuro
    primaryContainer = Color(0xFFEADFD3),// Crema tostado suave para contenedores (Snackbar de éxito)
    onPrimaryContainer = Color(0xFF231A13), // Texto café muy oscuro para excelente lectura

    secondary = Color(0xFF8A796B),       // Taupe intermedio
    tertiary = Color(0xFF706155),        // Neutro complementario

    background = Color(0xFFF5EFE6),      // Fondo general color crema cálido y profundo (cero brillos molestos)
    surface = Color(0xFFFAF6F0),         // Superficie de tarjetas ligeramente más clara
    onBackground = Color(0xFF332B25),    // Texto general café oscuro muy legible
    onSurface = Color(0xFF332B25),       // Texto en superficies

    error = Color(0xFF8C4A40),           // Terracota / vino cálido para validaciones y bordes de error
    onError = Color(0xFFFFFFFF),         // Texto blanco sobre error
    errorContainer = Color(0xFFD9B9B3),  // Contenedor crema-terracota suave para Snackbar de error/borrar
    onErrorContainer = Color(0xFF3B120D) // Texto oscuro para el contenedor de error
)

@Composable
fun CasoSemestralTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}