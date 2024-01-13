package app.xlei.vipexam.ui.question.translate

import app.xlei.vipexam.core.network.module.Muban

data class TranslateUiState(
    val muban: Muban?=null,
    val translations: List<Translation>,
){
    data class Translation(
        val question: String,
        val refAnswer: String,
        val description: String,
    )
}
