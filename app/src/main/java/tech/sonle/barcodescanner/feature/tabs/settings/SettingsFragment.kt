package tech.sonle.barcodescanner.feature.tabs.settings

import android.content.Intent
import android.net.Uri
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
import kotlinx.android.synthetic.main.fragment_settings.*
import tech.sonle.barcodescanner.App
import tech.sonle.barcodescanner.BuildConfig
import tech.sonle.barcodescanner.R
import tech.sonle.barcodescanner.di.barcodeDatabase
import tech.sonle.barcodescanner.di.settings
import tech.sonle.barcodescanner.extension.Config
import tech.sonle.barcodescanner.extension.applySystemWindowInsets
import tech.sonle.barcodescanner.extension.showError
import tech.sonle.barcodescanner.feature.common.dialog.DeleteConfirmationDialogFragment
import tech.sonle.barcodescanner.feature.tabs.settings.camera.ChooseCameraActivity
import tech.sonle.barcodescanner.feature.tabs.settings.formats.SupportedFormatsActivity
import tech.sonle.barcodescanner.feature.tabs.settings.permissions.AllPermissionsActivity
import tech.sonle.barcodescanner.feature.tabs.settings.permissions.WebViewActivity
import tech.sonle.barcodescanner.feature.tabs.settings.search.ChooseSearchEngineActivity
import tech.sonle.barcodescanner.feature.tabs.settings.theme.ChooseThemeActivity

class SettingsFragment : Fragment(), DeleteConfirmationDialogFragment.Listener {
    private val disposable = CompositeDisposable()

    private var adView: MaxAdView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportEdgeToEdge()
    }

    override fun onResume() {
        super.onResume()
        handleButtonCheckedChanged()
        handleButtonClicks()
        showSettings()
        showAppVersion()
        if (((requireActivity().application as App).showAdsIn.value?.minus(System.currentTimeMillis())
                ?: 0) <= 0
        ) {
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
        adView.adUnitId = BuildConfig.CA_APP_PUB_SETTINGS
        adContainer.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun createBannerAd() {
        adView = MaxAdView(BuildConfig.AD_UNIT_CREATE_BANNER, requireContext())
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

    fun supportEdgeToEdge() {
        app_bar_layout.applySystemWindowInsets(applyTop = true)
    }

    private fun handleButtonCheckedChanged() {
        button_inverse_barcode_colors_in_dark_theme.setCheckedChangedListener {
            settings.areBarcodeColorsInversed = it
        }
        button_open_links_automatically.setCheckedChangedListener {
            settings.openLinksAutomatically = it
        }
        button_copy_to_clipboard.setCheckedChangedListener { settings.copyToClipboard = it }
        button_simple_auto_focus.setCheckedChangedListener { settings.simpleAutoFocus = it }
        button_flashlight.setCheckedChangedListener { settings.flash = it }
        button_vibrate.setCheckedChangedListener { settings.vibrate = it }
        button_continuous_scanning.setCheckedChangedListener { settings.continuousScanning = it }
        button_confirm_scans_manually.setCheckedChangedListener {
            settings.confirmScansManually = it
        }
        button_save_scanned_barcodes.setCheckedChangedListener {
            settings.saveScannedBarcodesToHistory = it
        }
        button_save_created_barcodes.setCheckedChangedListener {
            settings.saveCreatedBarcodesToHistory = it
        }
        button_do_not_save_duplicates.setCheckedChangedListener {
            settings.doNotSaveDuplicates = it
        }

        privacyPolicy.setOnClickListener {
            startActivity(WebViewActivity.newInstance(requireActivity()))
        }
    }

    private fun handleButtonClicks() {
        button_choose_theme.setOnClickListener { ChooseThemeActivity.start(requireActivity()) }
        button_choose_camera.setOnClickListener { ChooseCameraActivity.start(requireActivity()) }
        button_select_supported_formats.setOnClickListener {
            SupportedFormatsActivity.start(
                requireActivity()
            )
        }
        button_clear_history.setOnClickListener { showDeleteHistoryConfirmationDialog() }
        button_choose_search_engine.setOnClickListener {
            ChooseSearchEngineActivity.start(
                requireContext()
            )
        }
        button_permissions.setOnClickListener { AllPermissionsActivity.start(requireActivity()) }
        button_check_updates.setOnClickListener { showAppInMarket() }
    }

    private fun clearHistory() {
        button_clear_history.isEnabled = false

        barcodeDatabase.deleteAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    button_clear_history.isEnabled = true
                },
                { error ->
                    button_clear_history.isEnabled = true
                    showError(error)
                }
            )
            .addTo(disposable)
    }

    private fun showSettings() {
        settings.apply {
            button_inverse_barcode_colors_in_dark_theme.isChecked = areBarcodeColorsInversed
            button_open_links_automatically.isChecked = openLinksAutomatically
            button_copy_to_clipboard.isChecked = copyToClipboard
            button_simple_auto_focus.isChecked = simpleAutoFocus
            button_flashlight.isChecked = flash
            button_vibrate.isChecked = vibrate
            button_continuous_scanning.isChecked = continuousScanning
            button_confirm_scans_manually.isChecked = confirmScansManually
            button_save_scanned_barcodes.isChecked = saveScannedBarcodesToHistory
            button_save_created_barcodes.isChecked = saveCreatedBarcodesToHistory
            button_do_not_save_duplicates.isChecked = doNotSaveDuplicates
        }
    }

    private fun showDeleteHistoryConfirmationDialog() {
        val dialog =
            DeleteConfirmationDialogFragment.newInstance(R.string.dialog_delete_clear_history_message)
        dialog.show(childFragmentManager, "")
    }

    private fun showAppInMarket() {
        val uri = Uri.parse("market://details?id=" + requireContext().packageName)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags =
                Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun showAppVersion() {
        button_app_version.hint = BuildConfig.VERSION_NAME
    }
}