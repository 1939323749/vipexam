package app.xlei.vipexam.data

import androidx.compose.runtime.MutableState
import app.xlei.vipexam.data.models.room.Setting

data class ExamUiState(
    val account:String="",
    val password:String="",
    val token:String?=null,
    val examList: ExamList?=null,
    val currentPage:String="1",
    val exam: Exam?=null,

    val setting: Setting?=null
)
