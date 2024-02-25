package app.xlei.vipexam.ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.xlei.vipexam.core.data.ExamListItem
import app.xlei.vipexam.core.data.GetExamListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

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