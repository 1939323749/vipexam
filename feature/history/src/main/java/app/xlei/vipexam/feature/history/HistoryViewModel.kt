package app.xlei.vipexam.feature.history

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.xlei.vipexam.core.data.repository.ExamHistoryRepository
import app.xlei.vipexam.core.database.module.ExamHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val examHistoryRepository: ExamHistoryRepository
) : ViewModel() {
    private val _examHistory = MutableStateFlow(emptyList<ExamHistory>())

    val examHistory = _examHistory.asStateFlow()

    init {
        getExamHistory()
    }
    private fun getExamHistory() {
        viewModelScope.launch {
            examHistoryRepository
                .getAllExamHistory()
                .flowOn(Dispatchers.IO)
                .collect { examHistory: List<ExamHistory> ->
                    _examHistory.update {
                        examHistory
                    }
                }
        }
    }

    fun cleanHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            examHistoryRepository
                .removeAllHistory()
        }
    }

    fun delete(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            examHistoryRepository
                .removeHistory(_examHistory.value[index])
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getHistoryByDateRange(): List<ExamHistory> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusMonths(12)
        return examHistoryRepository.getHistoryByDateRange(startDate, endDate)
    }
}