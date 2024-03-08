package app.xlei.vipexam.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.R
import app.xlei.vipexam.core.database.module.User
import app.xlei.vipexam.core.network.module.login.LoginResponse

/**
 * Login screen
 *
 * @param modifier
 * @param account 账号
 * @param password 密码
 * @param users 用户列表
 * @param loginResponse 登录事件响应
 * @param onAccountChange 输入账号改变事件
 * @param onPasswordChange 输入密码改变事件
 * @param onDeleteUser 删除用户事件
 * @param onLoginButtonClicked 登录按钮点击事件
 * @receiver
 * @receiver
 * @receiver
 * @receiver
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    account: String,
    password: String,
    users: List<User>,
    loginResponse: LoginResponse?,
    onAccountChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onDeleteUser: (User) -> Unit,
    onLoginButtonClicked: () -> Unit,
) {
    var showUsers by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(3f)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = showUsers,
                onExpandedChange = { showUsers = true }
            ) {
                OutlinedTextField(
                    value = account,
                    onValueChange = onAccountChange,
                    label = { Text(stringResource(R.string.account)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showUsers)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
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
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text(stringResource(R.string.password)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            )



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
        }
    }
}
