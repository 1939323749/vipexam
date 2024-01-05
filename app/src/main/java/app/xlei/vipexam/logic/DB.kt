package app.xlei.vipexam.logic

import android.content.Context
import app.xlei.vipexam.data.models.room.db.UserDatabase
import app.xlei.vipexam.repository.Repository
import app.xlei.vipexam.ui.page.WordDatabase

object DB {
    private lateinit var user_db: UserDatabase
    private lateinit var word_db: WordDatabase

    val repository by lazy {
        Repository(
            userDao = user_db.userDao(),
            wordDao = word_db.wordDao(),
        )
    }

    fun provide(context: Context){
        user_db = UserDatabase.getDatabase(context = context)
        word_db = WordDatabase.getDatabase(context = context)
    }
}