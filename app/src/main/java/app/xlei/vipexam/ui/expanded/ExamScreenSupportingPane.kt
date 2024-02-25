package app.xlei.vipexam.ui.expanded

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import app.xlei.vipexam.core.data.constant.ShowAnswerOption
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.core.data.util.dataStore
import app.xlei.vipexam.ui.VipexamUiState
import app.xlei.vipexam.ui.components.Timer
import compose.icons.FeatherIcons
import compose.icons.feathericons.Clock
import compose.icons.feathericons.RotateCw
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreenSupportingPane(
    modifier: Modifier = Modifier,
    viewModel: ExamScreenSupportingPaneViewModel = hiltViewModel(),
    questionListUiState: VipexamUiState.QuestionListUiState,
    navController: NavHostController,
){
    val showAnswerOption = ShowAnswerOption.entries[
        Preferences.showAnswerOption.collectAsState(initial = ShowAnswerOption.ONCE.value).value
    ]
    val bookmarks by viewModel.bookmarks.collectAsState()
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    var showTimer by remember {
        mutableStateOf(false)
    }

    Scaffold (
        topBar = {
            TopAppBar(title = { Text(text = questionListUiState.exam.examName) })
        },
        bottomBar = {
            ElevatedCard(
                modifier = Modifier
                    .padding(top = 24.dp)
            ) {
                BottomAppBar(
                    containerColor = CardDefaults.elevatedCardColors().containerColor,
                    actions = {
                        AnimatedVisibility(
                            visible = showTimer,
                            enter = fadeIn(animationSpec = tween(200), 0F),
                            exit = fadeOut(animationSpec = tween(200),0F)
                            ) {
                            Timer(
                                isTimerStart = showTimer,
                                isResetTimer = !showTimer,
                                modifier = Modifier
                                    .padding(start = 24.dp)
                            )
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            showTimer = !showTimer
                        }) {
                            if (!showTimer) Icon(imageVector = FeatherIcons.Clock, contentDescription = null)
                            else Icon(imageVector = FeatherIcons.RotateCw, contentDescription = null)
                        }
                    },
                    // scroll behavior
                    // Scrolling
                    // Upon scroll, the bottom app bar can appear or disappear:
                    // * Scrolling downward hides the bottom app bar. If a FAB is present, it detaches from the bar and remains on screen.
                    // * Scrolling upward reveals the bottom app bar, and reattaches to a FAB if one is present
                )
            }

        },
        modifier = modifier
    ){padding->
        ElevatedCard (
            shape = CardDefaults.elevatedShape,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ){
            val vibrate by Preferences.vibrate.collectAsState(initial = true)
            val haptics = LocalHapticFeedback.current
            LazyColumn {
                items(questionListUiState.questions.size){index->
                    questionListUiState.questions[index].let {
                        ListItem(
                            headlineContent = { Text(text = it.second) },
                            trailingContent = {
                                if (bookmarks.any {bookmark->
                                        bookmark.examName == questionListUiState.exam.examName
                                                && bookmark.question == it.second })
                                    Icon(imageVector = Icons.Default.Star, contentDescription = null)
                            },
                            modifier = Modifier
                                .clickable {
                                    // examName: String ,examId: String, question: String
                                    navController.navigate(it.first) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                    if (vibrate) haptics.performHapticFeedback(
                                        HapticFeedbackType.LongPress)
                                    if (showAnswerOption == ShowAnswerOption.ONCE) {
                                        coroutine.launch {
                                            context.dataStore.edit { preferences ->
                                                preferences[Preferences.SHOW_ANSWER] = false
                                            }
                                        }
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}
