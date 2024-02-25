package app.xlei.vipexam.ui.question

import app.xlei.vipexam.R
import app.xlei.vipexam.ui.UiState
import app.xlei.vipexam.ui.VipexamUiState
import app.xlei.vipexam.ui.appbar.AppBarTitle
import app.xlei.vipexam.ui.question.cloze.ClozeUiState
import app.xlei.vipexam.ui.question.listening.ListeningUiState
import app.xlei.vipexam.ui.question.qread.QreadUiState
import app.xlei.vipexam.ui.question.translate.TranslateUiState
import app.xlei.vipexam.ui.question.writing.WritingUiState
import app.xlei.vipexam.ui.question.zread.ZreadUiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object QuestionModule {
    @Provides
    fun provideExamUiState() = VipexamUiState(
        loginUiState = UiState.Loading(R.string.loading),
        examTypeListUiState = UiState.Loading(R.string.loading),
        examListUiState = UiState.Loading(R.string.loading),
        questionListUiState = UiState.Loading(R.string.loading),
        title = AppBarTitle.Login
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