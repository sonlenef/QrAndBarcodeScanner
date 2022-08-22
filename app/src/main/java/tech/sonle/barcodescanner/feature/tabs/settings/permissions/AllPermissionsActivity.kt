package tech.sonle.barcodescanner.feature.tabs.settings.permissions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import tech.sonle.barcodescanner.R
import tech.sonle.barcodescanner.extension.applySystemWindowInsets
import tech.sonle.barcodescanner.feature.BaseActivity
import kotlinx.android.synthetic.main.activity_all_permissions.*

class AllPermissionsActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AllPermissionsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_permissions)
        root_view.applySystemWindowInsets(applyTop = true, applyBottom = true)
        toolbar.setNavigationOnClickListener { finish() }
        privacyPolicy.setOnClickListener {
            startActivity(WebViewActivity.newInstance(this))
        }
    }
}