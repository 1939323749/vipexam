package app.xlei.vipexam.ui.page

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xlei.vipexam.MainActivity
import app.xlei.vipexam.R
import app.xlei.vipexam.constant.ThemeMode
import app.xlei.vipexam.util.LocaleHelper
import app.xlei.vipexam.util.Preferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(

) {
    val context = LocalContext.current

    var showThemeOptions by remember {
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
                actions = {

                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
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
                    modifier = Modifier.padding(top = 10.dp),
                    title = stringResource(R.string.app_theme),
                    summary = stringResource(R.string.app_theme_summary)
                ) {
                    showThemeOptions = true
                }
            }

        }

        if (showThemeOptions)
            ThemeModeDialog { showThemeOptions = false }
    }
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
