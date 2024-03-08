package app.xlei.vipexam.ui.appbar

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
 * App bar view model
 *
 * @property bookmarkRepository 用于书签按钮
 * @constructor Create empty App bar view model
 */
@HiltViewModel
class AppBarViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    private val _bookmarks = MutableStateFlow(emptyList<Bookmark>())
    val bookmarks = _bookmarks.asStateFlow()

    init {
        getBookmarks()
    }

    /**
     * Get bookmarks
     * 获得全部书签
     */
    private fun getBookmarks() {
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

    /**
     * Add to bookmark
     * 添加到书签
     * @param examName 试卷名称
     * @param examId 试卷id
     * @param question 问题名称
     */
    fun addToBookmark(examName: String, examId: String, question: String) {
        viewModelScope.launch {
            bookmarkRepository
                .addBookmark(
                    examName = examName,
                    examId = examId,
                    question = question,
                )
        }
    }

    /**
     * Remove from bookmarks
     * 移除书签
     * @param bookmark 书签对象
     */
    fun removeFromBookmarks(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkRepository
                .deleteBookmark(
                    bookmark = bookmark
                )
        }
    }
}