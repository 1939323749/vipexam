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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.core.network.module.Muban
import app.xlei.vipexam.core.data.util.Preferences

@Composable
fun translateView(
    viewModel: TranslateViewModel = hiltViewModel(),
    muban: Muban,
){
    val showAnswer = Preferences.showAnswerFlow.collectAsState(initial = false)
    viewModel.setMuban(muban)
    viewModel.setTranslations()
    val uiState by viewModel.uiState.collectAsState()

    translate(
        translations = uiState.translations,
        showAnswer = showAnswer,
    )
}


@Composable
private fun translate(
    translations: List<TranslateUiState.Translation>,
    showAnswer: State<Boolean>,
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

            if(showAnswer.value){
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