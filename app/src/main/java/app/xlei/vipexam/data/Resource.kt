package app.xlei.vipexam.data

data class Resource(
    val tid: Int,
    val examid: String,
    val examname: String,
    val examtypecode: String,
    val examstyle: String,
    val examdate: String,
    val templatecode: String,
    val tmplExamScore: Int,
    val temlExamtimeLimit: Int,
    val fullName: String,
    val examtypeII: Any?,
    val collectDate: Any?,
    val examtyleStr: String,
    val examTypeEName: String
)