package app.xlei.vipexam.core.network.module.login


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("account")
    val account: String = "",
    @SerialName("collegename")
    val collegename: String = "",
    @SerialName("credentialsSalt")
    val credentialsSalt: String = "",
    @SerialName("email")
    val email: String? = null,
    @SerialName("headimg")
    val headimg: String? = null,
    @SerialName("issuper")
    val issuper: Boolean = false,
    @SerialName("lastlogintime")
    val lastlogintime: String? = null,
    @SerialName("locked")
    val locked: String = "",
    @SerialName("managerid")
    val managerid: Int = 0,
    @SerialName("password")
    val password: String? = null,
    @SerialName("phone")
    val phone: String = "",
    @SerialName("regdate")
    val regdate: String = "",
    @SerialName("role")
    val role: Int = 0,
    @SerialName("sex")
    val sex: String? = null,
    @SerialName("token")
    val token: String = "",
    @SerialName("username")
    val username: String? = null
)