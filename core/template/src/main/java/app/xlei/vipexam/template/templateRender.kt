package app.xlei.vipexam.template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import app.xlei.vipexam.template.writing.WritingView

@Composable
fun Render(
    submitMyAnswer: (String, String) -> Unit,
    question: String,
    muban: Muban,
) {
    when (question) {
        "keread" -> ReadView(muban = muban, submitMyAnswer = submitMyAnswer)
        "keclozea" -> ClozeView(muban = muban, submitMyAnswer = submitMyAnswer)
        "kereadcloze" -> ReadClozeView(muban = muban, submitMyAnswer = submitMyAnswer)
        "kereadf" -> TranslateView(muban = muban)
        //"kewritinga" -> WritingView(muban = muban)
        "kewritinga" -> Template {
            Questions(muban.shiti.size) { index ->
                Question(muban.shiti[index].primQuestion)
                Answer(muban.shiti[index].refAnswer)
            }
        }

        "kewritingb" -> WritingView(muban = muban)
        "ketclose" -> ClozeView(muban = muban, submitMyAnswer = submitMyAnswer)
        "ketread" -> ReadView(muban = muban, submitMyAnswer = submitMyAnswer)

        "kzjsjzhchoose" -> {
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

        "kzjsjzhbig" -> {
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
