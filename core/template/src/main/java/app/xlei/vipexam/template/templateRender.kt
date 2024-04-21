package app.xlei.vipexam.template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import app.xlei.vipexam.template.cloze.ClozeView
import app.xlei.vipexam.template.read.ReadView
import app.xlei.vipexam.template.readCloze.ReadClozeView
import app.xlei.vipexam.template.translate.TranslateView

@Composable
fun Render(
    submitMyAnswer: (String, String) -> Unit,
    question: String,
    muban: Muban,
) {
    when (question) {
        "keread", "ketread" -> ReadView(muban = muban, submitMyAnswer = submitMyAnswer)
        "keclozea", "ketclose" -> ClozeView(muban = muban, submitMyAnswer = submitMyAnswer)
        "kereadcloze" -> ReadClozeView(muban = muban, submitMyAnswer = submitMyAnswer)
        "kereadf" -> TranslateView(muban = muban)
        //"kewritinga" -> WritingView(muban = muban)
        "kewritinga", "kewritingb" -> Template {
            Questions(muban.shiti.size) { index ->
                Question(muban.shiti[index].primQuestion)
                Answer(muban.shiti[index].refAnswer)
            }
        }

        "kzjsjzhchoose", "crmsdschoosecn", "kpchoose", "kpmchoose" -> {
            Template {
                Questions(muban.shiti.size) {
                    muban.shiti[it].let { shiti ->
                        Question(shiti.primQuestion)
                        if (shiti.primQuestion.contains("[*]"))
                            QuestionPic(shiti.primPic)
                        OptionA(shiti.first)
                        OptionB(shiti.second)
                        OptionC(shiti.third)
                        OptionD(shiti.fourth)
                        Answer(shiti.refAnswer)
                        if (shiti.refAnswer.contains("[*]"))
                            AnswerPic(shiti.answerPic)
                        Description(shiti.discription)
                        if (shiti.discription.contains("[*]"))
                            DescriptionPic(shiti.discPic)
                    }
                }
            }
        }

        "kzjsjzhbig", "crmsdxzuti", "crmsdxprogx" -> {
            Template {
                Questions(muban.shiti.size) {
                    muban.shiti[it].let { shiti ->
                        Question(shiti.primQuestion + "\n" + shiti.children.mapIndexed { index, child ->
                            "${index + 1}" + child.secondQuestion
                        }.joinToString("\n"))
                        if (shiti.primQuestion.contains("[*]"))
                            QuestionPic(shiti.primPic)
                        Answer(shiti.refAnswer + shiti.children.joinToString("\n") { it.refAnswer })
                        if (shiti.refAnswer.contains("[*]"))
                            AnswerPic(shiti.answerPic)
                        Description(shiti.discription)
                        if (shiti.discription.contains("[*]"))
                            DescriptionPic(shiti.discPic)
                    }
                }
            }
        }

        "crmsdschoosecnz2", "crmsdschoosecnz3", "crmsdschooseenz5", "kpdataaNAlysis" -> {
            LazyColumn {
                muban.shiti.forEachIndexed { index, shiti ->
                    item {
                        Template(
                            isSubQuestion = true,
                            index = index + 1
                        ) {
                            Article {
                                Content(shiti.primQuestion.removeSuffix("[*]"))
                                if (shiti.primQuestion.contains("[*]"))
                                    ContentPic(shiti.primPic)
                            }
                            Questions(shiti.children.size) {
                                shiti.children[it].let { child ->
                                    if (child.secondQuestion != "") Question(child.secondQuestion)
                                    OptionA(child.first)
                                    OptionB(child.second)
                                    OptionC(child.third)
                                    OptionD(child.fourth)
                                    Answer(child.refAnswer)
                                    if (child.refAnswer.contains("[*]"))
                                        AnswerPic(child.answerPic)
                                    Description(child.discription)
                                    if (child.discription.contains("[*]"))
                                        DescriptionPic(child.discPic)
                                }
                            }
                        }
                    }
                }
            }
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = stringResource(id = R.string.not_supported),
                    style = TextStyle(textDecoration = TextDecoration.Underline),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}
