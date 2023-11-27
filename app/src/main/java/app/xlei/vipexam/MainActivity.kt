package app.xlei.vipexam

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import app.xlei.vipexam.logic.DB
import app.xlei.vipexam.ui.App
import app.xlei.vipexam.ui.theme.VipexamTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        DB.provide(this)
        super.onCreate(savedInstanceState)
        val appContainer = (application as VipExamApplication).container
        setContent {
            VipexamTheme {
                val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
                App(
                    appContainer,
                    widthSizeClass,
                )
            }
        }
    }
}









