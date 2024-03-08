package app.xlei.vipexam.ui.components

import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable

/**
 * Vipexam checkbox
 *
 * @param checked
 * @param onCheckedChange
 */
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