package tech.sonle.barcodescanner.feature.tabs

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import tech.sonle.barcodescanner.BuildConfig
import tech.sonle.barcodescanner.R
import tech.sonle.barcodescanner.extension.applySystemWindowInsets
import tech.sonle.barcodescanner.feature.BaseActivity
import tech.sonle.barcodescanner.feature.tabs.create.CreateBarcodeFragment
import tech.sonle.barcodescanner.feature.tabs.history.BarcodeHistoryFragment
import tech.sonle.barcodescanner.feature.tabs.scan.ScanBarcodeFromCameraFragment
import tech.sonle.barcodescanner.feature.tabs.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_bottom_tabs.*
import tech.sonle.barcodescanner.extension.Config

class BottomTabsActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val ACTION_CREATE_BARCODE = "${BuildConfig.APPLICATION_ID}.CREATE_BARCODE"
        private const val ACTION_HISTORY = "${BuildConfig.APPLICATION_ID}.HISTORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_tabs)

        val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder().apply {
            minimumFetchIntervalInSeconds = 0
        }.build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Config.IS_SHOW_ADS = remoteConfig.getBoolean("AdsShow")
                    Config.APPLOVIN_SHOW = remoteConfig.getBoolean("ApplovinShow")
                }
            }

        supportEdgeToEdge()
        initBottomNavigationView()

        if (savedInstanceState == null) {
            showInitialFragment()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == bottom_navigation_view.selectedItemId) {
            return false
        }
        showFragment(item.itemId)
        return true
    }

    override fun onBackPressed() {
        if (bottom_navigation_view.selectedItemId == R.id.item_scan) {
            super.onBackPressed()
        } else {
            bottom_navigation_view.selectedItemId = R.id.item_scan
        }
    }

    private fun supportEdgeToEdge() {
        bottom_navigation_view.applySystemWindowInsets(applyBottom = true)
    }

    private fun initBottomNavigationView() {
        bottom_navigation_view.apply {
            setOnNavigationItemSelectedListener(this@BottomTabsActivity)
        }
    }

    private fun showInitialFragment() {
        when (intent?.action) {
            ACTION_CREATE_BARCODE -> bottom_navigation_view.selectedItemId = R.id.item_create
            ACTION_HISTORY -> bottom_navigation_view.selectedItemId = R.id.item_history
            else -> showFragment(R.id.item_scan)
        }
    }

    private fun showFragment(bottomItemId: Int) {
        val fragment = when (bottomItemId) {
            R.id.item_scan -> ScanBarcodeFromCameraFragment()
            R.id.item_create -> CreateBarcodeFragment()
            R.id.item_history -> BarcodeHistoryFragment()
            R.id.item_settings -> SettingsFragment()
            else -> null
        }
        fragment?.apply(::replaceFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_fragment_container, fragment)
            .setReorderingAllowed(true)
            .commit()
    }
}