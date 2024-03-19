package app.xlei.vipexam.feature.wordlist.components

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.xlei.vipexam.feature.wordlist.R
import compose.icons.FeatherIcons
import compose.icons.feathericons.Clipboard

@Composable
fun CopyToClipboardButton(
    modifier: Modifier = Modifier,
    text: String,
) {
    val context = LocalContext.current
    val string = stringResource(R.string.CopyToClipboardSuccess)

    fun copyToClipboard(word: String) {
        context.copyToClipboard(word)
    }

    IconButton(
        onClick = {
            copyToClipboard(text)
            Toast.makeText(context, "$text $string", Toast.LENGTH_LONG).show()
        },
        modifier = modifier,
    ) {
        Icon(
            imageVector = FeatherIcons.Clipboard,
            contentDescription = null,
        )
    }
}

@SuppressLint("ServiceCast")
fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
}