package app.xlei.vipexam.core.domain

import app.xlei.vipexam.core.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    @Singleton
    @Provides
    fun providesGetAllUsersUseCase(
        userRepository: UserRepository
    ) = GetAllUsersUseCase(userRepository)

    @Singleton
    @Provides
    fun providesAddUserUseCase(
        userRepository: UserRepository
    ) = AddUserUseCase(userRepository)

    @Singleton
    @Provides
    fun providesDeleteUserUseCase(
        userRepository: UserRepository
    ) = DeleteUserUseCase(userRepository)
}