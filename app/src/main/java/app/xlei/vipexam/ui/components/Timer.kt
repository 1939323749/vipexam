package app.xlei.vipexam.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Timer view model
 * 计时器vm
 * @constructor Create empty Timer view model
 */
class TimerViewModel : ViewModel() {
    private val _time = MutableStateFlow(0)
    val time: StateFlow<Int> = _time

    private var timerJob: Job? = null

    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _time.value++
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    fun resetTimer() {
        _time.value = 0
    }
}

/**
 * Timer
 * 计时器
 * @param timerViewModel 计时器vm
 * @param isTimerStart 开始
 * @param isResetTimer 结束
 * @param modifier
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Timer(
    timerViewModel: TimerViewModel = viewModel(),
    isTimerStart: Boolean,
    isResetTimer: Boolean,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val time by timerViewModel.time.collectAsState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = formatTime(time), style = MaterialTheme.typography.labelLarge)
    }
    if (isTimerStart)
        timerViewModel.startTimer()
    else
        timerViewModel.stopTimer()

    if (isResetTimer)
        timerViewModel.resetTimer()
}

@SuppressLint("DefaultLocale")
private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}