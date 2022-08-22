package tech.sonle.barcodescanner.feature.tabs.settings.permissions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web_view.*
import tech.sonle.barcodescanner.R

/**
 * Create by SonLe on 22/08/2022
 */
class WebViewActivity : AppCompatActivity() {

    companion object {
        const val URL = "http://sonlenef.dev/privacy-policy/scanner"

        @JvmStatic
        fun newInstance(context: Context) =
            Intent(context, WebViewActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        tabBar.setOnClickListener { finish() }

        webView.loadUrl(URL)
    }
}