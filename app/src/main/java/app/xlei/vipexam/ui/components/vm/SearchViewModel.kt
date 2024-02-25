package app.xlei.vipexam.ui.components.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.xlei.vipexam.core.data.ExamListItem
import app.xlei.vipexam.core.data.ExamListRepository
import app.xlei.vipexam.core.network.module.NetWorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val examListRepository : ExamListRepository,
) : ViewModel() {
    private val _examListState: MutableStateFlow<PagingData<ExamListItem>> = MutableStateFlow(value = PagingData.empty())
    val examListState: MutableStateFlow<PagingData<ExamListItem>>
        get() = _examListState

    fun search(query: String) {
        viewModelScope.launch {
            examListRepository.search(query)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _examListState.value = it
                }
        }
    }

    fun download(
        fileName: String,
        examId: String,
    ){
        viewModelScope.launch {
            NetWorkRepository
                .download(
                    fileName = fileName,
                    examId = examId
                )
        }
    }
}