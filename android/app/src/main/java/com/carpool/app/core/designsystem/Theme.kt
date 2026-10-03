package com.carpool.app.core.designsystem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
val Forest = Color(0xFF1E5144)
val Lime = Color(0xFFD3EB94)
val Ink = Color(0xFF172B25)
val Muted = Color(0xFF728078)
val Paper = Color(0xFFF7F8F2)
@Composable fun CarPoolTheme(content: @Composable () -> Unit) {
 MaterialTheme(colorScheme = lightColorScheme(primary = Forest, onPrimary = Color.White,
 secondary = Lime, onSecondary = Ink, background = Paper, surface = Color.White,
 onBackground = Ink, onSurface = Ink, outline = Color(0xFFDAE1D9)), content = content)
}
