package app.xlei.vipexam.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DateText(time: Long?=null){
    time?.let {
        Text(
            text = getFormattedDate(it),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
@Composable
fun getFormattedDate(time: Long): String {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault())
    val now = LocalDateTime.now()
    val today = LocalDate.now()
    val date = dateTime.toLocalDate()
    val duration = Duration.between(dateTime, now)

    return when {
        duration.toMinutes() < 1 -> stringResource(id = R.string.just_now)
        duration.toHours() < 1 -> "${duration.toMinutes()}" + stringResource(id = R.string.minute_before)
        duration.toDays() < 1 -> "${duration.toHours()}" + stringResource(id = R.string.hour_before)
        date.year == today.year -> dateTime.format(DateTimeFormatter.ofPattern("MM-dd"))
        else -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}