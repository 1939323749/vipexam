package app.xlei.vipexam.ui.question.translate

import app.xlei.vipexam.data.Muban

data class TranslateUiState(
    val muban: Muban?=null,
    val translations: List<Translation>,
){
    data class Translation(
        val question: String,
        val refAnswer: String,
    )
}
