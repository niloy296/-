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

private val DarkColorScheme =
  darkColorScheme(
    primary = BkashPinkLight,
    onPrimary = Color.White,
    primaryContainer = BkashPinkDark,
    onPrimaryContainer = Color(0xFFFFD8E4),
    secondary = IncomeGreenLight,
    onSecondary = Color(0xFF00381F),
    secondaryContainer = Color(0xFF005230),
    onSecondaryContainer = Color(0xFFB9F4D1),
    tertiary = CoinGold,
    background = DarkNavyBg,
    surface = DarkNavyCard,
    surfaceVariant = Color(0xFF2A374A),
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = Color(0xFFCBD5E1)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BkashPink,
    onPrimary = Color.White,
    primaryContainer = BkashPinkContainer,
    onPrimaryContainer = Color(0xFF3E001D),
    secondary = IncomeGreen,
    onSecondary = Color.White,
    secondaryContainer = IncomeGreenContainer,
    onSecondaryContainer = Color(0xFF002111),
    tertiary = CoinGold,
    background = LightBg,
    surface = LightSurface,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = Color(0xFF475569)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep signature bKash aesthetic
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

