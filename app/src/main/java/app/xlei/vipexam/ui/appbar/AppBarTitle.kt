package app.xlei.vipexam.ui.appbar

import app.xlei.vipexam.R

sealed class AppBarTitle(
    val nameId: Int
){
    data object Login: AppBarTitle(R.string.login)
    data object ExamType: AppBarTitle(R.string.examtype)
    data object ExamList: AppBarTitle(R.string.examlist)
    data class Exam(var examName: String, var examId: String, var question: String) : AppBarTitle(R.string.exam)
}