package app.xlei.vipexam.core.data.repository

import app.xlei.vipexam.core.database.dao.BookmarkDao
import app.xlei.vipexam.core.database.module.Bookmark
import app.xlei.vipexam.core.network.module.NetWorkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
): BookmarkRepository {
    override fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookMarks()
    }

    override suspend fun addBookmark(
        examName: String,
        examId: String,
        question: String,
    ) {
        val addFirstLevelQuestions = suspend {
            NetWorkRepository.getExam(examId).onSuccess {
                val firstLevelQuestion = it.muban.firstOrNull { _muban ->
                    _muban.cname == question
                }
                firstLevelQuestion?.shiti?.map { shiti ->
                    NetWorkRepository.addQCollect(examId, shiti.questionCode)
                        .onSuccess { println(it) }
                }
            }
        }

        val addSecondLevelQuestions = suspend {
            NetWorkRepository.getExam(examId).onSuccess {
                it.muban.firstOrNull { _muban ->
                    _muban.cname == question
                }?.shiti?.forEach { shiti ->
                    shiti.children.map { children ->
                        NetWorkRepository.addQCollect(examId, children.questionCode)
                            .onSuccess { println(it) }
                    }
                }
            }
        }

        bookmarkDao.insertBookmark(
            Bookmark(
                examId = examId,
                examName = examName,
                question = question,
            )
        )
            .also {
                withContext(Dispatchers.IO) {
                    coroutineScope {
                        async { addFirstLevelQuestions.invoke() }.await()
                        async { addSecondLevelQuestions.invoke() }.await()
                    }
                }
            }
    }

    override suspend fun deleteBookmark(bookmark: Bookmark) {
        val examId = bookmark.examId
        val question = bookmark.question

        val deleteFirstLevelQuestions = suspend {
            NetWorkRepository.getExam(examId).onSuccess {
                val firstLevelQuestion = it.muban.firstOrNull { _muban ->
                    _muban.cname == question
                }
                firstLevelQuestion?.shiti?.map { shiti ->
                    NetWorkRepository.deleteQCollect(examId, shiti.questionCode)
                        .onSuccess { println(it) }
                }
            }
        }

        val deleteSecondLevelQuestions = suspend {
            NetWorkRepository.getExam(examId).onSuccess {
                it.muban.firstOrNull { _muban ->
                    _muban.cname == question
                }?.shiti?.forEach { shiti ->
                    shiti.children.map { children ->
                        NetWorkRepository.deleteQCollect(examId, children.questionCode)
                            .onSuccess { println(it) }
                    }
                }
            }
        }

        bookmarkDao.deleteBookmark(bookmark)
            .also {
                withContext(Dispatchers.IO) {
                    coroutineScope {
                        async { deleteFirstLevelQuestions.invoke() }.await()
                        async { deleteSecondLevelQuestions.invoke() }.await()
                    }
                }
            }
    }

}