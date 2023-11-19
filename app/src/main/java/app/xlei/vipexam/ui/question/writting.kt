package app.xlei.vipexam.ui.question

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xlei.vipexam.data.Muban

@Composable
fun Ecswriting(muban: Muban){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
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
        ) {
            Text(
                muban.shiti[0].primQuestion,
                modifier = Modifier
                    .padding(start = 4.dp, end = 4.dp)
            )
        }
    }
}