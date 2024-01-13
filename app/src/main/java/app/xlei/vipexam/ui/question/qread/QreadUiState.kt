package app.xlei.vipexam.ui.question.qread

import androidx.compose.runtime.MutableState
import app.xlei.vipexam.core.network.module.Muban

data class QreadUiState(
    val muban: Muban?=null,
    var showBottomSheet: Boolean = false,
    var showOptionsSheet: Boolean = false,

    val articles: List<Article>
){
    data class Article(
        val title: String,
        val content: String,
        val questions: List<Question>,
        val options: List<Option>,
    )

    data class Question(
        val index: String,
        val question: String,
        var choice: MutableState<String>,
        val refAnswer: String,
        val description: String,
    )

    data class Option(
        val index: Int,
        val option: String,
    )
}