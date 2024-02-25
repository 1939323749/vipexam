package app.xlei.vipexam.core.domain

import app.xlei.vipexam.core.data.repository.Repository
import app.xlei.vipexam.core.database.module.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val userRepository: Repository<User>,
) {
    private fun getAllUsers(): Flow<List<User>> {
        return userRepository.getAll()
    }

    operator fun invoke() = getAllUsers()
}



