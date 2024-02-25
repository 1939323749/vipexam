package app.xlei.vipexam

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.lifecycleScope
import app.xlei.vipexam.core.data.constant.ThemeMode
import app.xlei.vipexam.core.data.repository.Repository
import app.xlei.vipexam.core.data.util.LocaleHelper
import app.xlei.vipexam.core.data.util.NetworkMonitor
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.core.database.module.Word
import app.xlei.vipexam.ui.App
import app.xlei.vipexam.ui.theme.VipexamTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var wordRepository: Repository<Word>

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleHelper.updateLanguage(this)

        super.onCreate(savedInstanceState)
        setContent {
            VipexamTheme(
                themeMode = ThemeMode.entries[Preferences.themeMode.collectAsState(
                    initial = ThemeMode.AUTO.value
                ).value],
            ) {
                val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
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
                App(
                    widthSizeClass = widthSizeClass,
                    networkMonitor = networkMonitor,
                )
            }
        }
        handleIntentData()
    }

    private fun getIntentText(): String? {
        return intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
            ?: intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
            ?: intent.getCharSequenceExtra(Intent.ACTION_SEND)?.toString()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
        handleIntentData()
    }

    private fun handleIntentData() {
        getIntentText()?.let {
            lifecycleScope.launch {
                wordRepository.add(
                    Word(
                        word = it
                    )
                )
                finish()
                return@launch
            }
        }
    }
}

@Composable
fun lightScrim() = MaterialTheme.colorScheme.background.toArgb()

@Composable
fun darkScrim() = MaterialTheme.colorScheme.background.toArgb()



