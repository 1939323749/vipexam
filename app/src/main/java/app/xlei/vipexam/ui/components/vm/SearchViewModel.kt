package app.xlei.vipexam.ui.components.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.xlei.vipexam.core.data.paging.ExamListItem
import app.xlei.vipexam.core.data.paging.ExamListRepository
import app.xlei.vipexam.core.network.module.NetWorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Search view model
 *
 * @property examListRepository 试卷列表
 * @constructor Create empty Search view model
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val examListRepository : ExamListRepository,
) : ViewModel() {
    private val _examListState: MutableStateFlow<PagingData<ExamListItem>> =
        MutableStateFlow(value = PagingData.empty())
    val examListState: MutableStateFlow<PagingData<ExamListItem>>
        get() = _examListState

    /**
     * Search
     * 搜索试卷
     * @param query 搜索关键词
     */
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

    /**
     * Download
     * 下载试卷
     * @param fileName 文件名
     * @param examId 试卷id
     */
    fun download(
        fileName: String,
        examId: String,
    ) {
        viewModelScope.launch {
            NetWorkRepository
                .download(
                    fileName = fileName,
                    examId = examId
                )
        }
    }
}