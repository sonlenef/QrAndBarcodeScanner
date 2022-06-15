package tech.sonle.barcodescanner

import android.app.Activity
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import tech.sonle.barcodescanner.di.settings
import tech.sonle.barcodescanner.extension.Config.Companion.IS_SHOW_ADS


class App : MultiDexApplication() {

    var TAG = "my_" + javaClass.simpleName

    var interstitial: InterstitialAd? = null
    var flag = false
    var currentTime: Long = 0
    var lastTimeShowInter: Long = 0

    override fun onCreate() {
        applyTheme()
        MobileAds.initialize(
            this
        ) {}
        super.onCreate()

        loadInter()
    }

    private fun applyTheme() {
        settings.reapplyTheme()
    }

    fun loadInter() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            BuildConfig.CA_APP_PUB_APP_INTERSTITIAL,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    interstitial = interstitialAd
                    Log.d(TAG, "onAdLoaded: ")
                    interstitialAd.fullScreenContentCallback = object :
                        FullScreenContentCallback() {
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.d(TAG, "onAdFailedToShowFullScreenContent: ")
                            super.onAdFailedToShowFullScreenContent(adError)
                            interstitial = null
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "onAdShowedFullScreenContent: ")
                            super.onAdShowedFullScreenContent()
                            interstitial = null
                        }

                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "onAdDismissedFullScreenContent: ")
                            super.onAdDismissedFullScreenContent()
                            interstitial = null
                            lastTimeShowInter = System.currentTimeMillis()
                            loadInter()
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.d(TAG, "onAdFailedToLoad: ")
                    super.onAdFailedToLoad(loadAdError)
                    interstitial = null
                    if (!flag) {
                        loadInter()
                        flag = true
                    }
                }
            })
    }

    fun showInter(activity: Activity) {
        currentTime = System.currentTimeMillis()
        if (currentTime - lastTimeShowInter >= 3000 && IS_SHOW_ADS) {
            Log.d(TAG, "showInter: $currentTime - $lastTimeShowInter")
            if (interstitial != null) {
                interstitial!!.show(activity)
                flag = false
            } else {
                Log.d(TAG, "showInter: inter don't show")
                loadInter()
            }
        }
    }
}