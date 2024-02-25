package app.xlei.vipexam.core.network.module.login


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("code")
    val code: String = "",
    @SerialName("msg")
    val msg: String = "",
    @SerialName("token")
    var token: String = "",
    @SerialName("user")
    val user: User = User()
)