package app.xlei.vipexam.core.data.repository

import app.xlei.vipexam.core.database.dao.UserDao
import app.xlei.vipexam.core.database.module.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) : Repository<User> {
    override fun getAll() = userDao.getAllUsers()

    override suspend fun remove(item: User) = userDao.delete(item)

    override suspend fun add(item: User) = userDao.insert(item)

}
