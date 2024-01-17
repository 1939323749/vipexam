package app.xlei.vipexam

import android.app.Application
import app.xlei.vipexam.core.data.util.Preferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VipExamApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Preferences.initialize(this)
        //DB.provide(this)
    }
}