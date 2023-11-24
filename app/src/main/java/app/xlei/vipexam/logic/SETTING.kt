package app.xlei.vipexam.logic

import android.content.Context
import app.xlei.vipexam.data.models.room.db.SettingDatabase
import app.xlei.vipexam.data.models.room.db.UserDatabase
import app.xlei.vipexam.repository.Repository

object SETTING {
    private lateinit var user_db: UserDatabase
    private lateinit var setting_db: SettingDatabase

    val repository by lazy {
        Repository(
            userDao = user_db.userDao(),
            settingDao = setting_db.settingDao()
        )
    }

    fun provide(context: Context){
        user_db = UserDatabase.getDatabase(context = context)
        setting_db = SettingDatabase.getDatabase(context = context)
    }
}