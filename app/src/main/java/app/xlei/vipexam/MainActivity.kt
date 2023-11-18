package app.xlei.vipexam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.ui.VipExamApp
import app.xlei.vipexam.ui.VipExamScreen
import app.xlei.vipexam.ui.page.ExamPage
import app.xlei.vipexam.ui.theme.VipexamTheme
import kotlinx.coroutines.DelicateCoroutinesApi


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VipexamTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    VipExamApp()
                }
            }
        }
    }
}









