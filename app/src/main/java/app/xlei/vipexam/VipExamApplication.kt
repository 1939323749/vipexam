package app.xlei.vipexam

import android.app.Application
import app.xlei.vipexam.data.AppContainer
import app.xlei.vipexam.data.AppContainerImpl

class VipExamApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}