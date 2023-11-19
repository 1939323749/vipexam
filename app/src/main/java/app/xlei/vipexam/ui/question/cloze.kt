package app.xlei.vipexam.ui.question

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xlei.vipexam.data.Children
import app.xlei.vipexam.data.Muban
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Ecscloze(
    muban: Muban
){
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showOptionsSheet by remember { mutableStateOf(false) }
    val options = getClozeOptions(muban.shiti[0].primQuestion)
    val choices by remember { mutableStateOf(getClozeChoices(muban.shiti[0].children)) }
    var selectedChoiceIndex by remember { mutableStateOf(-1) }
    val haptics = LocalHapticFeedback.current

    Column{
        Column {
            Text(
                muban.cname,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(start = 12.dp)
            )
            HorizontalDivider(
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp),
                thickness = 1.dp,
                color = Color.Gray
            )
            Column(
                modifier = Modifier
                    .padding(top = 12.dp,start = 12.dp, end = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            showOptionsSheet=true
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
            ) {
                Text(
                    muban.shiti[0].primQuestion,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                )
            }
            for (index in choices.indices){
                SuggestionChip(
                    onClick = {
                        showBottomSheet=true
                        selectedChoiceIndex = index
                        haptics.performHapticFeedback(HapticFeedbackType(100))
                    },
                    label = {
                        Text(
                            text = choices[index].value.first +
                                    if(choices[index].value.second==null){
                                        ""
                                    }else{
                                        choices[index].value.second
                                    }
                        )
                    }
                )
            }
        }

        if(showOptionsSheet){
            ModalBottomSheet(
                onDismissRequest = {
                    showOptionsSheet = false
                },
                sheetState = sheetState
            ){
                for (option in options){
                    Text(
                        text = "[${option.first}]${option.second}",
                        modifier = Modifier
                            .padding(4.dp)
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                for (option in options){
                    SuggestionChip(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                            if (selectedChoiceIndex != -1) {
                                choices[selectedChoiceIndex].value =choices[selectedChoiceIndex].value.first to option.second
                            }
                        },
                        label = {
                            Text("[${option.first}]${option.second}")
                        }
                    )
                }
            }
        }
    }
}

fun getClozeChoices(children: List<Children>): MutableList<MutableState<Pair<String, String?>>> {
    val choices = mutableListOf<MutableState<Pair<String, String?>>>()

    for(ti in children){
        choices.add(mutableStateOf(ti.secondQuestion to null) )
    }

    return choices
}

fun getClozeOptions(text: String):List<Pair<String,String>>{
    val pattern = Regex("""([A-O])\)\s*([^A-O]+)""")
    val matches = pattern.findAll(text)
    val options = mutableListOf<Pair<String, String>>()

    for (match in matches) {
        val option = match.groupValues[1]
        val optionText = match.groupValues[2].trim()
        options.add(option to optionText)
    }

    return options.sortedBy { it.first }
}