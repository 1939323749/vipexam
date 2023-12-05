package app.xlei.vipexam.data

import android.content.Context
import app.xlei.vipexam.data.models.room.db.UserDatabase
import app.xlei.vipexam.repository.Repository

interface AppContainer{
    val repository: Repository
}

class AppContainerImpl(applicationContext: Context) : AppContainer {
    private lateinit var user_db: UserDatabase

    override val repository by lazy {
        Repository(
            userDao = user_db.userDao()
        )
    }

    init{
        user_db = UserDatabase.getDatabase(applicationContext)
    }

}