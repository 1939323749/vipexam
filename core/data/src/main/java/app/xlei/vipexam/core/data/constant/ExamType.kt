package app.xlei.vipexam.core.data.constant

enum class ExamType(
    val examTypeCode: String,
    val examStyle: Int,
    val examTypeName: String,
    val isReal: Boolean,
) {
    CET4_REAL("ve01001", 5, "大学英语四级真题", true),
    CET4_PRACTICE("ve01001", 4, "大学英语四级模拟试题", false),
    CET6_REAL("ve01002", 5, "大学英语六级真题", true),
    CET6_PRACTICE("ve01002", 4, "大学英语六级模拟试题", false),
    KAOYAN_ENGLISH_I_REAL("ve03001", 5, "考研英语一真题", true),
    KAOYAN_ENGLISH_I_PRACTICE("ve03001", 4, "考研英语一模拟试题", false),
    KAOYAN_ENGLISH_II_REAL("ve03002", 5, "考研英语二真题", true),
    KAOYAN_ENGLISH_II_PRACTICE("ve03002", 4, "考研英语二模拟试题", false),
    KAOYAN_408("ve03201003", 4, "考研408模拟试题", false),
}