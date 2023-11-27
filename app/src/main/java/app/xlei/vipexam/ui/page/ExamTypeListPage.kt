package app.xlei.vipexam.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.xlei.vipexam.constant.Constants

@Composable
fun examTypeListView(
    onExamTypeClicked:(String)->Unit,
    onFirstItemHidden: ()->Unit,
    onFirstItemAppear: ()->Unit
) {
    val scrollState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }

    Scaffold {padding->
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .padding(padding)
        ) {
            items(Constants.EXAMTYPES.size){
                ListItem(
                    headlineContent = { Text(stringResource(Constants.EXAMTYPES[it].first) )  },
                    modifier = Modifier
                        .clickable {
                            onExamTypeClicked(Constants.EXAMTYPES[it].second)
                        }
                )
                HorizontalDivider()
            }
        }

        if (firstVisibleItemIndex > 0)
            onFirstItemHidden()
        else
            onFirstItemAppear()
    }
}