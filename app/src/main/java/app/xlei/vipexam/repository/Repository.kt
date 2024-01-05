package app.xlei.vipexam.repository

import app.xlei.vipexam.data.models.room.User
import app.xlei.vipexam.data.models.room.UserDao
import app.xlei.vipexam.ui.page.Word
import app.xlei.vipexam.ui.page.WordDao

class Repository(
    private val userDao: UserDao,
    private val wordDao: WordDao,
) {
    fun getAllUsers() = userDao.getAllUsers()


    suspend fun insertUser(user: User) = userDao.insert(user)

    suspend fun deleteUser(user: User) = userDao.delete(user)

    fun getAllWords() = wordDao.getAllWords();
    suspend fun addWord(word: Word) = wordDao.insert(word)

    suspend fun removeWord(word: Word) = wordDao.delete(word)
}
