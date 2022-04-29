package com.seen.user.extra

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.webkit.WebView
import android.webkit.WebViewClient

class MyWebViewClient : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        view?.setBackgroundColor(Color.TRANSPARENT)
        if (Build.VERSION.SDK_INT >= 11) view?.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        view.loadUrl(url!!)
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

    }

}