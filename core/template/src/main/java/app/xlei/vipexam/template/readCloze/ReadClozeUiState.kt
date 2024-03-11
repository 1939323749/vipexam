package app.xlei.vipexam.template.readCloze

import androidx.compose.runtime.MutableState
import app.xlei.vipexam.core.network.module.getExamResponse.Muban

data class ReadClozeUiState(
    val muban: Muban? = null,
    var showBottomSheet: Boolean = false,
    var showOptionsSheet: Boolean = false,

    val articles: List<Article>
) {
    data class Article(
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