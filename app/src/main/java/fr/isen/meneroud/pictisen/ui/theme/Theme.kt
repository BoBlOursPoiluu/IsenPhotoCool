package fr.isen.meneroud.pictisen.ui.theme

import android.app.Activity
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

// 🎨 Définition des couleurs principales
val DarkBackground = Color(0xFF1C1E1E)  // Fond sombre personnalisé
val DarkSurface = Color(0xFF363333)     // Fond des cartes
val VioletPrimary = Color(0xFF8A2BE2)   // Violet pour les accents et les ombres
val GrayText = Color(0xFFD3D3D3)        // Texte gris clair
val HighlightViolet = Color(0xFF9C27B0) // Violet plus clair pour les éléments sélectionnés

private val DarkColorScheme = darkColorScheme(
    primary = VioletPrimary,
    secondary = HighlightViolet,
    background = DarkBackground,  // Utilisation du fond personnalisé
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onSurface = GrayText
)

// 🎨 Définition du thème clair
private val LightColorScheme = lightColorScheme(
    primary = VioletPrimary,
    secondary = HighlightViolet,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onSurface = Color.Black
)

@Composable
fun PictIsenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Prise en charge des couleurs dynamiques (Android 12+)
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