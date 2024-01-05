package app.xlei.vipexam.glance

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.xlei.vipexam.darkScrim
import app.xlei.vipexam.lightScrim
import app.xlei.vipexam.logic.DB
import app.xlei.vipexam.ui.page.Word
import app.xlei.vipexam.ui.page.WordListPage
import app.xlei.vipexam.ui.theme.VipexamTheme
import app.xlei.vipexam.ui.theme.hexToColor
import app.xlei.vipexam.util.LocaleHelper
import app.xlei.vipexam.util.Preferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WordActivity : ComponentActivity() {
    var themeMode by mutableStateOf(Preferences.getThemeMode())
    var accentColor by mutableStateOf(Preferences.getAccentColor())
    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleHelper.updateLanguage(this)
        super.onCreate(savedInstanceState)
        setContent {
            VipexamTheme(
                themeMode = themeMode,
                accentColor = accentColor?.hexToColor()
            ) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = lightScrim(),
                        darkScrim = darkScrim()
                    ),
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = lightScrim(),
                        darkScrim = darkScrim()
                    ),
                )
                WordListPage(openDrawer = {})
            }
        }
        handleIntentData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
        handleIntentData()
    }

    private fun handleIntentData() {
        getIntentText()?.let {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            coroutineScope.launch {
                DB.repository.addWord(
                    word = Word(
                        word = it,
                    )
                )
            }
        }
    }

    private fun getIntentText(): String? {
        return intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
            ?: intent.takeIf { true }
                ?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
            ?: intent.getCharSequenceExtra(Intent.ACTION_SEND)?.toString()
    }
}