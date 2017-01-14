package com.wytings.hybrid.bridge;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wytings.hybrid.utils.ViewReplaceHelper;

/**
 * Created by rex on 12/30/16.
 */

public class HybridWebViewClient extends WebViewClient {
    private boolean isErrorWeb = false;
    private WebViewListener listener = new WebViewListener();
    private ViewReplaceHelper replaceHelper;
    private final CoreHybrid coreHybrid;

    public HybridWebViewClient(CoreHybrid coreHybrid) {
        this.coreHybrid = coreHybrid;
    }

    public CoreHybrid getCoreHybrid() {
        return coreHybrid;
    }

    public void setWebViewListener(WebViewListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    private void initReplaceHelper(final WebView view) {
        if (replaceHelper != null)
            return;
        replaceHelper = new ViewReplaceHelper();
        replaceHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.reload();
            }
        });
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return coreHybrid.shouldProcessHybrid(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (errorCode < 0) {
            replaceHelper.replace(view);
            listener.onFailLoaded(view);
        }
        isErrorWeb = true;
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (!isErrorWeb) {
            replaceHelper.restore();
            listener.onSuccessLoaded(view);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        isErrorWeb = false;
        initReplaceHelper(view);
    }

    public boolean isErrorWeb() {
        return isErrorWeb;
    }
}