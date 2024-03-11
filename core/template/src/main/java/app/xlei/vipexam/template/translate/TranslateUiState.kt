package app.xlei.vipexam.template.translate

import androidx.compose.ui.text.AnnotatedString
import app.xlei.vipexam.core.network.module.getExamResponse.Muban

data class TranslateUiState(
    val muban: Muban? = null,
    val translations: List<Translation>,
) {
    data class Translation(
        val content: AnnotatedString,
        val sentences: List<Sentence>
    )

    data class Sentence(
        val index: Int,
        val sentence: String,
        val refAnswer: String,
    )
}
