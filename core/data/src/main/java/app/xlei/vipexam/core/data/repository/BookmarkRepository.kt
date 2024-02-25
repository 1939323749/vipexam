package app.xlei.vipexam.core.data.repository

import app.xlei.vipexam.core.database.module.Bookmark
import kotlinx.coroutines.flow.Flow


interface BookmarkRepository {
    fun getAllBookmarks(): Flow<List<Bookmark>>
    suspend fun addBookmark(examName: String, examId: String, question: String)
    suspend fun deleteBookmark(bookmark: Bookmark)
}