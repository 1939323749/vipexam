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
import app.xlei.vipexam.ui.question.writing.WritingView

@Composable
fun Render(
    question: String,
    muban: Muban,
) {
    when (question) {
        "keread" -> ReadView(muban = muban)
        "keclozea" -> ClozeView(muban = muban)
        "kereadcloze" -> ReadClozeView(muban = muban)
        "kereadf" -> TranslateView(muban = muban)
        //"kewritinga" -> WritingView(muban = muban)
        "kewritinga" -> Template {
            Questions(muban.shiti.size) { index ->
                Question(muban.shiti[index].primQuestion)
                Answer(muban.shiti[index].refAnswer)
            }
        }

        "kewritingb" -> WritingView(muban = muban)
        "ketclose" -> ClozeView(muban = muban)
        "ketread" -> ReadView(muban = muban)
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
