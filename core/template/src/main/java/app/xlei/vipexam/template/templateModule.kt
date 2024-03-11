package app.xlei.vipexam.template

import app.xlei.vipexam.template.cloze.ClozeUiState
import app.xlei.vipexam.template.read.ReadUiState
import app.xlei.vipexam.template.readCloze.ReadClozeUiState
import app.xlei.vipexam.template.translate.TranslateUiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class templateModule {
    @Provides
    fun providesClozeUiState() = ClozeUiState(clozes = emptyList())

    @Provides
    fun providesReadUiState() = ReadUiState(articles = emptyList())

    @Provides
    fun providesReadClozeUiState() = ReadClozeUiState(articles = emptyList())

    @Provides
    fun providesTranslateUiState() = TranslateUiState(translations = emptyList())
}