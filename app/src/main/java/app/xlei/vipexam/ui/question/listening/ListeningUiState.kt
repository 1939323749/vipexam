package app.xlei.vipexam.ui.question.listening

import androidx.compose.runtime.MutableState
import app.xlei.vipexam.data.Muban

data class ListeningUiState(
    val muban: Muban?=null,
    var showBottomSheet: MutableState<Boolean>?=null,
    var showOptionsSheet: MutableState<Boolean>?=null,
    var selectedChoiceIndex: MutableState<Pair<Int, Int>>?=null,

    val options: List<String>?=null,
    val choices: MutableList<MutableState<Pair<Int, Pair<Int, String?>>>>?=null,
)
