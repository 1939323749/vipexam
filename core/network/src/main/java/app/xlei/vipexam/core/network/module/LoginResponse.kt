package app.xlei.vipexam.core.network.module


data class LoginResponse(
    val msg: String,
    val code: String,
    val user: User,
    val token: String
)

data class User(
    val managerid: Int,
    val role: Int,
    val account: String,
    val password: String?,
    val username: String?,
    val lastlogintime: String?,
    val credentialsSalt: String,
    val locked: String,
    val headimg: String?,
    val collegename: String,
    val email: String?,
    val sex: String?,
    val regdate: String,
    val phone: String,
    val token: String,
    val issuper: Boolean
)