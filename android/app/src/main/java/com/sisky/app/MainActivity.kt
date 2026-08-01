package com.sisky.app

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.getcapacitor.BridgeActivity
import com.getcapacitor.BridgeWebViewClient

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

        // Sobrescreve o WebViewClient do Capacitor
        bridge.webView.webViewClient = object : BridgeWebViewClient(bridge) {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString() ?: return false
                // Carrega tudo dentro do WebView
                view?.loadUrl(url)
                return true
            }
        }

        // Verifica subdomínio salvo
        val prefs = getSharedPreferences("SiskyPrefs", MODE_PRIVATE)
        val subdomain = prefs.getString("subdomain", null)
        if (!subdomain.isNullOrEmpty()) {
            bridge.webView.post {
                bridge.webView.loadUrl("https://$subdomain.sisky.com.br")
            }
        }
    }
}