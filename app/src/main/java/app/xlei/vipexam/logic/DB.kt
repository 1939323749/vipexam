package app.xlei.vipexam.logic

import android.content.Context
import app.xlei.vipexam.data.models.room.db.UserDatabase
import app.xlei.vipexam.repository.Repository

object DB {
    private lateinit var user_db: UserDatabase

    val repository by lazy {
        Repository(
            userDao = user_db.userDao(),
        )
    }

    fun provide(context: Context){
        user_db = UserDatabase.getDatabase(context = context)
    }
}