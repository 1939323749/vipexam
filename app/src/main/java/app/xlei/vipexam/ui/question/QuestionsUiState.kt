package app.xlei.vipexam.ui.question

import app.xlei.vipexam.core.network.module.getExamResponse.Muban

data class QuestionsUiState(
    val mubanList: List<Muban>?=null
)