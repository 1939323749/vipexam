package app.xlei.vipexam.ui.question.zread

import androidx.compose.runtime.MutableState
import app.xlei.vipexam.data.Muban

data class ZreadUiState(
    val muban: Muban?=null,
    var showBottomSheet: MutableState<Boolean>?=null,
    var showQuestionsSheet: MutableState<Boolean>?=null,
    var selectedChoiceIndex: MutableState<Pair<Int,Int>>?=null,

    val options: List<String>?=null,
    val choices: MutableState< MutableList<MutableState<Pair<Int, Pair<Int, String?>>>>>?=null,
    val questions: MutableState<MutableList<String>>?=null,
)
