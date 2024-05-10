package app.xlei.vipexam.core.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import app.xlei.vipexam.core.database.dao.ExamHistoryDao
import app.xlei.vipexam.core.database.module.ExamHistory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

class ExamHistoryRepositoryImpl @Inject constructor(
    private val examHistoryDao: ExamHistoryDao
): ExamHistoryRepository {
    override fun getAllExamHistory(): Flow<List<ExamHistory>> {
        return examHistoryDao.getAllExamHistory()
    }

    override suspend fun removeAllHistory() {
        return examHistoryDao.removeAllHistory()
    }

    override suspend fun getExamHistoryByExamId(examId: String): ExamHistory? {
        return examHistoryDao.getExamHistoryByExamId(examId)
    }

    override suspend fun getLastOpened(): Flow<ExamHistory?> {
        return examHistoryDao.getLastOpened()
    }

    override suspend fun removeHistory(examHistory: ExamHistory) {
        examHistoryDao.deleteHistory(examHistory)
    }

    override suspend fun insertHistory(examName: String, examId: String) {
        examHistoryDao.insert(
            ExamHistory(examName = examName, examId = examId)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getHistoryByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<ExamHistory> {
        return examHistoryDao.getHistoryByDateRange(
            startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
            endDate.atStartOfDay().plusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli(),
        )
    }
}