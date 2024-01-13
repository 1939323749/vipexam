package app.xlei.vipexam.core.data.repository

import android.util.Log
import app.xlei.vipexam.core.database.dao.WordDao
import app.xlei.vipexam.core.database.module.Word
import javax.inject.Inject

private const val TAG = "WORDREPOSITORY"

class WordRepository @Inject constructor(
    private val wordDao: WordDao
) : Repository<Word> {
    override fun getAll() = run {
        wordDao.getAllWords()
    }

    override suspend fun add(item: Word) = run {
        Log.d(TAG, "add word ${item.word}")
        wordDao.insert(item)
    }

    override suspend fun remove(item: Word) = wordDao.delete(item)
}