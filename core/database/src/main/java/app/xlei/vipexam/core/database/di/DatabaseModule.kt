package app.xlei.vipexam.core.database.di

import android.content.Context
import androidx.room.Room
import app.xlei.vipexam.core.database.VipexamDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesVipexamDatabase(
        @ApplicationContext context: Context
    ): VipexamDatabase = Room.databaseBuilder(
        context,
        VipexamDatabase::class.java,
        "vipexam-database"
    ).build()
    @Singleton
    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}