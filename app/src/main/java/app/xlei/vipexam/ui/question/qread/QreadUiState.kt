package app.xlei.vipexam.ui.question.qread

import androidx.compose.runtime.MutableState
import app.xlei.vipexam.data.Muban

data class QreadUiState(
    val muban: Muban?=null,
    var showBottomSheet: MutableState<Boolean>?=null,
    var showOptionsSheet: MutableState<Boolean>?=null,
    var selectedChoiceIndex: MutableState<Int>?=null,

    val title:String?=null,
    val article:String?=null,
    val options: List<String>?=null,
    val choices: MutableState<MutableList<MutableState<Pair<String, String?>>>>?=null
)