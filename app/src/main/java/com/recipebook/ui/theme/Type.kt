package com.recipebook.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.recipebook.R

private fun variableFont(resId: Int, weight: FontWeight) =
    Font(resId, weight, variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)))

val Lora = FontFamily(
    variableFont(R.font.lora, FontWeight.Medium),
    variableFont(R.font.lora, FontWeight.SemiBold),
    variableFont(R.font.lora, FontWeight.Bold),
)

val Roboto = FontFamily(
    variableFont(R.font.roboto, FontWeight.Normal),
    variableFont(R.font.roboto, FontWeight.Medium),
    variableFont(R.font.roboto, FontWeight.SemiBold),
    variableFont(R.font.roboto, FontWeight.Bold),
)

private val Base = Typography()

val Typography = Typography(
    displayLarge = Base.displayLarge.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
    displayMedium = Base.displayMedium.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
    displaySmall = Base.displaySmall.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
    headlineLarge = Base.headlineLarge.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
    headlineMedium = Base.headlineMedium.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
    headlineSmall = Base.headlineSmall.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
    titleLarge = Base.titleLarge.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
    titleMedium = Base.titleMedium.copy(
        fontFamily = Lora,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    titleSmall = Base.titleSmall.copy(fontFamily = Roboto),
    bodyLarge = Base.bodyLarge.copy(fontFamily = Roboto),
    bodyMedium = Base.bodyMedium.copy(fontFamily = Roboto),
    bodySmall = Base.bodySmall.copy(fontFamily = Roboto),
    labelLarge = Base.labelLarge.copy(fontFamily = Roboto),
    labelMedium = Base.labelMedium.copy(fontFamily = Roboto),
    labelSmall = Base.labelSmall.copy(fontFamily = Roboto),
)
