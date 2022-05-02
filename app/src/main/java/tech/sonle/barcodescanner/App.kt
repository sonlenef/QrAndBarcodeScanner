package tech.sonle.barcodescanner

import androidx.multidex.MultiDexApplication
import tech.sonle.barcodescanner.di.settings
import io.reactivex.plugins.RxJavaPlugins

class App : MultiDexApplication() {

    override fun onCreate() {
        applyTheme()
        super.onCreate()
    }

    private fun applyTheme() {
        settings.reapplyTheme()
    }
}