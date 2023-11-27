package app.xlei.vipexam.data

import android.content.Context
import app.xlei.vipexam.data.models.room.db.SettingDatabase
import app.xlei.vipexam.data.models.room.db.UserDatabase
import app.xlei.vipexam.repository.Repository

interface AppContainer{
    val repository: Repository
}

class AppContainerImpl(applicationContext: Context) : AppContainer {
    private lateinit var user_db: UserDatabase
    private lateinit var setting_db: SettingDatabase

    override val repository by lazy {
        Repository(
            userDao = user_db.userDao(),
            settingDao = setting_db.settingDao()
        )
    }

    init{
        user_db = UserDatabase.getDatabase(applicationContext)
        setting_db = SettingDatabase.getDatabase(applicationContext)
    }

}