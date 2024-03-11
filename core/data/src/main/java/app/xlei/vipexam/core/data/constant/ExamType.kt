package app.xlei.vipexam.core.data.constant

enum class ExamType(
    val examTypeCode: String,
    val examStyle: Int,
    val examTypeName: String,
    val isReal: Boolean,
) {
    CET6_REAL("ve01002", 5, "大学英语六级真题", true),
    CET6_PRACTICE("ve01002", 4, "大学英语六级模拟试题", false),
    KAOYAN_ENGLISH_I_REAL("ve03001", 5, "考研英语一真题", true),
    KAOYAN_ENGLISH_I_PRACTICE("ve03001", 4, "考研英语一模拟试题", false)
}