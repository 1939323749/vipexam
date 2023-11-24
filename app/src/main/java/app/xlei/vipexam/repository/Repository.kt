package app.xlei.vipexam.repository

import app.xlei.vipexam.data.models.room.Setting
import app.xlei.vipexam.data.models.room.SettingDao
import app.xlei.vipexam.data.models.room.User
import app.xlei.vipexam.data.models.room.UserDao

class Repository (
    private val userDao:UserDao,
    private val settingDao: SettingDao,
) {
    fun getAllUsers() = userDao.getAllUsers()

    fun getSetting() = settingDao.getSetting()

    suspend fun insertUser(user:User) = userDao.insert(user)

    suspend fun deleteUser(user: User) = userDao.delete(user)

    suspend fun insertSetting(setting: Setting)  = settingDao.insert(setting)

    suspend fun updateSetting(setting: Setting) = settingDao.update(setting)
}