package app.xlei.vipexam.util

import android.content.Context
import android.content.res.Configuration
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.xlei.vipexam.R
import kotlinx.serialization.Serializable
import java.util.Locale

@Entity
@Serializable
data class Language(
    @PrimaryKey(autoGenerate = false)
    val code: String = "",
    @ColumnInfo val name: String = ""
) {
    override fun equals(other: Any?): Boolean {
        (other as? Language)?.let { otherLang ->
            return otherLang.name.lowercase() == this.name.lowercase() ||
                    otherLang.code.lowercase() == this.code.lowercase()
        }
        return super.equals(other)
    }

    override fun hashCode() = 31 * code.hashCode() + name.hashCode()
}

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