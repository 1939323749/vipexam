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

@HiltViewModel
class AppBarViewModel @Inject constructor(
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

    fun addToBookmark(examName: String, examId: String, question: String){
        viewModelScope.launch {
            bookmarkRepository
                .addBookmark(
                    examName = examName,
                    examId = examId,
                    question = question,
                )
        }
    }

    fun removeFromBookmarks(bookmark: Bookmark){
        viewModelScope.launch {
            bookmarkRepository
                .deleteBookmark(
                    bookmark = bookmark
                )
        }
    }
}