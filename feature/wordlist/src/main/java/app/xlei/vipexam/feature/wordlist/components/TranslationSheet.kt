package app.xlei.vipexam.feature.wordlist.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import app.xlei.vipexam.core.data.paging.MomoLookUpApi
import app.xlei.vipexam.core.data.paging.Source
import app.xlei.vipexam.core.network.module.NetWorkRepository
import app.xlei.vipexam.core.ui.EmptyTextToolbar
import app.xlei.vipexam.core.ui.TranslateDialog
import app.xlei.vipexam.feature.wordlist.components.viewmodel.TranslationSheetViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.Loader
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TranslationSheet(
    text: String,
    viewModel: TranslationSheetViewModel = hiltViewModel(),
    toggleBottomSheet: () -> Unit,
) {
    val phrases = viewModel.phrases.collectAsLazyPagingItems()
    var translation by remember {
        mutableStateOf(
            app.xlei.vipexam.core.network.module.TranslationResponse(
                code = 200,
                id = "",
                data = "",
                alternatives = emptyList(),
                sourceLang = "en",
                targetLang = "zh",
                method = "vipexam translate"
            )
        )
    }
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    var showTranslationDialog by remember {
        mutableStateOf(false)
    }
    var selectedSource by remember {
        mutableStateOf(Source.ALL)
    }

    DisposableEffect(Unit) {
        coroutine.launch {
            val res = NetWorkRepository.translateToZH(text)
            res.onSuccess {
                translation = it
            }
            res.onFailure {
                toggleBottomSheet()
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
            viewModel.search(text)
        }
        onDispose { viewModel.clean() }
    }
    ModalBottomSheet(
        onDismissRequest = toggleBottomSheet,
    ) {
        Column(
            modifier = Modifier
                .run {
                    if (phrases.itemCount == 0)
                        this.padding(bottom = 120.dp)
                    else
                        this
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                )
                HorizontalDivider()
                Text(
                    text = translation.data,
                    fontSize = 24.sp,
                )
                LazyRow {
                    if (translation.alternatives.isEmpty() && translation.data == "") {
                        item {
                            Icon(
                                imageVector = FeatherIcons.Loader,
                                contentDescription = null,
                            )
                        }
                    } else {
                        items(translation.alternatives.size) {
                            Text(
                                text = translation.alternatives[it],
                                modifier = Modifier
                                    .padding(end = 12.dp)
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(visible = phrases.itemCount > 0) {
                FlowRow(
                    modifier = Modifier.padding(4.dp)
                ) {
                    Source.entries.forEach {
                        FilterChip(
                            selected = selectedSource == it,
                            onClick = {
                                selectedSource = it
                                MomoLookUpApi.source = it
                                coroutine.launch {
                                    viewModel.refresh(text)
                                }
                            },
                            label = { Text(text = it.displayName) },
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            LazyColumn {
                items(phrases.itemCount) {
                    CompositionLocalProvider(
                        value = LocalTextToolbar provides EmptyTextToolbar {
                            showTranslationDialog = true
                        }
                    ) {
                        SelectionContainer {
                            phrases[it]?.let { phrase ->
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = "${it + 1}. ${
                                                phrase.context.phrase.removeSuffix(
                                                    "//"
                                                )
                                            }"
                                        )
                                    },
                                    supportingContent = {
                                        Column {
                                            Text(
                                                text = phrase.context.options.joinToString(
                                                    separator = "\n"
                                                ) { option ->
                                                    option.option + ". " + option.phrase
                                                })
                                            Text(text = phrase.source)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showTranslationDialog)
            TranslateDialog(
                onDismissRequest = { showTranslationDialog = false },
                dismissButton = {}
            )
    }
}
