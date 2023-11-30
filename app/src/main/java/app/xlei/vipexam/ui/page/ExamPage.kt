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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.hilt.navigation.compose.hiltViewModel
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
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

@Composable
fun ExamPage(
    questionListUiState: ExamUiState.QuestionListUiState,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: () -> Unit,
    showAnswer: MutableState<Boolean>
) {
    questions(
        mubanList = questionListUiState.exam.muban,
        onFirstItemHidden = { title ->
            onFirstItemHidden(title)
        },
        onFirstItemAppear = {
            onFirstItemAppear()
        },
        showAnswer = showAnswer
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun questions(
    mubanList: List<Muban>,
    viewModel: QuestionsViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: () -> Unit,
    showAnswer: MutableState<Boolean>
) {
    viewModel.setMubanList(mubanList)
    val uiState by viewModel.uiState.collectAsState()
    val questions = getQuestions(uiState.mubanList!!)
    val haptics = LocalHapticFeedback.current

    Scaffold(
        floatingActionButton = {
            CustomFloatingActionButton(
                expandable = true,
                onFabClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                iconExpanded = Icons.Filled.KeyboardArrowDown,
                iconUnExpanded = Icons.Filled.KeyboardArrowUp,
                items = questions,
                onItemClick = {
                    navController.navigate(it){
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(questions[0].first)
                    }
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = questions[0].first,
                modifier = Modifier
            ) {
                for ((index,q) in questions.withIndex()){
                    composable(route = q.first){
                        when (q.first) {
                            "ecswriting" -> writingView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                                showAnswer = showAnswer,
                            )
                            "ecscloze" -> clozeView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                                showAnswer = showAnswer,
                            )
                            "ecsqread" -> qreadView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                                showAnswer = showAnswer,
                            )
                            "ecszread" -> zreadView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                                showAnswer = showAnswer,
                            )
                            "ecstranslate" -> translateView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                                showAnswer = showAnswer,
                            )
                            "ecfwriting" -> writingView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                                showAnswer = showAnswer,
                            )
                            "ecfcloze" -> clozeView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                                showAnswer = showAnswer,
                            )
                            "ecfqread" -> qreadView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                                showAnswer = showAnswer,
                            )
                            "ecfzread" -> zreadView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                                showAnswer = showAnswer,
                            )
                            "ecftranslate" -> translateView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                                showAnswer = showAnswer,
                            )
                            "eylhlisteninga" -> listeningView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                                showAnswer = showAnswer,
                            )
                            "eylhlisteningb" -> listeningView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                                showAnswer = showAnswer,
                            )
                            "eylhlisteningc" -> listeningView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                                showAnswer = showAnswer,
                            )
                        }
                    }
                }
            }
        }
    }
}



