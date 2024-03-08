package app.xlei.vipexam.ui.appbar

import app.xlei.vipexam.R

/**
 * App bar title
 * 主页面的标题
 * @property nameId
 * @constructor Create empty App bar title
 */
sealed class AppBarTitle(
    val nameId: Int
) {
    data object Login : AppBarTitle(R.string.login)
    data object ExamType : AppBarTitle(R.string.examtype)
    data object ExamList : AppBarTitle(R.string.examlist)

    /**
     * Exam
     * 试卷页面的标题，由于附加了收藏功能，
     * 所以需要提供用于记录收藏的试卷名称、
     * ID、问题
     * @property examName
     * @property examId
     * @property question
     * @constructor Create empty Exam
     */
    data class Exam(var examName: String, var examId: String, var question: String) :
        AppBarTitle(R.string.exam)
}