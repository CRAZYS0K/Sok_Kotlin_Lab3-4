package com.recipebook

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.recipebook.data.AppLanguage
import com.recipebook.data.ThemeMode
import com.recipebook.ui.settings.SettingsScreen
import com.recipebook.ui.theme.RecipeBookTheme

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val settings = (application as RecipeBookApplication).settings
        val configuration = resources.configuration
        val initialLanguage = settings.language
            ?: if (configuration.locales[0].language == AppLanguage.ENGLISH.tag) AppLanguage.ENGLISH else AppLanguage.RUSSIAN
        val isNight = (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val initialTheme = settings.themeMode ?: if (isNight) ThemeMode.DARK else ThemeMode.LIGHT

        setContent {
            RecipeBookTheme {
                var language by remember { mutableStateOf(initialLanguage) }
                var themeMode by remember { mutableStateOf(initialTheme) }
                SettingsScreen(
                    language = language,
                    themeMode = themeMode,
                    onLanguageSelect = {
                        language = it
                        settings.applyLanguage(it)
                    },
                    onThemeSelect = {
                        themeMode = it
                        settings.applyTheme(it)
                    },
                    onBack = ::finish,
                )
            }
        }
    }
}
