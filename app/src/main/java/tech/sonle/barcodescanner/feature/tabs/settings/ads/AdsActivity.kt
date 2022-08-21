package tech.sonle.barcodescanner.feature.tabs.settings.ads

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdFormat
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdkUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.activity_ads.*
import tech.sonle.barcodescanner.App
import tech.sonle.barcodescanner.BuildConfig
import tech.sonle.barcodescanner.R
import tech.sonle.barcodescanner.extension.Config
import tech.sonle.barcodescanner.extension.applySystemWindowInsets
import tech.sonle.barcodescanner.feature.BaseActivity

/**
 * Create by SonLe on 14/08/2022
 */
class AdsActivity : BaseActivity() {

    private var adView: MaxAdView? = null

    companion object {
        const val TAG = "AdsActivity"
        fun start(context: Context) {
            val intent = Intent(context, AdsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads)
        supportEdgeToEdge()
        initToolbar()
        init()
        if (((application as App).showAdsIn.value?.minus(System.currentTimeMillis()) ?: 0) <= 0) {
            if (Config.IS_SHOW_ADS) {
                if (!Config.APPLOVIN_SHOW) {
                    initAds()
                } else {
                    createBannerAd()
                }
            }
        }
    }

    private fun initAds() {
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = BuildConfig.CA_APP_PUB_SETTINGS
        adContainer.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun createBannerAd() {
        adView = MaxAdView(BuildConfig.AD_UNIT_SETTING_BANNER, this)
        adView?.setListener(object : MaxAdViewAdListener {
            override fun onAdLoaded(ad: MaxAd?) {
                println("onAdLoaded")
            }

            override fun onAdDisplayed(ad: MaxAd?) {
                println("onAdDisplayed")
            }

            override fun onAdHidden(ad: MaxAd?) {
                println("onAdHidden")
            }

            override fun onAdClicked(ad: MaxAd?) {
                println("onAdClicked")
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                println("onAdLoadFailed: ${error?.message}")
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                println("onAdDisplayFailed: ${error?.message}")
            }

            override fun onAdExpanded(ad: MaxAd?) {
                println("onAdExpanded")
            }

            override fun onAdCollapsed(ad: MaxAd?) {
                println("onAdCollapsed")
            }
        })

        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val heightDp = MaxAdFormat.BANNER.getAdaptiveSize(this).height
        val heightPx = AppLovinSdkUtils.dpToPx(this, heightDp)

        adView?.layoutParams = FrameLayout.LayoutParams(width, heightPx)

        adView?.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        adView?.setExtraParameter("adaptive_banner", "true")
        adContainer.addView(adView)

        adView?.startAutoRefresh()
        adView?.loadAd()
    }

    private fun supportEdgeToEdge() {
        root_view.applySystemWindowInsets(applyTop = true, applyBottom = true)
    }

    private fun initToolbar() {
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun init() {
        blockAds()
        button_block_ads_10.setOnClickListener {
            (application as App).showReward(this, BuildConfig.CA_APP_PUB_BLOCK_ADS_10) {
                (application as App).showAdsIn.value =
                    (application as App).showAdsIn.value?.plus(10 * 60 * 1000)
                blockAds()
            }
        }

        button_block_ads_20.setOnClickListener {
            (application as App).showReward(this, BuildConfig.CA_APP_PUB_BLOCK_ADS_20) {
                (application as App).showAdsIn.value =
                    (application as App).showAdsIn.value?.plus(20 * 60 * 1000)
                blockAds()
            }
        }

        button_block_ads_30.setOnClickListener {
            (application as App).showReward(this, BuildConfig.CA_APP_PUB_BLOCK_ADS_30) {
                (application as App).showAdsIn.value =
                    (application as App).showAdsIn.value?.plus(30 * 60 * 1000)
                blockAds()
            }
        }
    }

    private fun blockAds() {
        (application as App).showAdsIn.observe(this) {
            it.let {
                adsBlock.text = if (it - System.currentTimeMillis() > 0) {
                    getString(
                        R.string.block_ads,
                        ((it - System.currentTimeMillis()) / 60000).toString()
                    )
                } else getString(R.string.block_ads, "0")
            }
        }
    }
}