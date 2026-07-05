package com.example.guesstheword.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkPrimaryDark,
    tertiary = DarkSuccess,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color(0xFF121212),
    onSecondary = Color.White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    outline = DarkBorderColor
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = PrimaryDark,
    tertiary = Success,
    background = Color.White,
    surface = Color(0xFFF9FAFB),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = BorderColor
)

@Composable
fun GuessTheWordTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color by default for game branding
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
