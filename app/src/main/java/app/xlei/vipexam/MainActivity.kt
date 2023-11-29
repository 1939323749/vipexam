package app.xlei.vipexam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import app.xlei.vipexam.logic.DB
import app.xlei.vipexam.ui.navigation.App
import app.xlei.vipexam.ui.theme.VipexamTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        DB.provide(this)
        super.onCreate(savedInstanceState)
        val appContainer = (application as VipExamApplication).container

        setContent {
            VipexamTheme {
                val widthSizeClass = calculateWindowSizeClass(this)
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(lightScrim = lightScrim(), darkScrim = darkScrim()),
                    navigationBarStyle = SystemBarStyle.auto(lightScrim = lightScrim(), darkScrim = darkScrim()),
                )
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    App(
                        appContainer,
                        widthSizeClass,
                    )
                }
            }
        }
    }
}

@Composable
private fun lightScrim() = MaterialTheme.colorScheme.background.toArgb()

@Composable
private fun darkScrim() = MaterialTheme.colorScheme.background.toArgb()



