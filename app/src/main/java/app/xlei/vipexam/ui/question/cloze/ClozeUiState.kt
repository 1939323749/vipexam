package app.xlei.vipexam.ui.question.cloze

import androidx.compose.runtime.*
import app.xlei.vipexam.data.Muban

data class ClozeUiState(
    val muban: Muban?=null,
    var showBottomSheet:MutableState<Boolean>?=null,
    var showOptionsSheet:MutableState<Boolean>?=null,
    val options :List<Pair<String,String>>?=null,
    val choices : MutableList<MutableState<Pair<String, String?>>>?=null,
    var selectedChoiceIndex :MutableState<Int>?=null,
)