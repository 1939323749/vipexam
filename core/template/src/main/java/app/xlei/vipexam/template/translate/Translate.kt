package app.xlei.vipexam.template.translate

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
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.core.network.module.getExamResponse.Muban

@Composable
fun TranslateView(
    viewModel: TranslateViewModel = hiltViewModel(),
    muban: Muban,
) {
    val showAnswer by Preferences.showAnswer.collectAsState(initial = false)
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
) {
    val scrollState = rememberLazyListState()

    LazyColumn(
        state = scrollState
    ) {
        items(translations.size) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = translations[it].content,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }

            translations[it].sentences.forEach { sentence ->
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "${sentence.index}. ${sentence.sentence}",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (showAnswer)
                    Text(
                        text = sentence.refAnswer,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(16.dp)
                    )

            }
        }
    }
}