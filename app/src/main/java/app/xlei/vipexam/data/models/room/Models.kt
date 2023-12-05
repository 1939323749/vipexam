package app.xlei.vipexam.data.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val account: String,
    val password: String,
)
