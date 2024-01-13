package app.xlei.vipexam.ui.page

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.R
import app.xlei.vipexam.core.database.module.User
import app.xlei.vipexam.core.network.module.LoginResponse
import app.xlei.vipexam.ui.LoginSetting

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginView(
    account: String,
    password: String,
    users: State<List<User>>,
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
            modifier = Modifier
                .weight(3f)
                .align(Alignment.CenterHorizontally)
        ) {
            ExposedDropdownMenuBox(
                expanded = showUsers,
                onExpandedChange = { showUsers = true }
            ) {
                TextField(
                    value = account,
                    onValueChange = onAccountChange,
                    label = { Text(stringResource(R.string.account)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showUsers)
                    },
                    modifier = Modifier
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = showUsers,
                    onDismissRequest = { showUsers = false }
                ) {
                    users.value.forEach {
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
            val expanded = remember { mutableStateOf(false) }
            val emptyTextToolbar = remember { EmptyTextToolbar(expanded) }

            CompositionLocalProvider(
                LocalTextToolbar provides emptyTextToolbar
            ) {
                Box {
                    TextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text(stringResource(R.string.password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            }

            if (loginResponse != null) {
                Text(loginResponse.msg)
            }

            Button(
                onClick = onLoginButtonClicked,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
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

class EmptyTextToolbar(private val expended: MutableState<Boolean>) : TextToolbar {
    override val status: TextToolbarStatus = TextToolbarStatus.Hidden

    override fun hide() {
        //expended.value=false
    }

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?,
    ) {
        expended.value = true
        onCopyRequested?.invoke()
    }

}