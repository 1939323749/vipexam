package app.xlei.vipexam

//noinspection UsingMaterialAndMaterial3Libraries
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import app.xlei.vipexam.logic.DB
import app.xlei.vipexam.ui.navigation.App
import app.xlei.vipexam.ui.theme.VipexamTheme
import app.xlei.vipexam.ui.theme.hexToColor
import app.xlei.vipexam.util.LocaleHelper
import app.xlei.vipexam.util.Preferences
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //val viewModel: MainActivityViewModel by viewModels()
    var themeMode by mutableStateOf(Preferences.getThemeMode())
    var accentColor by mutableStateOf(Preferences.getAccentColor())

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        DB.provide(this)
        LocaleHelper.updateLanguage(this)

        super.onCreate(savedInstanceState)


        val appContainer = (application as VipExamApplication).container

        setContent {
            VipexamTheme(
                themeMode = themeMode,
                accentColor = accentColor?.hexToColor()
            ) {
                val widthSizeClass = calculateWindowSizeClass(this)
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(lightScrim = lightScrim(), darkScrim = darkScrim()),
                    navigationBarStyle = SystemBarStyle.auto(lightScrim = lightScrim(), darkScrim = darkScrim()),
                )
                App(
                    appContainer,
                    widthSizeClass,
                )
            }
        }
    }
}

@Composable
private fun lightScrim() = MaterialTheme.colorScheme.background.toArgb()

@Composable
private fun darkScrim() = MaterialTheme.colorScheme.background.toArgb()



