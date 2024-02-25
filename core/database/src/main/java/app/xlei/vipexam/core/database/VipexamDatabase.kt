package app.xlei.vipexam.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import app.xlei.vipexam.core.database.dao.BookmarkDao
import app.xlei.vipexam.core.database.dao.ExamHistoryDao
import app.xlei.vipexam.core.database.dao.UserDao
import app.xlei.vipexam.core.database.dao.WordDao
import app.xlei.vipexam.core.database.module.Bookmark
import app.xlei.vipexam.core.database.module.ExamHistory
import app.xlei.vipexam.core.database.module.User
import app.xlei.vipexam.core.database.module.Word

@Database(
    entities = [
        Word::class,
        User::class,
        ExamHistory::class,
        Bookmark::class,
    ],
    version = 1
)
abstract class VipexamDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun userDao(): UserDao
    abstract fun examHistoryDao(): ExamHistoryDao
    abstract fun bookmarkDao(): BookmarkDao
}