package app.xlei.vipexam.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import app.xlei.vipexam.core.database.dao.WordDao
import app.xlei.vipexam.core.database.module.Word

@Database(
    entities = [
        Word::class,
    ],
    version = 1
)
abstract class VipexamDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}