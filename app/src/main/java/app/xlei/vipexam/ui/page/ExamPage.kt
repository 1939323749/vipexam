package app.xlei.vipexam.ui.page

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import app.xlei.vipexam.data.ExamUiState
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.data.network.Repository.getQuestions
import app.xlei.vipexam.ui.components.CustomFloatingActionButton
import app.xlei.vipexam.ui.question.*
import app.xlei.vipexam.ui.question.cloze.clozeView
import app.xlei.vipexam.ui.question.listening.listeningView
import app.xlei.vipexam.ui.question.qread.qreadView
import app.xlei.vipexam.ui.question.translate.translateView
import app.xlei.vipexam.ui.question.writing.writingView
import app.xlei.vipexam.ui.question.zread.zreadView
import app.xlei.vipexam.util.Preferences
import app.xlei.vipexam.util.dataStore
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ExamPage(
    questionListUiState: ExamUiState.QuestionListUiState,
    viewModel: QuestionsViewModel = hiltViewModel(),
    setQuestion: (String) -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    viewModel.setMubanList(mubanList = questionListUiState.exam.muban)
    val uiState by viewModel.uiState.collectAsState()
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
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
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
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                if (Preferences.get(
                        Preferences.alwaysShowAnswerKey,
                        ShowAnswerOptions.ONCE.value
                    )
                    == ShowAnswerOptions.ONCE.value
                ) {
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

@RequiresApi(Build.VERSION_CODES.P)
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