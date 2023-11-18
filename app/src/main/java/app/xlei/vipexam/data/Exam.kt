package app.xlei.vipexam.data

data class Exam(
    val msg: String,
    val muban: List<Muban>,
    val examTypeCode: String,
    val code: Int,
    val examstyle: String,
    val examName: String,
    val count: Int,
    val examID: String,
    val planID: String,
    val timelimit: Int
)


data class Muban(
    val ename: String,
    val cunt: Int,
    val gradel: String,
    val cname: String,
    val shiti: List<Shiti>,
    val basic: String
)

data class Shiti(
    val groupCodePrimQuestion: String,
    val primQuestion: String,
    val discription: String,
    val firstPic: String,
    val questionCode: String,
    val isCollect: String,
    val subPrimPic: String,
    val refAnswer: String,
    val fourthPic: String,
    val discPic: String,
    val second: String,
    val subjectTypeEname: String,
    val originalText: String,
    val thirdPic: String,
    val secondPic: String,
    val third: String,
    val fifthPic: String,
    val audioFiles: String,
    val children: List<Children>,
    val primPic: String,
    val fifth: String,
    val fourth: String,
    val answerPic: String,
    val first: String,
    val secondQuestion: String
)

data class Children(
    val primQuestion: String,
    val discription: String,
    val firstPic: String,
    val questionCode: String,
    val isCollect: String,
    val subPrimPic: String,
    val refAnswer: String,
    val fourthPic: String,
    val discPic: String,
    val secondQuestion: String,
    val second: String,
    val subjectTypeEname: String,
    val originalText: String,
    val thirdPic: String,
    val secondPic: String,
    val third: String,
    val fifthPic: String,
    val audioFiles: String,
    val primPic: String,
    val fifth: String,
    val fourth: String,
    val answerPic: String,
    val first: String
)
