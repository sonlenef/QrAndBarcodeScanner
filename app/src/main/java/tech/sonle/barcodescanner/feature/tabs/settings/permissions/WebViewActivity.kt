package tech.sonle.barcodescanner.feature.tabs.settings.permissions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web_view.*
import tech.sonle.barcodescanner.R

/**
 * Create by SonLe on 22/08/2022
 */
class WebViewActivity : AppCompatActivity() {

    companion object {
        const val URL = "https://sonlenef.dev/privacy-policy/scanner"

        @JvmStatic
        fun newInstance(context: Context) =
            Intent(context, WebViewActivity::class.java)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true // enable javascript

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {}

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return true
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return true
            }

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                er: SslError
            ) {
            }

            override fun onReceivedHttpAuthRequest(
                view: WebView,
                handler: HttpAuthHandler,
                host: String,
                realm: String
            ) {
            }
        }

        webView.loadUrl(URL)
    }
}