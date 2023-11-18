package app.xlei.vipexam.data

data class ExamUiState(
    val account:String="",
    val password:String="",
    val token:String?=null,
    val examList: ExamList?=null,
    val currentPage:String="1",
    val exam: Exam?=null
)
