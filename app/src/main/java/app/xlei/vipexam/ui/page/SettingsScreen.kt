package app.xlei.vipexam.ui.page

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import app.xlei.vipexam.MainActivity
import app.xlei.vipexam.R
import app.xlei.vipexam.constant.ThemeMode
import app.xlei.vipexam.ui.theme.defaultAccentColor
import app.xlei.vipexam.ui.theme.hexToColor
import app.xlei.vipexam.util.LocaleHelper
import app.xlei.vipexam.util.Preferences
import compose.icons.FeatherIcons
import compose.icons.TablerIcons
import compose.icons.feathericons.Menu
import compose.icons.tablericons.Palette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    openDrawer: () -> Unit,
) {
    val context = LocalContext.current

    var showThemeOptions by remember {
        mutableStateOf(false)
    }

    var showAccentColorDialog by remember {
        mutableStateOf(false)
    }

    var showShowAnswerOptions by remember {
        mutableStateOf(false)
    }

    var showLongPressActions by remember {
        mutableStateOf(false)
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.settings)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = openDrawer
                    ) {
                        Icon(
                            imageVector = FeatherIcons.Menu,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    StyledIconButton(
                        imageVector = TablerIcons.Palette
                    ) {
                        showAccentColorDialog = true
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            item {
                SettingsCategory(stringResource(R.string.general))

                val appLanguages = LocaleHelper.getLanguages(context)

                ListPreference(
                    title = stringResource(R.string.app_language),
                    preferenceKey = Preferences.appLanguageKey,
                    defaultValue = "",
                    entries = appLanguages.map { it.name },
                    values = appLanguages.map { it.code }
                ) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        (context as MainActivity).recreate()
                    }, 100)
                }
            }

            item {
                PreferenceItem(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    title = stringResource(R.string.app_theme),
                    summary = stringResource(R.string.app_theme_summary)
                ) {
                    showThemeOptions = true
                }
            }

            item {
                PreferenceItem(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    title = stringResource(R.string.show_answer),
                    summary = stringResource(R.string.show_answer_summary)
                ) {
                    showShowAnswerOptions = true
                }
            }

            item {
                PreferenceItem(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    title = stringResource(R.string.long_press_action),
                    summary = stringResource(R.string.long_press_action)
                ) {
                    showLongPressActions = true
                }
            }

        }

        if (showThemeOptions)
            ThemeModeDialog { showThemeOptions = false }

        if (showAccentColorDialog) {
            AccentColorPrefDialog {
                showAccentColorDialog = false
            }
        }

        if (showShowAnswerOptions) {
            ShowAnswerDialog {
                showShowAnswerOptions = false
            }
        }

        if (showLongPressActions)
            longPressActionDialog {
                showLongPressActions = false
            }
    }
}

enum class LongPressActions(
    val value: Int
) {
    SHOW_QUESTION(0), TRANSLATE(1)
}

@Composable
fun longPressActionDialog(
    onDismiss: () -> Unit
) {
    val longPressAction =
        Preferences.get(Preferences.longPressActionKey, LongPressActions.SHOW_QUESTION.value)
    ListPreferenceDialog(
        title = stringResource(R.string.long_press_action),
        preferenceKey = Preferences.longPressActionKey,
        onDismissRequest = {
            onDismiss.invoke()
        },
        options = listOf(
            ListPreferenceOption(
                name = stringResource(R.string.show_question),
                value = LongPressActions.SHOW_QUESTION.value,
                isSelected = longPressAction == LongPressActions.SHOW_QUESTION.value
            ),
            ListPreferenceOption(
                name = stringResource(R.string.show_translation),
                value = LongPressActions.TRANSLATE.value,
                isSelected = longPressAction == LongPressActions.TRANSLATE.value
            ),
        ),
        onOptionSelected = {
            Preferences.put(Preferences.longPressActionKey, it.value)
        }
    )
}

enum class ShowAnswerOptions(val value: Int) {
    ALWAYS(0), ONCE(1)
}

@Composable
fun ShowAnswerDialog(
    onDismiss: () -> Unit
) {
    val showAnswer = Preferences.get(Preferences.alwaysShowAnswerKey, ShowAnswerOptions.ONCE.value)
    ListPreferenceDialog(
        title = stringResource(R.string.show_answer),
        preferenceKey = Preferences.alwaysShowAnswerKey,
        onDismissRequest = {
            onDismiss.invoke()
        },
        options = listOf(
            ListPreferenceOption(
                name = stringResource(R.string.always),
                value = ShowAnswerOptions.ALWAYS.value,
                isSelected = showAnswer == ShowAnswerOptions.ALWAYS.value
            ),
            ListPreferenceOption(
                name = stringResource(R.string.once),
                value = ShowAnswerOptions.ONCE.value,
                isSelected = showAnswer == ShowAnswerOptions.ONCE.value
            ),
        ),
        onOptionSelected = {
            Preferences.put(Preferences.alwaysShowAnswerKey, it.value)
        }
    )
}

@Composable
fun ThemeModeDialog(
    onDismiss: () -> Unit
) {
    val activity = LocalContext.current as MainActivity
    ListPreferenceDialog(
        title = stringResource(R.string.select_theme),
        preferenceKey = Preferences.themeModeKey,
        onDismissRequest = {
            onDismiss.invoke()
        },
        options = listOf(
            ListPreferenceOption(
                name = stringResource(R.string.theme_auto),
                value = ThemeMode.AUTO.value,
                isSelected = activity.themeMode == ThemeMode.AUTO
            ),
            ListPreferenceOption(
                name = stringResource(R.string.theme_light),
                value = ThemeMode.LIGHT.value,
                isSelected = activity.themeMode == ThemeMode.LIGHT
            ),
            ListPreferenceOption(
                name = stringResource(R.string.theme_dark),
                value = ThemeMode.DARK.value,
                isSelected = activity.themeMode == ThemeMode.DARK
            ),
            ListPreferenceOption(
                name = stringResource(R.string.theme_black),
                value = ThemeMode.BLACK.value,
                isSelected = activity.themeMode == ThemeMode.BLACK
            )
        ),
        onOptionSelected = {
            activity.themeMode = ThemeMode.entries.toTypedArray()[it.value]
        }
    )
}

@Composable
fun SettingsCategory(
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 20.dp,
                bottom = 5.dp,
                end = 5.dp
            )
    ) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun ListPreference(
    title: String,
    summary: String? = null,
    preferenceKey: String,
    defaultValue: String,
    entries: List<String>,
    values: List<String>,
    onOptionSelected: (ListPreferenceOption) -> Unit = {}
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    var selectedIndex by remember {
        mutableStateOf(values.indexOf(Preferences.get(preferenceKey, defaultValue)))
    }

    PreferenceItem(
        title = title,
        summary = summary ?: entries.getOrElse(selectedIndex) { entries.first() },
        modifier = Modifier.fillMaxWidth()
    ) {
        showDialog = true
    }

    if (showDialog) {
        ListPreferenceDialog(
            preferenceKey = preferenceKey,
            onDismissRequest = {
                showDialog = false
            },
            options = entries.mapIndexed { index, entry ->
                ListPreferenceOption(entry, index)
            },
            onOptionSelected = {
                Preferences.put(preferenceKey, values[it.value])
                selectedIndex = it.value
                onOptionSelected.invoke(it)
            },
            currentValue = selectedIndex
        )
    }
}

@Composable
fun ListPreferenceDialog(
    preferenceKey: String,
    onDismissRequest: () -> Unit,
    options: List<ListPreferenceOption>,
    currentValue: Int? = null,
    title: String? = null,
    onOptionSelected: (ListPreferenceOption) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            if (title != null)
                Text(title)
        },
        text = {
            LazyColumn {
                items(options) {
                    SelectableItem(
                        text = if (it.value == currentValue) "${it.name}   ✓" else it.name,
                        onClick = {
                            Preferences.put(
                                preferenceKey,
                                it.value.toString()
                            )
                            onOptionSelected.invoke(it)
                            onDismissRequest.invoke()
                        },
                        isSelected = it.isSelected
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest.invoke()
                }
            ) {
                Text(
                    stringResource(
                        R.string.cancel
                    )
                )
            }
        }
    )
}

data class ListPreferenceOption(
    val name: String,
    val value: Int,
    val isSelected: Boolean = false
)

@Composable
fun SelectableItem(
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(30.dp)
            )
            .clickable {
                onClick.invoke()
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )

    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Unspecified
        )
    }
}

@Composable
fun PreferenceItem(
    title: String,
    summary: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick.invoke()
            }
    ) {
        Column {
            Text(title)
            Spacer(Modifier.height(4.dp))
            Text(
                text = summary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun AccentColorPrefDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val supportsDynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    var color by remember {
        mutableStateOf(
            Preferences.getAccentColor() ?: run {
                if (supportsDynamicColors) null else defaultAccentColor
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            DialogButton(text = stringResource(R.string.okay)) {
                Preferences.prefs.edit(true) { putString(Preferences.accentColorKey, color) }
                (context as MainActivity).accentColor = color
                onDismissRequest.invoke()
            }
        },
        dismissButton = {
            DialogButton(text = stringResource(R.string.cancel)) {
                onDismissRequest.invoke()
            }
        },
        title = {
            Text(stringResource(R.string.accent_color))
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (supportsDynamicColors) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.dynamic_colors)
                        )
                        Switch(
                            checked = color == null,
                            onCheckedChange = { newValue ->
                                color = defaultAccentColor.takeIf { !newValue }
                            }
                        )
                    }
                }

                val isColorPickerEnabled = color != null
                val imageAlpha: Float by animateFloatAsState(
                    targetValue = if (isColorPickerEnabled) 1f else .5f,
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = LinearEasing,
                    ), label = ""
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .height(250.dp)
                        .alpha(imageAlpha)
                        .let {
                            if (isColorPickerEnabled) {
                                it
                            } else {
                                // disable input
                                it.pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            awaitPointerEvent(pass = PointerEventPass.Initial)
                                                .changes
                                                .forEach(PointerInputChange::consume)
                                        }
                                    }
                                }
                            }
                        }
                ) {
                    listOf("R", "G", "B").forEachIndexed { index, c ->
                        val startIndex = index * 2
                        ColorSlider(
                            label = c,
                            value = color?.substring(startIndex, startIndex + 2)?.toInt(16) ?: 0,
                            onChange = { colorInt ->
                                var newHex = colorInt.toHexString()
                                if (newHex.length == 1) newHex = "0$newHex"
                                color = StringBuilder(color).apply {
                                    setCharAt(startIndex, newHex[0])
                                    setCharAt(startIndex + 1, newHex[1])
                                }.toString()
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(50.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                        )
                        Text(text = "   =>   ", fontSize = 27.sp)
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    color?.hexToColor() ?: Color.Black,
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
    )
}

fun Int.toHexString(): String = Integer.toHexString(this)

@Composable
fun ColorSlider(
    label: String,
    value: Int,
    onChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Slider(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f),
            value = value.toFloat(),
            valueRange = 0f..255f,
            steps = 256,
            onValueChange = {
                onChange(it.toInt())
            }
        )
        Text(value.toString())
    }
}

@Composable
fun DialogButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Text(text)
    }
}

@Composable
fun StyledIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit = {}
) {
    IconButton(
        onClick = {
            onClick.invoke()
        }
    ) {
        Icon(
            modifier = modifier,
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}

