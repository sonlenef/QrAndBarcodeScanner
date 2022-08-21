package tech.sonle.barcodescanner.feature.tabs.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdFormat
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdkUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_barcode_history.*
import tech.sonle.barcodescanner.App
import tech.sonle.barcodescanner.BuildConfig
import tech.sonle.barcodescanner.R
import tech.sonle.barcodescanner.di.barcodeDatabase
import tech.sonle.barcodescanner.extension.Config
import tech.sonle.barcodescanner.extension.applySystemWindowInsets
import tech.sonle.barcodescanner.extension.showError
import tech.sonle.barcodescanner.feature.common.dialog.DeleteConfirmationDialogFragment
import tech.sonle.barcodescanner.feature.tabs.history.export.ExportHistoryActivity


class BarcodeHistoryFragment : Fragment(), DeleteConfirmationDialogFragment.Listener {
    private val disposable = CompositeDisposable()

    private var adView: MaxAdView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_barcode_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportEdgeToEdge()
        initTabs()
        handleMenuClicked()
        if (((requireActivity().application as App).showAdsIn.value?.minus(System.currentTimeMillis()) ?: 0) <= 0) {
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
        val adView = AdView(requireContext())
        adView.adSize = AdSize.BANNER
        adView.adUnitId = BuildConfig.CA_APP_PUB_HISTORY
        adContainer.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun createBannerAd() {
        adView = MaxAdView(BuildConfig.AD_UNIT_HISTORY_BANNER, requireContext())
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
        val heightDp = MaxAdFormat.BANNER.getAdaptiveSize(requireActivity()).height
        val heightPx = AppLovinSdkUtils.dpToPx(requireContext(), heightDp)

        adView?.layoutParams = FrameLayout.LayoutParams(width, heightPx)

        adView?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        adView?.setExtraParameter("adaptive_banner", "true")
        adContainer.addView(adView)

        adView?.startAutoRefresh()
        adView?.loadAd()
    }

    override fun onDeleteConfirmed() {
        clearHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    private fun supportEdgeToEdge() {
        app_bar_layout.applySystemWindowInsets(applyTop = true)
    }

    private fun initTabs() {
        view_pager.adapter = BarcodeHistoryViewPagerAdapter(requireContext(), childFragmentManager)
        tab_layout.setupWithViewPager(view_pager)
    }

    private fun handleMenuClicked() {
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_export_history -> navigateToExportHistoryScreen()
                R.id.item_clear_history -> showDeleteHistoryConfirmationDialog()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun navigateToExportHistoryScreen() {
        ExportHistoryActivity.start(requireActivity())
    }

    private fun showDeleteHistoryConfirmationDialog() {
        val dialog =
            DeleteConfirmationDialogFragment.newInstance(R.string.dialog_delete_clear_history_message)
        dialog.show(childFragmentManager, "")
    }

    private fun clearHistory() {
        barcodeDatabase.deleteAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { },
                ::showError
            )
            .addTo(disposable)
    }
}