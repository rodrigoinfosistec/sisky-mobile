package com.sisky.app

import android.os.Bundle
import android.webkit.JavascriptInterface
import com.getcapacitor.BridgeActivity
import com.getcapacitor.BridgeWebViewClient
import android.webkit.WebResourceRequest
import android.webkit.WebView

class MainActivity : BridgeActivity() {

    inner class AndroidBridge {
        @JavascriptInterface
        fun setSubdomain(subdomain: String) {
            val prefs = getSharedPreferences("SiskyPrefs", MODE_PRIVATE)
            prefs.edit().putString("subdomain", subdomain).apply()
            runOnUiThread {
                bridge.webView.loadUrl("https://$subdomain.sisky.com.br")
            }
        }

        @JavascriptInterface
        fun getSavedSubdomain(): String {
            val prefs = getSharedPreferences("SiskyPrefs", MODE_PRIVATE)
            return prefs.getString("subdomain", "") ?: ""
        }

        @JavascriptInterface
        fun navigateTo(url: String) {
            runOnUiThread {
                bridge.webView.loadUrl(url)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bridge.webView.addJavascriptInterface(AndroidBridge(), "Android")

        bridge.webView.webViewClient = object : BridgeWebViewClient(bridge) {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString() ?: return false
                if (url.contains("sisky.com.br") || url.contains("localhost")) {
                    return false
                }
                return true
            }
        }

        val prefs = getSharedPreferences("SiskyPrefs", MODE_PRIVATE)
        val subdomain = prefs.getString("subdomain", null)
        if (!subdomain.isNullOrEmpty()) {
            bridge.webView.post {
                bridge.webView.loadUrl("https://$subdomain.sisky.com.br")
            }
        }
    }
}