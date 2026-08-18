package com.qibla.compass.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF81C784),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1B5E20),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF64B5F6),
    onTertiary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    outline = Color(0xFF8A8A8F)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF388E3C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = Color.Black,
    tertiary = Color(0xFF1565C0),
    onTertiary = Color.White,
    background = Color(0xFFF5F5F5),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = Color(0xFFB00020),
    onError = Color.White,
    outline = Color(0xFF757575)
)

@Composable
fun provideColorScheme(isDark: Boolean) = if (isDark) DarkColorScheme else LightColorScheme