package app.xlei.vipexam.core.network.module

data class ExamList(
    val msg: String,
    val code: String,
    val count: Int,
    val list: List<Resource>,
    val resourceType: Int
)
