package tech.sonle.barcodescanner

import androidx.multidex.MultiDexApplication
import com.google.android.gms.ads.MobileAds
import tech.sonle.barcodescanner.di.settings

class App : MultiDexApplication() {

    override fun onCreate() {
        applyTheme()
        MobileAds.initialize(
            this
        ) {}
        super.onCreate()
    }

    private fun applyTheme() {
        settings.reapplyTheme()
    }
}