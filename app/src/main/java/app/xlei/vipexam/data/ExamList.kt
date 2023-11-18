package app.xlei.vipexam.data

data class ExamList(
    val msg: String,
    val code: String,
    val count: Int,
    val list: List<Resource>,
    val resourceType: Int
)
