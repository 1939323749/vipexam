package app.xlei.vipexam.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.data.LoginResponse
import kotlinx.coroutines.DelicateCoroutinesApi

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun loginView(
    account: String,
    password: String,
    loginResponse: LoginResponse?,
    onAccountChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginButtonClicked:()->Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.weight(3f).align(Alignment.CenterHorizontally)
        ) {
            TextField(
                value = account,
                onValueChange = { onAccountChange(it) },
                label = { Text("account") }
            )
            TextField(
                value = password,
                onValueChange = { onPasswordChange(it) },
                label = { Text("password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(top = 20.dp)
            )
            if(loginResponse!=null){
                Text(loginResponse.msg)
            }
            Button(
                onClick = onLoginButtonClicked,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 20.dp)
            ) {
                Text("Login")
            }
        }
    }
}
