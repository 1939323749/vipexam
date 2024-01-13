package app.xlei.vipexam.core.data.repository

import kotlinx.coroutines.flow.Flow

interface Repository<T> {
    fun getAll(): Flow<List<T>>
    suspend fun add(item: T)
    suspend fun remove(item: T)
}