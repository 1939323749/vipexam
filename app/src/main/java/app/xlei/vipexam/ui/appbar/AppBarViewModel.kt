package app.xlei.vipexam.ui.appbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.xlei.vipexam.core.data.repository.BookmarkRepository
import app.xlei.vipexam.core.database.module.Bookmark
import app.xlei.vipexam.core.network.module.NetWorkRepository
import app.xlei.vipexam.core.network.module.TiJiaoTest.TestQuestion
import app.xlei.vipexam.core.network.module.TiJiaoTest.TiJiaoTestPayload
import app.xlei.vipexam.core.network.module.getExamResponse.GetExamResponse
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

    private val _submitState = MutableStateFlow<SubmitState<Nothing>>(SubmitState.Default)
    val submitState
        get() = _submitState.asStateFlow()

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
    fun addToBookmark(
        examName: String,
        examId: String,
        question: String,
    ) {
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

    fun submit(exam: GetExamResponse, myAnswer: Map<String, String>) {
        println(myAnswer)
        viewModelScope.launch {
            _submitState.update {
                SubmitState.Submitted
            }
            NetWorkRepository.tiJiaoTest(
                payload = TiJiaoTestPayload(
                    count = exam.count.toString(),
                    examID = exam.examID,
                    examName = exam.examName,
                    examStyle = exam.examstyle,
                    examTypeCode = exam.examTypeCode,
                    testQuestion = exam.muban.flatMap { muban ->
                        muban.shiti.flatMap { shiti ->
                            listOf(
                                TestQuestion(
                                    basic = muban.basic,
                                    grade = muban.grade,
                                    questiontype = muban.ename,
                                    questionCode = shiti.questionCode,
                                    refAnswer = myAnswer[shiti.questionCode] ?: ""
                                )
                            ) + shiti.children.map { child ->
                                TestQuestion(
                                    basic = muban.basic,
                                    grade = muban.grade,
                                    questiontype = muban.ename,
                                    questionCode = child.questionCode,
                                    refAnswer = myAnswer[child.questionCode] ?: ""
                                )
                            }
                        }
                    }
                )
            ).onSuccess {
                _submitState.update {
                    SubmitState.Success(
                        grade = exam.muban.flatMap { muban ->
                            muban.shiti.flatMap { shiti ->
                                listOf(
                                    if (shiti.refAnswer != "" && shiti.refAnswer == (myAnswer[shiti.questionCode]
                                            ?: "")
                                    )
                                        1 else 0
                                ) + shiti.children.map { child ->
                                    if (child.refAnswer != "" && child.refAnswer == (myAnswer[child.questionCode]
                                            ?: "")
                                    )
                                        1 else 0
                                }
                            }
                        }.sum(),
                        gradeCount = exam.muban.joinToString(";") {
                            it.cname + ": " + it.shiti.flatMap { shiti ->
                                listOf(
                                    if (shiti.refAnswer != "" && shiti.refAnswer == (myAnswer[shiti.questionCode]
                                            ?: "")
                                    )
                                        1 else 0
                                ) + shiti.children.map { child ->
                                    if (child.refAnswer != "" && child.refAnswer == (myAnswer[child.questionCode]
                                            ?: "")
                                    )
                                        1 else 0
                                }
                            }.sum().toString() + "/" + it.shiti.flatMap { shiti ->
                                listOf(
                                    if (shiti.refAnswer != "")
                                        1 else 0
                                ) + shiti.children.map { child ->
                                    if (child.refAnswer != "")
                                        1 else 0
                                }
                            }.sum().toString()
                        }
                    )
                }
            }.onFailure { err ->
                _submitState.update { SubmitState.Failed(msg = err.toString()) }
            }
        }
    }

    fun resetSubmitState() {
        _submitState.update {
            SubmitState.Default
        }
    }
}

sealed class SubmitState<out T> {
    data object Default : SubmitState<Nothing>()

    data object Submitted : SubmitState<Nothing>()

    data class Success(val grade: Int, val gradeCount: String) : SubmitState<Nothing>()

    data class Failed(val msg: String) : SubmitState<Nothing>()
}