package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = CyanAccent,
    secondary = PrimaryVariant,
    tertiary = CyanAccent,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = CardDark,
    onPrimary = SurfaceDark,
    onSecondary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark,
    onSurfaceVariant = TextDark.copy(alpha = 0.7f),
    error = ErrorRed
  )

private val LightColorScheme =
  lightColorScheme(
    primary = DeepBlue,
    secondary = PrimaryVariant,
    tertiary = DeepBlue,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = CardLight,
    onPrimary = SurfaceLight,
    onSecondary = TextLight,
    onBackground = TextLight,
    onSurface = TextLight,
    onSurfaceVariant = TextLight.copy(alpha = 0.7f),
    error = ErrorRed
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  amoledMode: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> {
        if (amoledMode) {
          DarkColorScheme.copy(
            background = androidx.compose.ui.graphics.Color.Black,
            surface = androidx.compose.ui.graphics.Color.Black,
            surfaceVariant = androidx.compose.ui.graphics.Color(0xFF0F0F0F)
          )
        } else {
          DarkColorScheme
        }
      }
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
