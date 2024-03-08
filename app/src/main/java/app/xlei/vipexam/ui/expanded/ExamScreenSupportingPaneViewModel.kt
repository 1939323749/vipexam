package app.xlei.vipexam.ui.expanded

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.xlei.vipexam.core.data.repository.BookmarkRepository
import app.xlei.vipexam.core.database.module.Bookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Exam screen supporting pane view model
 * 试卷页面显示试卷标题和问题列表，切换问题
 * @property bookmarkRepository 用于显示是否已经加入书签
 * @constructor Create empty Exam screen supporting pane view model
 */
@HiltViewModel
class ExamScreenSupportingPaneViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    private val _bookmarks = MutableStateFlow(emptyList<Bookmark>())
    val bookmarks = _bookmarks.asStateFlow()

    init {
        getBookmarks()
    }

    private fun getBookmarks(){
        viewModelScope.launch {
            bookmarkRepository
                .getAllBookmarks()
                .flowOn(Dispatchers.IO)
                .collect { bookmark: List<Bookmark> ->
                    _bookmarks.update {
                        bookmark
                    }
                }
        }
    }

}