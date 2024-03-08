package app.xlei.vipexam.ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.xlei.vipexam.core.data.paging.ExamListItem
import app.xlei.vipexam.core.data.paging.GetExamListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Exam list view model
 * 事件列表vm
 * @property getExamListUseCase 获得试卷列表vm
 * @constructor Create empty Exam list view model
 */
@HiltViewModel
class ExamListViewModel @Inject constructor(
    private val getExamListUseCase: GetExamListUseCase,
) : ViewModel() {

    private val _examListState: MutableStateFlow<PagingData<ExamListItem>> = MutableStateFlow(value = PagingData.empty())
    val examListState: MutableStateFlow<PagingData<ExamListItem>> get() = _examListState

    init {
        viewModelScope.launch {
            getExamList()
        }
    }

    private suspend fun getExamList() {
        getExamListUseCase.execute(Unit)
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
            .collect {
                _examListState.value = it
            }
    }
}