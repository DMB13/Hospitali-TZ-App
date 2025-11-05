package com.labtrace.tz.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColorPalette = lightColors(
    primary = 0xFF1976D2.toInt(), // Blue
    primaryVariant = 0xFF0D47A1.toInt(),
    secondary = 0xFF4CAF50.toInt() // Green
)

@Composable
fun LabTraceTZTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        typography = androidx.compose.material.Typography(),
        shapes = androidx.compose.material.Shapes(),
        content = content
    )
}
