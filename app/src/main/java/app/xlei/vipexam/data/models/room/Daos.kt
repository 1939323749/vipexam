package app.xlei.vipexam.data.models.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE account=:account")
    fun getUser(account: String): User
}

@Dao
interface SettingDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: Setting)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(setting: Setting)

    @Query("SELECT * FROM vipexam_settings LIMIT 1")
    fun getSetting(): Setting
}