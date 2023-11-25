package app.xlei.vipexam.ui.question.cloze

import androidx.compose.runtime.*
import androidx.compose.ui.text.AnnotatedString
import app.xlei.vipexam.data.Muban

data class ClozeUiState(
    val muban: Muban?=null,
    var showBottomSheet: Boolean = false,

    val clozes: List<Cloze>,
){
    data class Cloze(
        val article: Article,
        val blanks: List<Blank>,
        val options: List<Option>,
    )

    data class Article(
        val article: AnnotatedString,
        val tags: List<String>,
    )
    data class Blank(
        val index: String,
        val choice: MutableState<String>,
        val refAnswer: String="",
        val description: String="",
    )

    data class Option(
        val index: String,
        val word: String,
    )
}