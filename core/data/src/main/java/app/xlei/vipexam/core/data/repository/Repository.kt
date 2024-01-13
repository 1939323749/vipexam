package app.xlei.vipexam.core.data.repository

import app.xlei.vipexam.core.database.module.Word
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getAllWords(): Flow<List<Word>>
    suspend fun addWord(word: Word)
    suspend fun removeWord(word: Word)
}