package app.xlei.vipexam.core.database.module

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val account: String,
    val password: String,
)
