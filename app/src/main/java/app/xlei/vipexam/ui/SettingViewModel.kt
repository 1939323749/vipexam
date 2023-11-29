package app.xlei.vipexam.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.data.Shiti
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

//class SettingRepository {
//    lateinit var settings: Flow<SettingsUiState>
//
//    fun setSetting(string: SettingsUiState){
//        settings= flowOf(string)
//    }
//}
//
//sealed interface SettingsUiState {
//    data object Loading : SettingsUiState
//    data class Success(val setting: String) : SettingsUiState
//}
//@HiltViewModel
//class SettingViewModel @Inject constructor(
//    private val settingRepository: SettingRepository
//) : ViewModel(){
//    val settingsUiState: StateFlow<SettingsUiState> = settingRepository
//        .settings
//        .stateIn(
//            viewModelScope,
//            SharingStarted.Eagerly,
//            initialValue = SettingsUiState.Loading
//        )
//
//    fun updateSetting(settingsUiState: SettingsUiState){
//        viewModelScope.launch {
//            settingRepository.setSetting(settingsUiState)
//        }
//    }
//}
//
//@Composable
//fun setting(
//    viewModel: SettingViewModel = hiltViewModel()
//){
//    val settingsUiState by viewModel.settingsUiState.collectAsState()
//
//    Column {
//        Button(
//            onClick = { viewModel.updateSetting(SettingsUiState.Loading) }
//        ){
//            Text("load")
//        }
//        Button(
//            onClick = { viewModel.updateSetting(SettingsUiState.Success("test"))}
//        ){
//            Text("success")
//        }
//        when (settingsUiState) {
//            SettingsUiState.Loading -> Text("loading")
//            SettingsUiState.Success("test") -> Text("123")
//            else -> Spacer(Modifier)
//        }
//    }
//}
//
//@Module
//@InstallIn(SettingViewModel::class)
//object SettingModel {
//    @Provides
//    fun provideSettingRepository(): SettingRepository {
//        val settingRepository = SettingRepository()
//        settingRepository.settings= flowOf(SettingsUiState.Loading)
//        return settingRepository
//    }
//}

interface VipExamApi {
    suspend fun getExamList(page: Int, type: String): List<String>
}

class VipExam @Inject constructor(
    private val api: VipExamApi
) {
    suspend fun getExamList(page: Int, type: String) = api.getExamList(page, type)
}

class fakeVipExamApi : VipExamApi {
    override suspend fun getExamList(page: Int, type: String): List<String> {
        return listOf("1", "2", "3")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object module {
    @Provides
    fun provideVipExamApi(): VipExamApi {
        return fakeVipExamApi()
    }

    @Provides
    fun provideMuban(): Muban {
        return Muban(
            basic = "1",
            ename = "ecswriting",
            cname = "123",
            cunt = 1,
            gradel = "1",
            shiti = listOf(
                Shiti(
                    answerPic = "",
                    audioFiles = "",
                    children = emptyList(),
                    discPic = "",
                    discription = "",
                    fifth = "",
                    fifthPic = "",
                    first = "",
                    firstPic = "",
                    fourth = "",
                    fourthPic = "",
                    groupCodePrimQuestion = "",
                    isCollect = "",
                    originalText = "",
                    primPic = "",
                    primQuestion = "1234",
                    questionCode = "",
                    refAnswer = "5678",
                    second = "",
                    secondPic = "",
                    secondQuestion = "",
                    subjectTypeEname = "",
                    subPrimPic = "",
                    third = "",
                    thirdPic = ""
                )
            )
        )
    }
}

@HiltViewModel
class VipExamViewModel @Inject constructor(
    private val vipExam: VipExam
) : ViewModel() {
    private lateinit var examList: List<String>

    init {
        viewModelScope.launch {
            examList = vipExam.getExamList(1, "2")
        }
    }

    fun getExamList() = examList
}

@Composable
fun examList(
    viewModel: VipExamViewModel = hiltViewModel()
) {
    val examList = viewModel.getExamList()

    Column {
        examList.forEach {
            Button(onClick = {}) { Text(it) }
        }
    }

}