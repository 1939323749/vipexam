package app.xlei.vipexam.core.data.repository

import app.xlei.vipexam.core.database.module.ExamHistory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ExamHistoryRepository {
    fun getAllExamHistory(): Flow<List<ExamHistory>>
    suspend fun removeAllHistory()
    suspend fun getExamHistoryByExamId(examId: String): ExamHistory?
    suspend fun getLastOpened(): Flow<ExamHistory?>
    suspend fun removeHistory(examHistory: ExamHistory)
    suspend fun insertHistory(examName: String, examId: String)
    suspend fun getHistoryByDateRange(startDate: LocalDate, endDate: LocalDate): List<ExamHistory>
}