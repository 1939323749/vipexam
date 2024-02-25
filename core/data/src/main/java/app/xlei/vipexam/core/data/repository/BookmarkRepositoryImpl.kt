package app.xlei.vipexam.core.data.repository

import app.xlei.vipexam.core.database.dao.BookmarkDao
import app.xlei.vipexam.core.database.module.Bookmark
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
): BookmarkRepository {
    override fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookMarks()
    }

    override suspend fun addBookmark(examName: String, examId: String, question: String) {
        return bookmarkDao.insertBookmark(
            Bookmark(
                examName = examName,
                examId = examId,
                question = question,
            )
        )
    }

    override suspend fun deleteBookmark(bookmark: Bookmark) {
        return bookmarkDao.deleteBookmark(
            bookmark = bookmark
        )
    }

}