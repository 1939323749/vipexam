package app.xlei.vipexam

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import app.xlei.vipexam.core.data.repository.Repository
import app.xlei.vipexam.core.data.util.NetworkMonitor
import app.xlei.vipexam.core.database.module.Word
import app.xlei.vipexam.core.ui.AddToWordListButton
import app.xlei.vipexam.core.ui.TranslateDialog
import app.xlei.vipexam.feature.wordlist.WordListScreen
import app.xlei.vipexam.feature.wordlist.components.copyToClipboard
import app.xlei.vipexam.feature.wordlist.constant.SortMethod
import app.xlei.vipexam.preference.LanguagePreference
import app.xlei.vipexam.preference.LocalThemeMode
import app.xlei.vipexam.preference.SettingsProvider
import app.xlei.vipexam.preference.languages
import app.xlei.vipexam.ui.App
import app.xlei.vipexam.ui.rememberVipExamAppState
import app.xlei.vipexam.ui.theme.VipexamTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var wordRepository: Repository<Word>

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT < 33) {
            LanguagePreference.fromValue(languages).let {
                LanguagePreference.setLocale(it)
            }
        }

        setContent {
            val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
            val appState = rememberVipExamAppState(
                windowSizeClass = widthSizeClass,
                networkMonitor = networkMonitor,
            )
            val density = LocalDensity.current
            val windowsInsets = WindowInsets.systemBars
            val bottomInset = with(density) { windowsInsets.getBottom(density).toDp() }

            val scrollAwareWindowInsets = remember(bottomInset) {
                windowsInsets
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    .add(WindowInsets(bottom = bottomInset))
            }
            CompositionLocalProvider(
                LocalScrollAwareWindowInsets provides scrollAwareWindowInsets
            ) {
                SettingsProvider {
                    VipexamTheme(
                        themeMode = LocalThemeMode.current,
                        shouldShowNavigationRegion = appState.shouldShowAppDrawer,
                    ) {
                        App(
                            widthSizeClass = widthSizeClass,
                            appState = appState
                        )
                    }
                }
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
            this.copyToClipboard(it)
            setContent {
                SettingsProvider {
                    VipexamTheme(
                        themeMode = LocalThemeMode.current,
                    ) {
                        var showTranslateDialog by remember {
                            mutableStateOf(true)
                        }
                        WordListScreen(
                            initSortMethod = SortMethod.NEW_TO_OLD
                        ) {

                        }
                        val context = LocalContext.current
                        val successTip = stringResource(id = R.string.add_to_word_list_success)
                        if (showTranslateDialog)
                            TranslateDialog(
                                onDismissRequest = {
                                    finish()
                                },
                                confirmButton = {
                                    AddToWordListButton(onClick = {
                                        showTranslateDialog = false
                                        Toast.makeText(context, successTip, Toast.LENGTH_SHORT)
                                            .show()
                                    })
                                }
                            )
                    }
                }

            }
        }
    }
}

val LocalScrollAwareWindowInsets =
    compositionLocalOf<WindowInsets> { error("No WindowInsets provided") }