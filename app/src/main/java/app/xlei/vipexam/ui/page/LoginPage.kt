package app.xlei.vipexam.ui.page

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.R
import app.xlei.vipexam.core.database.module.User
import app.xlei.vipexam.core.network.module.LoginResponse
import app.xlei.vipexam.ui.components.VipexamCheckbox

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginView(
    account: String,
    password: String,
    users: List<User>,
    loginResponse: LoginResponse?,
    onAccountChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onDeleteUser: (User) -> Unit,
    onLoginButtonClicked: () -> Unit,
    isAutoLogin: Boolean,
    isRememberAccount: Boolean,
    toggleAutoLogin: (Context, Boolean) -> Unit,
    toggleRememberAccount: (Context, Boolean) -> Unit,
) {
    var showUsers by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
            val expanded = remember { mutableStateOf(false) }
            val emptyTextToolbar = remember { EmptyTextToolbar {
                expanded.value = true
            } }

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
                    VipexamCheckbox(
                        checked = isRememberAccount,
                        onCheckedChange = {newBoolean->
                            toggleRememberAccount(context, newBoolean)
                        }
                    )
                    Text(
                        text = stringResource(R.string.remember_account),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
                Row {
                    VipexamCheckbox(
                        checked = isAutoLogin,
                        onCheckedChange = {newBoolean->
                            toggleAutoLogin(context, newBoolean)
                        },
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

class EmptyTextToolbar(
    private val onSelect: () -> Unit,
) : TextToolbar {
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
        onSelect.invoke()
        onCopyRequested?.invoke()
    }

}