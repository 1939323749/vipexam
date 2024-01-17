package app.xlei.vipexam.core.data.util

import android.content.Context
import android.content.res.Configuration
import app.xlei.vipexam.core.data.R
import app.xlei.vipexam.core.database.module.Language
import java.util.Locale


object LocaleHelper {
    fun updateLanguage(context: Context) {
        val langPref = Preferences.get(Preferences.appLanguageKey, "")
        val locale = when {
            langPref.isEmpty() -> Locale.getDefault()
            langPref.contains("-") -> Locale(
                langPref.substringBefore("-"),
                langPref.substringAfter("r")
            )

            else -> Locale(langPref)
        }
        updateResources(context, locale)
    }

    private fun updateResources(context: Context, locale: Locale) {
        context.resources.apply {
            val config = Configuration(configuration)

            context.createConfigurationContext(configuration)
            Locale.setDefault(locale)
            config.setLocale(locale)

            @Suppress("DEPRECATION")
            updateConfiguration(config, displayMetrics)
        }
    }

    fun getLanguages(context: Context) = listOf(
        Language("en", "English"),
        Language("zh-rCN", "Chinese (Simplified)")
    ).sortedBy { it.name }.toMutableList()
        .apply {
            add(0, Language("", context.getString(R.string.system)))
        }
}