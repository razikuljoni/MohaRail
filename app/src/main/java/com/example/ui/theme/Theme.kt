package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = BdRailGreenContainer,
    onPrimary = BdRailGreenDark,
    primaryContainer = BdRailGreenDark,
    onPrimaryContainer = BdRailGreenLight,
    secondary = BdRailOrangeAccent,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF4E342E),
    onSecondaryContainer = BdRailOrangeLight,
    tertiary = BdRailAmber,
    background = DarkSurface,
    onBackground = DarkTextPrimary,
    surface = DarkCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkBorder,
    onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = BdRailGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = BdRailGreenLight,
    onPrimaryContainer = BdRailGreenDark,
    secondary = BdRailOrangeAccent,
    onSecondary = Color.White,
    secondaryContainer = BdRailOrangeLight,
    onSecondaryContainer = Color(0xFFE65100),
    tertiary = BdRailBlueExpress,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceCardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFECEFF1),
    onSurfaceVariant = TextSecondaryLight
)

@Composable
fun MohaRailTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun TrainKothayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MohaRailTheme(darkTheme = darkTheme, content = content)
}
