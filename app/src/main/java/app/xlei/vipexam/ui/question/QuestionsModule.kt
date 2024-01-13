package app.xlei.vipexam.ui.question

import app.xlei.vipexam.core.network.module.Exam
import app.xlei.vipexam.core.network.module.ExamList
import app.xlei.vipexam.data.ExamUiState
import app.xlei.vipexam.ui.LoginSetting
import app.xlei.vipexam.ui.question.cloze.ClozeUiState
import app.xlei.vipexam.ui.question.listening.ListeningUiState
import app.xlei.vipexam.ui.question.qread.QreadUiState
import app.xlei.vipexam.ui.question.translate.TranslateUiState
import app.xlei.vipexam.ui.question.writing.WritingUiState
import app.xlei.vipexam.ui.question.zread.ZreadUiState
import app.xlei.vipexam.util.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.emptyFlow

@Module
@InstallIn(SingletonComponent::class)
object QuestionModule {
    @Provides
    fun provideExamUiState() = ExamUiState(
        examListUiState = ExamUiState.ExamListUiState(
            examType = 0,
            examList = ExamList(
                msg = "",
                code = "",
                count = 1,
                list = emptyList(),
                resourceType = 1,
            ),
            currentPage = "1",
            questionListUiState = null,
        ),
        examTypeListUiState = ExamUiState.ExamTypeListUiState(
            examListUiState = null,
            examTypeList = emptyList(),
        ),
        loginUiState = ExamUiState.LoginUiState(
            account = "",
            password = "",
            connectivity = false,
            loginResponse = null,
            setting = LoginSetting(
                isRememberAccount = Preferences.get(Preferences.rememberAccountKey, false),
                isAutoLogin = Preferences.get(Preferences.autoLoginKey, false),
            ),
            users = emptyFlow(),
        ),
        questionListUiState = ExamUiState.QuestionListUiState(
            exam = Exam(
                code = 1,
                count = 1,
                examID = "",
                examName = "",
                examstyle = "",
                examTypeCode = "",
                timelimit = 0,
                msg = "",
                muban = emptyList(),
                planID = "",
            ),
            question = null,
            questions = emptyList(),
        ),
        title = ""
    )

    @Provides
    fun provideClozeUiState() = ClozeUiState(
        clozes = emptyList()
    )

    @Provides
    fun provideListeningUiState() = ListeningUiState(
        listenings = emptyList()
    )

    @Provides
    fun provideQreadUiState() = QreadUiState(
        articles = emptyList()
    )

    @Provides
    fun provideTranslateUiState() = TranslateUiState(
        translations = emptyList()
    )

    @Provides
    fun provideWritingUiState() = WritingUiState(
        writings = emptyList()
    )

    @Provides
    fun provideZreadUiState() = ZreadUiState(
        articles = emptyList()
    )

    @Provides
    fun provideQuestionsUiState() = QuestionsUiState()
}