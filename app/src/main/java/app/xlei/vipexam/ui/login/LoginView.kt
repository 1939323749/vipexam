package app.xlei.vipexam.ui.login

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.R
import app.xlei.vipexam.data.LoginResponse
import app.xlei.vipexam.data.models.room.User
import app.xlei.vipexam.ui.LoginSetting
import io.ktor.network.selector.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun loginView(
    account: String,
    password: String,
    users: List<User>,
    setting: LoginSetting,
    loginResponse: LoginResponse?,
    onAccountChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSettingChange: (LoginSetting) -> Unit,
    onDeleteUser: (User) -> Unit,
    onLoginButtonClicked: () -> Unit,
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
                    label = { Text(stringResource(R.string.account)) },
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
                label = { Text(stringResource(R.string.password)) },
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
                Text(stringResource(R.string.login))
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
                        text = stringResource(R.string.remember_account),
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
                        text = stringResource(R.string.auto_login),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

