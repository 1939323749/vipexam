package app.xlei.vipexam.core.database.module

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(
    tableName = "words",
    indices = [Index(value = ["word"], unique = true)]
)
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "word") val word: String,
    val created: Long = Calendar.getInstance().timeInMillis,
)