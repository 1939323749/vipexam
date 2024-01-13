package app.xlei.vipexam.core.data.repository

import android.util.Log
import app.xlei.vipexam.core.database.dao.WordDao
import app.xlei.vipexam.core.database.module.Word
import javax.inject.Inject

private const val TAG = "WORDREPOSITORY"

class WordRepository @Inject constructor(
    private val wordDao: WordDao
) : Repository {
    override fun getAllWords() = run {
        wordDao.getAllWords()
    }

    override suspend fun addWord(word: Word) = run {
        Log.d(TAG, "add word ${word.word}")
        wordDao.insert(word)
    }

    override suspend fun removeWord(word: Word) = wordDao.delete(word)
}