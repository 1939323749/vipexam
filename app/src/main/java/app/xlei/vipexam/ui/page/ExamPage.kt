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
import app.xlei.vipexam.core.data.constant.ShowAnswerOption
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.core.data.util.dataStore
import app.xlei.vipexam.core.network.module.NetWorkRepository.getQuestions
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import app.xlei.vipexam.template.Render
import app.xlei.vipexam.ui.VipexamUiState
import app.xlei.vipexam.ui.components.CustomFloatingActionButton
import app.xlei.vipexam.ui.question.*
import app.xlei.vipexam.ui.question.cloze.clozeView
import app.xlei.vipexam.ui.question.listening.ListeningView
import app.xlei.vipexam.ui.question.qread.QreadView
import app.xlei.vipexam.ui.question.translate.TranslateView
import app.xlei.vipexam.ui.question.writing.WritingView
import app.xlei.vipexam.ui.question.zread.ZreadView
import kotlinx.coroutines.launch

/**
 * Exam page
 * 试卷页面
 * @param modifier
 * @param questionListUiState 问题列表状态
 * @param viewModel 问题列表vm
 * @param setQuestion 问题点击事件
 * @param navController 导航控制器
 * @param showFab 显示按钮
 * @receiver
 */
@Composable
fun ExamPage(
    modifier: Modifier = Modifier,
    questionListUiState: VipexamUiState.QuestionListUiState,
    viewModel: QuestionsViewModel = hiltViewModel(),
    setQuestion: (String,String,String) -> Unit,
    navController: NavHostController,
    showFab: Boolean = true,
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

    if (questions.isNotEmpty())
        Questions(
            mubanList = questionListUiState.exam.muban,
            question = if (questions.toMap().containsKey(questionListUiState.question)) {
                questionListUiState.question!!
            } else {
                questions.first().first
            },
            questions = questions,
            navController = navController,
            setQuestion = { title ->
                setQuestion(questionListUiState.exam.examName,questionListUiState.exam.examID,title)
            },
            modifier = modifier
        ) {
            if (showFab)
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
fun Questions(
    modifier: Modifier = Modifier,
    mubanList: List<Muban>,
    question: String,
    questions: List<Pair<String, String>>,
    navController: NavHostController,
    setQuestion: (String) -> Unit,
    FAB: @Composable () -> Unit,
) {
    Scaffold(
        floatingActionButton = FAB,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = question,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                for ((index, q) in questions.withIndex()) {
                    composable(route = q.first) {
                        setQuestion(mubanList[index].cname)
                        QuestionMapToView(question = q.first, muban = mubanList[index])
                    }
                }
            }
        }
    }
}

/**
 * Question map to view
 * 根据问题类型切换不同的显示方式
 * @param question 问题
 * @param muban 模板
 */
@Composable
fun QuestionMapToView(question: String, muban: Muban){
    return when (question) {
        "ecswriting" -> WritingView(muban = muban)
        "ecscloze" -> clozeView(muban = muban)
        "ecsqread" -> QreadView(muban = muban)
        "ecszread" -> ZreadView(muban = muban)
        "ecstranslate" -> TranslateView(muban = muban)
        "ecfwriting" -> WritingView(muban = muban)
        "ecfcloze" -> clozeView(muban = muban)
        "ecfqread" -> QreadView(muban = muban)
        "ecfzread" -> ZreadView(muban = muban)
        "ecftranslate" -> TranslateView(muban = muban)
        "eylhlisteninga" -> ListeningView(muban = muban)
        "eylhlisteningb" -> ListeningView(muban = muban)
        "eylhlisteningc" -> ListeningView(muban = muban)

        "kettrans" -> TranslateView(muban = muban)
        "ketwrite" -> WritingView(muban = muban)
        else -> Render(question = question, muban = muban)
    }
}