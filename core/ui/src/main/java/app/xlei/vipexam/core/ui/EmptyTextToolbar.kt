package app.xlei.vipexam.core.ui

import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

/**
 * Empty text toolbar
 *
 * @property onHide
 * @property onSelect
 * in [app.xlei.vipexam.feature.wordlist.components.TranslationSheet], [VipexamTextToolbar] don't
 * work, we use this.
 */
class EmptyTextToolbar(
    private val onHide: (() -> Unit)? = null,
    private val onSelect: () -> Unit,
) : TextToolbar {
    override val status: TextToolbarStatus = TextToolbarStatus.Hidden

    override fun hide() {
        onHide?.invoke()
    }

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?,
    ) {
        onSelect.invoke()
        onCopyRequested?.invoke()
    }
}

class VipexamTextToolbar(
    private val onHide: (() -> Unit)? = null,
    private val view: View,
    onTranslate: () -> Unit,
) : TextToolbar {
    private var actionMode: ActionMode? = null
    private val textActionModeCallback: VipexamTextActionModeCallback =
        VipexamTextActionModeCallback(
            context = view.context,
            onActionModeDestroy = {
                actionMode = null
            },
            onTranslate = onTranslate
        )
    override var status: TextToolbarStatus = TextToolbarStatus.Hidden
        private set

    override fun hide() {
        status = TextToolbarStatus.Hidden
        actionMode?.finish()
        actionMode = null
        onHide?.invoke()
    }

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?,
    ) {
        textActionModeCallback.rect = rect
        textActionModeCallback.onCopyRequested = onCopyRequested
        textActionModeCallback.onCutRequested = onCutRequested
        textActionModeCallback.onPasteRequested = onPasteRequested
        textActionModeCallback.onSelectAllRequested = onSelectAllRequested
        if (actionMode == null) {
            status = TextToolbarStatus.Shown
            actionMode =
                view.startActionMode(
                    FloatingTextActionModeCallback(textActionModeCallback),
                    ActionMode.TYPE_FLOATING,
                )
        } else {
            actionMode?.invalidate()
        }
    }
}

class VipexamTextActionModeCallback(
    val context: Context,
    val onActionModeDestroy: (() -> Unit)? = null,
    var rect: Rect = Rect.Zero,
    var onCopyRequested: (() -> Unit)? = null,
    var onPasteRequested: (() -> Unit)? = null,
    var onCutRequested: (() -> Unit)? = null,
    var onSelectAllRequested: (() -> Unit)? = null,
    val onTranslate: () -> Unit,
) : ActionMode.Callback {
    private val displayNameComparator by lazy {
        ResolveInfo.DisplayNameComparator(packageManager)
    }
    private val packageManager by lazy {
        context.packageManager
    }
    private val clipboardManager by lazy {
        context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    private val textProcessors = mutableListOf<ComponentName>()

    override fun onCreateActionMode(
        mode: ActionMode?,
        menu: Menu?,
    ): Boolean {
        requireNotNull(menu)
        requireNotNull(mode)
        onCopyRequested?.let {
            menu.add(
                0,
                0,
                0,
                R.string.translate
            )
            addTextProcessors(menu)
        }
        return true
    }

    override fun onPrepareActionMode(
        mode: ActionMode?,
        menu: Menu?,
    ): Boolean {
        if (mode == null || menu == null) return false
        updateMenuItems(menu)
        return true
    }

    override fun onActionItemClicked(
        mode: ActionMode?,
        item: MenuItem?,
    ): Boolean {
        when (val itemId = item!!.itemId) {
            0 -> {
                onCopyRequested?.invoke()
                onTranslate.invoke()
            }

            else -> {
                if (itemId < 100) return false
                onCopyRequested?.invoke()
                val clip = clipboardManager.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    textProcessors.getOrNull(itemId - 100)?.let { cn ->
                        context.startActivity(
                            Intent(Intent.ACTION_PROCESS_TEXT).apply {
                                type = "text/plain"
                                component = cn
                                putExtra(Intent.EXTRA_PROCESS_TEXT, clip.getItemAt(0).text)
                            }
                        )
                    }
                }
            }
        }
        mode?.finish()
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        onActionModeDestroy?.invoke()
    }

    private fun addTextProcessors(menu: Menu) {
        textProcessors.clear()


        val intentActionProcessText = Intent(Intent.ACTION_PROCESS_TEXT).apply {
            type = "text/plain"
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                intentActionProcessText,
                PackageManager.ResolveInfoFlags.of(0L)
            )
        } else {
            packageManager.queryIntentActivities(intentActionProcessText, 0)
        }
            .sortedWith(displayNameComparator)
            .forEachIndexed { index, info ->
                val label = info.loadLabel(packageManager)
                val id = 100 + index
                if (menu.findItem(id) == null) {
                    // groupId, itemId, order, title
                    menu.add(1, id, id, label)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                }

                textProcessors.add(
                    ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name,
                    ),
                )
            }
    }

    private fun updateMenuItems(menu: Menu) {
        onCopyRequested?.let {
            addTextProcessors(menu)
        }
    }
}

internal class FloatingTextActionModeCallback(
    private val callback: VipexamTextActionModeCallback,
) : ActionMode.Callback2() {
    override fun onActionItemClicked(
        mode: ActionMode?,
        item: MenuItem?,
    ): Boolean {
        return callback.onActionItemClicked(mode, item)
    }

    override fun onCreateActionMode(
        mode: ActionMode?,
        menu: Menu?,
    ): Boolean {
        return callback.onCreateActionMode(mode, menu)
    }

    override fun onPrepareActionMode(
        mode: ActionMode?,
        menu: Menu?,
    ): Boolean {
        return callback.onPrepareActionMode(mode, menu)
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        callback.onDestroyActionMode(mode)
    }

    override fun onGetContentRect(
        mode: ActionMode?,
        view: View?,
        outRect: android.graphics.Rect?,
    ) {
        val rect = callback.rect
        outRect?.set(
            rect.left.toInt(),
            rect.top.toInt(),
            rect.right.toInt(),
            rect.bottom.toInt(),
        )
    }
}