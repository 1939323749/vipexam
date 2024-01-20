package app.xlei.vipexam.ui.components

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun VipexamCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
){
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
    )
}