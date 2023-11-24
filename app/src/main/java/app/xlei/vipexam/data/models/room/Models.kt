package app.xlei.vipexam.data.models.room

import android.accounts.Account
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val account: String,
    val password: String,
)

@Entity(tableName = "vipexam_settings")
data class Setting(
    @ColumnInfo(name = "setting_id")
    @PrimaryKey
    val id: Int,
    val isRememberAccount: Boolean,
    val isAutoLogin: Boolean,
)