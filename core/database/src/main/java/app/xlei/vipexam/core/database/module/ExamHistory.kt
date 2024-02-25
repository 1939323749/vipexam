package app.xlei.vipexam.core.database.module

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(
    tableName = "exam_history",
    indices = [Index(value = ["examId"], unique = true)]
)
data class ExamHistory (
    @PrimaryKey(autoGenerate = true)
    val examHistoryId: Int = 0,
    val examName: String,
    @ColumnInfo(name = "examId") val examId: String,
    val lastOpen: Long = Calendar.getInstance().timeInMillis,
)