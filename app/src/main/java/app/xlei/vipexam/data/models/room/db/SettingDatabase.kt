package app.xlei.vipexam.data.models.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.xlei.vipexam.data.models.room.Setting
import app.xlei.vipexam.data.models.room.SettingDao
import app.xlei.vipexam.data.models.room.UserDao

@Database(
    entities = [Setting::class],
    version = 1
)
abstract class SettingDatabase : RoomDatabase(){
    abstract fun settingDao(): SettingDao

    companion object{
        @Volatile
        var INSTANCE:SettingDatabase?=null
        fun getDatabase(context: Context):SettingDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context = context,
                    SettingDatabase::class.java,
                    "setting_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}