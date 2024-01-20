package app.xlei.vipexam.ui.page

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.datastore.preferences.core.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.core.data.constant.ShowAnswerOption
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.core.data.util.dataStore
import app.xlei.vipexam.core.network.module.Muban
import app.xlei.vipexam.core.network.module.NetWorkRepository.getQuestions
import app.xlei.vipexam.ui.VipexamUiState
import app.xlei.vipexam.ui.components.CustomFloatingActionButton
import app.xlei.vipexam.ui.question.*
import app.xlei.vipexam.ui.question.cloze.clozeView
import app.xlei.vipexam.ui.question.listening.listeningView
import app.xlei.vipexam.ui.question.qread.qreadView
import app.xlei.vipexam.ui.question.translate.translateView
import app.xlei.vipexam.ui.question.writing.writingView
import app.xlei.vipexam.ui.question.zread.zreadView
import kotlinx.coroutines.launch

@Composable
fun ExamPage(
    questionListUiState: VipexamUiState.QuestionListUiState,
    viewModel: QuestionsViewModel = hiltViewModel(),
    setQuestion: (String) -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    viewModel.setMubanList(mubanList = questionListUiState.exam.muban)
    val uiState by viewModel.uiState.collectAsState()
    val vibrate by Preferences.vibrate.collectAsState(initial = true)
    val showAnswerOption = ShowAnswerOption.entries[
            Preferences.showAnswerOption.collectAsState(initial = ShowAnswerOption.ONCE.value).value
    ]

    val questions = getQuestions(uiState.mubanList!!)
    val haptics = LocalHapticFeedback.current
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    questions(
        mubanList = questionListUiState.exam.muban,
        question = if (questions.toMap().containsKey(questionListUiState.question)) {
            questionListUiState.question!!
        } else {
            questions.first().first
        },
        questions = questions,
        navController = navController,
        setQuestion = { title ->
            setQuestion(title)
        },
    ) {
        CustomFloatingActionButton(
            expandable = true,
            onFabClick = {
                if (vibrate) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            },
            iconExpanded = Icons.Filled.KeyboardArrowDown,
            iconUnExpanded = Icons.Filled.KeyboardArrowUp,
            items = questions,
            onItemClick = {
                navController.navigate(it) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                if (vibrate) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun questions(
    mubanList: List<Muban>,
    question: String,
    questions: List<Pair<String, String>>,
    navController: NavHostController,
    setQuestion: (String) -> Unit,
    FAB: @Composable () -> Unit,
) {
    Scaffold(
        floatingActionButton = FAB,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = question,
                modifier = Modifier
            ) {
                for ((index, q) in questions.withIndex()) {
                    composable(route = q.first) {
                        setQuestion(mubanList[index].cname)
                        when (q.first) {
                            "ecswriting" -> writingView(muban = mubanList[index])
                            "ecscloze" -> clozeView(muban = mubanList[index])
                            "ecsqread" -> qreadView(muban = mubanList[index])
                            "ecszread" -> zreadView(muban = mubanList[index])
                            "ecstranslate" -> translateView(muban = mubanList[index])
                            "ecfwriting" -> writingView(muban = mubanList[index])
                            "ecfcloze" -> clozeView(muban = mubanList[index])
                            "ecfqread" -> qreadView(muban = mubanList[index])
                            "ecfzread" -> zreadView(muban = mubanList[index])
                            "ecftranslate" -> translateView(muban = mubanList[index])
                            "eylhlisteninga" -> listeningView(muban = mubanList[index])
                            "eylhlisteningb" -> listeningView(muban = mubanList[index])
                            "eylhlisteningc" -> listeningView(muban = mubanList[index])
                        }
                    }
                }
            }
        }
    }
}