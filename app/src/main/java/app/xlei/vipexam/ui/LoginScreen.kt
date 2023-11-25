package app.xlei.vipexam.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.data.LoginResponse
import app.xlei.vipexam.data.models.room.Setting
import app.xlei.vipexam.data.models.room.User

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun loginView(
    account: String,
    password: String,
    users: List<User>,
    setting: Setting,
    loginResponse: LoginResponse?,
    onAccountChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSettingChange: (Setting) -> Unit,
    onDeleteUser: (User) -> Unit,
    onLoginButtonClicked:()->Unit,
) {
    var showUsers by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.weight(3f).align(Alignment.CenterHorizontally)
        ) {
            ExposedDropdownMenuBox(
                expanded = showUsers,
                onExpandedChange = {showUsers=true}
            ){
                TextField(
                    value = account,
                    onValueChange = { onAccountChange(it) },
                    label = { Text("account") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showUsers)
                    },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = showUsers,
                    onDismissRequest = {showUsers = false}
                ){
                    users.forEach {
                        DropdownMenuItem(
                            text = { Text(it.account) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "delete user",
                                    modifier = Modifier
                                        .clickable {
                                            onDeleteUser(it)
                                        }
                                )
                            },
                            onClick = {
                                onAccountChange(it.account)
                                onPasswordChange(it.password)
                                showUsers = false
                            }
                        )
                    }
                }
            }

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
            Row {
                Row {
                    Checkbox(
                        checked = setting.isRememberAccount,
                        onCheckedChange = {
                           onSettingChange(
                               setting.copy(
                                   isRememberAccount = it,
                               )
                           )
                        }
                    )
                    Text(
                        text = "remember",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
                Row {
                    Checkbox(
                        checked = setting.isAutoLogin,
                        onCheckedChange = {
                            onSettingChange(
                                setting.copy(
                                    isAutoLogin = it
                                )
                            )
                        }
                    )
                    Text(
                        text = "auto login",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

