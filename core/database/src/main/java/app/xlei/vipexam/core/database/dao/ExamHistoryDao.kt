package app.xlei.vipexam.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.xlei.vipexam.core.database.module.ExamHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyEntity: ExamHistory)

    @Query("SELECT * FROM exam_history ORDER BY lastOpen DESC")
    fun getAllExamHistory(): Flow<List<ExamHistory>>

    @Query("SELECT * FROM exam_history WHERE examId=:examId LIMIT 1")
    fun getExamHistoryByExamId(examId: String): ExamHistory?

    @Query("SELECT * FROM exam_history ORDER BY lastOpen DESC LIMIT 1")
    fun getLastOpened(): Flow<ExamHistory?>

    @Delete
    suspend fun deleteHistory(historyEntity: ExamHistory)

    @Query("DELETE FROM exam_history WHERE examId=:examId")
    suspend fun deleteHistoryByExamId(examId: String)

    @Query("DELETE FROM exam_history WHERE examHistoryId IN (SELECT examHistoryId FROM exam_history)")
    suspend fun removeAllHistory()
}