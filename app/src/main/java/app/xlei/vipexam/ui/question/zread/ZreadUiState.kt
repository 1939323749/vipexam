package app.xlei.vipexam.ui.question.zread

import androidx.compose.runtime.MutableState
import app.xlei.vipexam.core.network.module.getExamResponse.Muban

data class ZreadUiState(
    val muban: Muban?=null,
    var showBottomSheet: Boolean = false,
    var showQuestionsSheet: Boolean = false,

    val articles: List<Article>,
){
    data class Article(
        val index: String,
        val content: String,
        val questions: List<Question>,
        val options: List<String> = listOf("A","B","C","D")
    )
    data class Question(
        val index: String,
        val question: String,
        val options: List<Option>,
        val choice: MutableState<String>,
        val refAnswer: String,
        val description: String,
    )

    data class Option(
        val index: String,
        val option: String,
    )
}
