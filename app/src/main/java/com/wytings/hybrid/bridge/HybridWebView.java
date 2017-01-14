package com.wytings.hybrid.bridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.wytings.hybrid.BuildConfig;
import com.wytings.hybrid.utils.Logs;

/**
 * Created by rex.wei on 2016/8/30.
 */
public class HybridWebView extends WebView {
    private WebViewListener webViewListener;
    private HybridWebViewClient hybridWebViewClient;


    public HybridWebView(Context context) {
        super(context);
        initialize();
    }

    public HybridWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public HybridWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initialize() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        String appCachePath = getContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true);
        }

        hybridWebViewClient = new HybridWebViewClient(new CoreHybrid());
        setWebViewClient(hybridWebViewClient);
        setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Logs.i(" onReceivedTitle -> " + title);
                if (webViewListener != null && !TextUtils.isEmpty(title) && !title.startsWith("http")) {
                    webViewListener.onReceivedTitle(HybridWebView.this, title);
                }
            }
        });
    }

    public boolean isErrorWeb() {
        return hybridWebViewClient.isErrorWeb();
    }

    public boolean isLoaded() {
        return !TextUtils.isEmpty(getOriginalUrl());
    }

    public void send(String type, String data, CallbackListener callback) {
        Logs.i("send event to webView -> action=" + type + ", json = " + data);
        hybridWebViewClient.getCoreHybrid().send(this, type, data, callback);
    }

    public void on(String type, ActionHandler handler) {
        hybridWebViewClient.getCoreHybrid().on(type, handler);
    }


    public void setWebViewListener(WebViewListener listener) {
        this.webViewListener = listener;
    }

}
