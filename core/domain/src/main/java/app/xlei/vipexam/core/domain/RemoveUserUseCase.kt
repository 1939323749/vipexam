package app.xlei.vipexam.core.domain

import app.xlei.vipexam.core.data.repository.Repository
import app.xlei.vipexam.core.database.module.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val userRepository: Repository<User>,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private suspend fun deleteUser(user: User) {
        return userRepository.remove(user)
    }

    suspend operator fun invoke(user: User) = withContext(defaultDispatcher){
        deleteUser(user)
    }
}
