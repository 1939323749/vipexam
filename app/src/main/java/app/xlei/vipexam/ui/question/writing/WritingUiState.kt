package app.xlei.vipexam.ui.question.writing

import app.xlei.vipexam.core.network.module.Muban

data class WritingUiState(
    val muban: Muban?=null,
    val writings: List<Writing>
){
    data class Writing(
        val question: String,
        val refAnswer: String,
        val image: String,
        val description: String,
    )
}