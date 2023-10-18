package com.example.idvonesdk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        // Initialize WebView
        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        // Load the HTML file from assets
        webView.loadUrl("file:///android_asset/idv_page.html")

        // Set a WebViewClient to handle links within the WebView
        webView.webViewClient = WebViewClient()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, WebViewActivity::class.java)
        }
    }
}
