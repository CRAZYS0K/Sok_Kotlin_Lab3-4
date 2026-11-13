package com.recipebook.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CardStyle(val container: Color, val border: Color)

val LightCardStyle = CardStyle(container = LightColors.surfaceContainerLowest, border = LightColors.outlineVariant)
val DarkCardStyle = CardStyle(container = DarkColors.surfaceContainer, border = Color.Transparent)

val LocalCardStyle = staticCompositionLocalOf { LightCardStyle }
