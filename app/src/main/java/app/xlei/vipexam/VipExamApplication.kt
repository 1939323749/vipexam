package app.xlei.vipexam

import android.app.Application
import app.xlei.vipexam.data.AppContainer
import app.xlei.vipexam.data.AppContainerImpl
import app.xlei.vipexam.util.Preferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VipExamApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        Preferences.initialize(this)
        container = AppContainerImpl(this)
    }
}