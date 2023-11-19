package app.xlei.vipexam.ui.question

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xlei.vipexam.data.Children
import app.xlei.vipexam.data.Muban

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Ecsqread(muban: Muban){
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showOptionsSheet by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    val title = extractFirstPart(muban.shiti[0].primQuestion).trim()
    val article = extractSecondPart(muban.shiti[0].primQuestion)
    val options = getQreadOptions(muban.shiti[0].primQuestion)
    val choices by remember { mutableStateOf(getQreadChoices(muban.shiti[0].children)) }
    var selectedChoiceIndex by remember { mutableStateOf(-1) }

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
                .padding(12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .combinedClickable (
                    onClick = {},
                    onLongClick = {
                        showBottomSheet=true
                        haptics.performHapticFeedback(HapticFeedbackType(100))
                    }
                )
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = article,
                modifier = Modifier
                    .padding(12.dp)
            )
        }
        for((no,ti) in choices.withIndex()){
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        showOptionsSheet = true
                        selectedChoiceIndex = no
                    }
            ){
                Text(
                    text = "${no+1}. " + ti.value.first,
                    modifier = Modifier.padding(12.dp)
                )
                if(ti.value.second!=null){
                    SuggestionChip(
                        onClick = {
                            showOptionsSheet = true
                            selectedChoiceIndex = no
                        },
                        label = {
                            Text(
                                text = ti.value.second!!
                            )
                        }
                    )
                }
            }
        }

        if(showBottomSheet){
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet=false
                },
                sheetState= sheetState
            ){
                Column (
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ){
                    for((no,ti) in muban.shiti[0].children.withIndex()){
                        Column (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ){
                            Text(
                                text = "${no+1}. " + ti.secondQuestion,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        if(showOptionsSheet){
            ModalBottomSheet(
                onDismissRequest = {
                    showOptionsSheet=false
                },
                sheetState= sheetState
            ){
                Column (
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ){
                    for(option in options){
                        SuggestionChip(
                            onClick = {
                                choices[selectedChoiceIndex].value=choices[selectedChoiceIndex].value.first to option
                                showOptionsSheet = false
                            },
                            label = {
                                Text(option)
                            }
                        )
                    }
                }
            }
        }
    }
}

fun extractFirstPart(text: String): String {
    val lines = text.split("\n")
    if (lines.isNotEmpty()) {
        return lines.first()
    }
    return ""
}

fun extractSecondPart(text: String): String {
    val index = text.indexOf("\n")
    if (index != -1) {
        return text.substring(index + 1)
    }
    return ""
}

fun getQreadOptions(text: String):List<String>{
    val result = mutableListOf<String>()
    val pattern = Regex("""([A-Z])([)])""")
    val matches = pattern.findAll(text)
    for(match in matches){
        result.add(match.groupValues[1])
    }
    return result
}

fun getQreadChoices(children: List<Children>): MutableList<MutableState<Pair<String, String?>>> {
    val choices = mutableListOf<MutableState<Pair<String, String?>>>()

    for(ti in children){
        choices.add(mutableStateOf(ti.secondQuestion to null) )
    }

    return choices
}