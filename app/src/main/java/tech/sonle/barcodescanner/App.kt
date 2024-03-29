package tech.sonle.barcodescanner

import android.app.Activity
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.multidex.MultiDexApplication
import com.applovin.mediation.*
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.ads.MaxRewardedAd
import com.applovin.sdk.AppLovinSdk
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import tech.sonle.barcodescanner.di.settings
import tech.sonle.barcodescanner.extension.Config
import tech.sonle.barcodescanner.extension.Config.Companion.IS_SHOW_ADS
import java.util.concurrent.TimeUnit
import kotlin.math.pow


class App : MultiDexApplication() {

    var TAG = "my_" + javaClass.simpleName

    var interstitial: InterstitialAd? = null
    private lateinit var interstitialAd: MaxInterstitialAd
    private var retryAttempt = 0.0
    var rewardedAd: RewardedAd? = null
    var flag = false
    var currentTime: Long = 0
    var lastTimeShowInter: Long = 0
    var lastTimeShowReward: Long = 0
    var showAdsIn: MutableLiveData<Long> = MutableLiveData(0)
    private lateinit var rewardedAdApplovin: MaxRewardedAd
    private var retryAttemptRewarded = 0.0

    override fun onCreate() {
        applyTheme()
        MobileAds.initialize(
            this
        ) {}

        // Make sure to set the mediation provider value to "max" to ensure proper functionality
        AppLovinSdk.getInstance(this).apply {
            mediationProvider = "max"
            initializeSdk {}
        }
        super.onCreate()

        loadInter()
        loadReward(BuildConfig.CA_APP_PUB_BLOCK_ADS_10)
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
        if ((showAdsIn.value?.minus(System.currentTimeMillis()) ?: 0) <= 0) {
            if (!Config.APPLOVIN_SHOW) {
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
            } else {
                if (IS_SHOW_ADS) {
                    if (this::interstitialAd.isInitialized && interstitialAd.isReady) {
                        interstitialAd.showAd()
                    } else {
                        createInterstitialAd(activity)
                    }
                }
            }
        }
    }

    fun loadReward(adUnitId: String) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            this,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    this@App.rewardedAd = rewardedAd
                }
            })
    }

    fun showReward(activity: Activity, adUnitId: String, claim: () -> Unit) {
        currentTime = System.currentTimeMillis()
        if (currentTime - lastTimeShowReward >= 5000) {
            if (!Config.APPLOVIN_SHOW) {
                Log.d(TAG, "showInter: $currentTime - $lastTimeShowReward")
                if (rewardedAd != null) {
                    rewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            // Called when a click is recorded for an ad.
                            Log.d(TAG, "Ad was clicked.")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            Log.d(TAG, "Ad dismissed fullscreen content.")
                            rewardedAd = null
                            lastTimeShowReward = System.currentTimeMillis()
                            loadReward(adUnitId)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Called when ad fails to show.
                            rewardedAd = null
                            lastTimeShowReward = System.currentTimeMillis()
                            loadReward(adUnitId)
                        }

                        override fun onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            Log.d(TAG, "Ad recorded an impression.")
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            Log.d(TAG, "Ad showed fullscreen content.")
                        }
                    }
                    rewardedAd!!.show(activity) {
                        if (showAdsIn.value == 0L) showAdsIn.value = System.currentTimeMillis()
                        claim.invoke()
                    }
                } else {
                    Log.d(TAG, "showInter: inter don't show")
                    loadReward(adUnitId)
                }
            } else {
                if (this::rewardedAdApplovin.isInitialized) {
                    if (rewardedAdApplovin.isReady) {
                        rewardedAdApplovin.showAd()
                    }
                } else {
                    createRewardedAd(activity, claim)
                }
            }
        } else {
            Toast.makeText(activity, "Action too fast", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createInterstitialAd(activity: Activity) {
        interstitialAd = MaxInterstitialAd(BuildConfig.AD_UNIT_APP_INTERSTITIAL, activity)
        interstitialAd.setListener(object : MaxAdListener {
            override fun onAdLoaded(ad: MaxAd?) {
                retryAttempt = 0.0
            }

            override fun onAdDisplayed(ad: MaxAd?) {}

            override fun onAdHidden(ad: MaxAd?) {
                interstitialAd.loadAd()
            }

            override fun onAdClicked(ad: MaxAd?) {}

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                retryAttempt++
                val delayMillis =
                    TimeUnit.SECONDS.toMillis(2.0.pow(6.0.coerceAtMost(retryAttempt)).toLong())

                Handler().postDelayed({ interstitialAd.loadAd() }, delayMillis)
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                interstitialAd.loadAd()
            }
        })

        interstitialAd.loadAd()
    }

    private fun createRewardedAd(activity: Activity, claim: () -> Unit) {
        rewardedAdApplovin = MaxRewardedAd.getInstance(BuildConfig.AD_UNIT_APP_REWARD, activity)
        rewardedAdApplovin.setListener(object : MaxRewardedAdListener {
            override fun onAdLoaded(ad: MaxAd?) {
                retryAttemptRewarded = 0.0
            }

            override fun onAdDisplayed(ad: MaxAd?) {}

            override fun onAdHidden(ad: MaxAd?) {
                rewardedAdApplovin.loadAd()
            }

            override fun onAdClicked(ad: MaxAd?) {}

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                // Rewarded ad failed to load
                // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)

                retryAttemptRewarded++
                val delayMillis = TimeUnit.SECONDS.toMillis(
                    2.0.pow(
                        6.0.coerceAtMost(
                            retryAttemptRewarded
                        )
                    ).toLong()
                )

                Handler().postDelayed({ rewardedAdApplovin.loadAd() }, delayMillis)
                lastTimeShowReward = System.currentTimeMillis()
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                lastTimeShowReward = System.currentTimeMillis()
                rewardedAdApplovin.loadAd()
            }

            override fun onRewardedVideoStarted(ad: MaxAd?) {}

            override fun onRewardedVideoCompleted(ad: MaxAd?) {}

            override fun onUserRewarded(ad: MaxAd?, reward: MaxReward?) {
                lastTimeShowReward = System.currentTimeMillis()
                if (showAdsIn.value == 0L) showAdsIn.value = System.currentTimeMillis()
                claim.invoke()
            }
        })
        rewardedAdApplovin.loadAd()
    }

}