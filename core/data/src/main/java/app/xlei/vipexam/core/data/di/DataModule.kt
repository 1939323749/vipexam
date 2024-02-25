package app.xlei.vipexam.core.data.di

import app.xlei.vipexam.core.data.repository.BookmarkRepository
import app.xlei.vipexam.core.data.repository.BookmarkRepositoryImpl
import app.xlei.vipexam.core.data.repository.ExamHistoryRepository
import app.xlei.vipexam.core.data.repository.ExamHistoryRepositoryImpl
import app.xlei.vipexam.core.data.repository.Repository
import app.xlei.vipexam.core.data.repository.UserRepository
import app.xlei.vipexam.core.data.repository.WordRepository
import app.xlei.vipexam.core.data.util.ConnectivityManagerNetworkMonitor
import app.xlei.vipexam.core.data.util.NetworkMonitor
import app.xlei.vipexam.core.database.module.User
import app.xlei.vipexam.core.database.module.Word
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsWordRepository(
        wordRepository: WordRepository
    ): Repository<Word>

    @Binds
    fun bindsUserRepository(
        userRepository: UserRepository
    ): Repository<User>

    @Binds
    fun bindsExamHistoryRepository(
        examHistoryRepositoryImpl: ExamHistoryRepositoryImpl
    ): ExamHistoryRepository

    @Binds
    fun bindsBookmarkRepository(
        bookmarkRepository: BookmarkRepositoryImpl
    ): BookmarkRepository

    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor
}
