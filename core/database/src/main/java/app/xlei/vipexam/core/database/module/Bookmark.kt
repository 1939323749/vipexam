package app.xlei.vipexam.core.database.module

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val examName: String,
    val examId: String,
    val question: String,
    val created: Long = Calendar.getInstance().timeInMillis,
)