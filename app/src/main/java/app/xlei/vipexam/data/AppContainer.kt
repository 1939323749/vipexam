package app.xlei.vipexam.data

import android.content.Context
import app.xlei.vipexam.data.models.room.db.UserDatabase
import app.xlei.vipexam.repository.Repository
import app.xlei.vipexam.ui.page.WordDatabase

interface AppContainer{
    val repository: Repository
}

class AppContainerImpl(applicationContext: Context) : AppContainer {
    private lateinit var user_db: UserDatabase
    private lateinit var word_db: WordDatabase

    override val repository by lazy {
        Repository(
            userDao = user_db.userDao(),
            wordDao = word_db.wordDao()
        )
    }

    init{
        user_db = UserDatabase.getDatabase(applicationContext)
        word_db = WordDatabase.getDatabase(applicationContext)
    }

}