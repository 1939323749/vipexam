package app.xlei.vipexam.core.database.di

import app.xlei.vipexam.core.database.VipexamDatabase
import app.xlei.vipexam.core.database.dao.BookmarkDao
import app.xlei.vipexam.core.database.dao.ExamHistoryDao
import app.xlei.vipexam.core.database.dao.UserDao
import app.xlei.vipexam.core.database.dao.WordDao
import app.xlei.vipexam.core.database.module.ExamHistory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {
    @Provides
    fun providesWordDao(
        database: VipexamDatabase
    ): WordDao = database.wordDao()

    @Provides
    fun providesUserDao(
        database: VipexamDatabase
    ): UserDao = database.userDao()

    @Provides
    fun providesExamHistory(
        database: VipexamDatabase
    ): ExamHistoryDao = database.examHistoryDao()

    @Provides
    fun providesBookmark(
        database: VipexamDatabase
    ): BookmarkDao = database.bookmarkDao()
}