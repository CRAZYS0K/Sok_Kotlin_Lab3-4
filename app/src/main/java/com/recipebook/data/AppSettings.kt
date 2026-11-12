package com.recipebook.data

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat

enum class ThemeMode(val systemMode: Int, val appCompatMode: Int) {
    LIGHT(UiModeManager.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_NO),
    DARK(UiModeManager.MODE_NIGHT_YES, AppCompatDelegate.MODE_NIGHT_YES),
}

enum class AppLanguage(val tag: String) {
    RUSSIAN("ru"),
    ENGLISH("en"),
}

class AppSettings(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    val themeMode: ThemeMode?
        get() = prefs.getString(KEY_THEME, null)
            ?.let { saved -> ThemeMode.entries.find { it.name == saved } }

    val language: AppLanguage?
        get() {
            val tag = AppCompatDelegate.getApplicationLocales()[0]?.language ?: return null
            return AppLanguage.entries.find { it.tag == tag }
        }

    fun applyTheme(mode: ThemeMode) {
        prefs.edit { putString(KEY_THEME, mode.name) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            appContext.getSystemService(UiModeManager::class.java).setApplicationNightMode(mode.systemMode)
        } else {
            AppCompatDelegate.setDefaultNightMode(mode.appCompatMode)
        }
    }

    fun restoreTheme() {
        val mode = themeMode ?: return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            AppCompatDelegate.setDefaultNightMode(mode.appCompatMode)
        }
    }

    fun applyLanguage(language: AppLanguage) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.tag))
    }

    private companion object {
        const val KEY_THEME = "theme"
    }
}
