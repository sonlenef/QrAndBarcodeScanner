package tech.sonle.barcodescanner.feature.tabs.settings.ads

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.android.synthetic.main.activity_ads.*
import tech.sonle.barcodescanner.App
import tech.sonle.barcodescanner.BuildConfig
import tech.sonle.barcodescanner.R
import tech.sonle.barcodescanner.extension.applySystemWindowInsets
import tech.sonle.barcodescanner.feature.BaseActivity

/**
 * Create by SonLe on 14/08/2022
 */
class AdsActivity : BaseActivity() {

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
    }

    private fun supportEdgeToEdge() {
        root_view.applySystemWindowInsets(applyTop = true, applyBottom = true)
    }

    private fun initToolbar() {
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun init() {
        button_block_ads_10.setOnClickListener {
            (application as App).showReward(this, BuildConfig.CA_APP_PUB_BLOCK_ADS_10) {

            }
        }

        button_block_ads_20.setOnClickListener {
            (application as App).showReward(this, BuildConfig.CA_APP_PUB_BLOCK_ADS_20) {

            }
        }

        button_block_ads_30.setOnClickListener {
            (application as App).showReward(this, BuildConfig.CA_APP_PUB_BLOCK_ADS_30) {

            }
        }
    }
}