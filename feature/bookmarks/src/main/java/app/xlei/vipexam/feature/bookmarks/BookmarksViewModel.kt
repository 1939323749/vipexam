package app.xlei.vipexam.feature.bookmarks

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
class BookmarksViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    private val _bookmarks = MutableStateFlow(emptyList<Bookmark>())

    val bookmarks = _bookmarks.asStateFlow()

    private var _filter = MutableStateFlow("")

    val filter = _filter.asStateFlow()

    init {
        getBookmarks()
    }

    private fun getBookmarks() {
        viewModelScope.launch {
            bookmarkRepository
                .getAllBookmarks()
                .flowOn(Dispatchers.IO)
                .collect { bookmark: List<Bookmark> ->
                    _bookmarks.update {
                        if (_filter.value == "")
                            bookmark
                        else
                            bookmark.filter {
                                it.question == _filter.value
                            }
                    }
                }
        }
    }

    fun setFilter(question: String?=null) {
        _filter.update {
            question?: ""
        }
        getBookmarks()
    }
}