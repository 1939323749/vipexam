package app.xlei.vipexam.feature.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.feature.settings.components.EudicApiKeyDialog
import app.xlei.vipexam.feature.settings.components.LanguagePreferenceDialog
import app.xlei.vipexam.feature.settings.components.LongPressActionDialog
import app.xlei.vipexam.feature.settings.components.OrganizationDialog
import app.xlei.vipexam.feature.settings.components.PreferenceItem
import app.xlei.vipexam.feature.settings.components.SettingsCategory
import app.xlei.vipexam.feature.settings.components.ShowAnswerDialog
import app.xlei.vipexam.feature.settings.components.SwitchPreference
import app.xlei.vipexam.feature.settings.components.ThemeModeDialog
import app.xlei.vipexam.preference.DataStoreKeys
import app.xlei.vipexam.preference.LocalVibrate
import app.xlei.vipexam.preference.VibratePreference
import compose.icons.FeatherIcons
import compose.icons.feathericons.Menu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showLanguagePreference by remember {
        mutableStateOf(false)
    }

    var showThemeOptions by remember {
        mutableStateOf(false)
    }

    var showShowAnswerOptions by remember {
        mutableStateOf(false)
    }

    var showLongPressActions by remember {
        mutableStateOf(false)
    }

    var showOrganizationDialog by remember {
        mutableStateOf(false)
    }

    var showEudicApiKeyDialog by remember {
        mutableStateOf(false)
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = modifier
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
            }

            item {
                PreferenceItem(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    title = stringResource(id = R.string.app_language),
                    summary = stringResource(id = R.string.app_language),
                ) {
                    showLanguagePreference = true
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

            item {
                SwitchPreference(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    title = stringResource(id = R.string.vibrate),
                    summary = stringResource(id = R.string.vibrate_summary),
                    checked = LocalVibrate.current == VibratePreference.On,
                    preferencesKey = DataStoreKeys.Vibrate
                )
            }

            item {
                PreferenceItem(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    title = stringResource(id = R.string.organiztion),
                    summary = stringResource(id = R.string.edit_organiztion)
                ) {
                    showOrganizationDialog = true
                }
            }

            item {
                SettingsCategory(stringResource(R.string.advanced))
            }

            item {
                PreferenceItem(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    title = stringResource(id = R.string.eudic),
                    summary = stringResource(id = R.string.edit_eudic_apikey),
                ) {
                    showEudicApiKeyDialog = true
                }
            }

        }

        if (showLanguagePreference)
            LanguagePreferenceDialog {
                showLanguagePreference = false
            }

        if (showThemeOptions)
            ThemeModeDialog {
                showThemeOptions = false
            }

        if (showShowAnswerOptions) {
            ShowAnswerDialog {
                showShowAnswerOptions = false
            }
        }

        if (showLongPressActions)
            LongPressActionDialog {
                showLongPressActions = false
            }

        if (showOrganizationDialog)
            OrganizationDialog {
                showOrganizationDialog = false
            }

        if (showEudicApiKeyDialog)
            EudicApiKeyDialog {
                showEudicApiKeyDialog = false
            }
    }
}