package app.xlei.vipexam.ui.question.translate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import app.xlei.vipexam.core.ui.container.VipexamArticleContainer
import app.xlei.vipexam.preference.LocalShowAnswer

@Composable
fun TranslateView(
    viewModel: TranslateViewModel = hiltViewModel(),
    muban: Muban,
) {
    val showAnswer = LocalShowAnswer.current.isShowAnswer()
    viewModel.setMuban(muban)
    viewModel.setTranslations()
    val uiState by viewModel.uiState.collectAsState()

    Translate(
        translations = uiState.translations,
        showAnswer = showAnswer,
    )
}


@Composable
private fun Translate(
    translations: List<TranslateUiState.Translation>,
    showAnswer: Boolean,
){
    val scrollState = rememberLazyListState()

    LazyColumn(
        state = scrollState
    ){
        items(translations.size){

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = translations[it].question,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if(showAnswer){
                VipexamArticleContainer(
                    onDragContent = translations[it].question
                            + "\n\n" + translations[it].refAnswer
                            + "\n\n" + translations[it].description
                ) {
                    Column {
                        Text(
                            text = translations[it].refAnswer,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                        )
                        Text(
                            text = translations[it].description,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }
}