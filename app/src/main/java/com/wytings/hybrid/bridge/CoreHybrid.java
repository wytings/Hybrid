package com.wytings.hybrid.bridge;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import com.wytings.hybrid.utils.Logs;

import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by rex on 12/31/16.
 */

public class CoreHybrid {

    private int actionIndex = 0;
    private Map<String, ActionHandler> handlerMap = new HashMap<>(20);
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, CallbackListener> callbacks = new HashMap<>();

    public boolean shouldProcessHybrid(WebView view, String url) {
        try {
            URI uri = new URI(url);
            if (isHybridScheme(uri)) {
                processUri(view, uri);
                return true;
            } else if (url.startsWith("tel:") || url.startsWith("sms:") || url.startsWith("smsto:") || url.startsWith("mms:") || url.startsWith("mmsto:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            Logs.e("fail to process hybrid -> " + e);
        }
        return false;
    }

    public boolean isHybridScheme(URI uri) {
        return "hybrid".equals(uri.getScheme()) && !"".equals(uri.getQuery());
    }

    public void processUri(WebView view, URI uri) {
        String action = uri.getHost();
        HybridData data = HybridData.parse(uri.getQuery());
        Logs.i("received from web -> " + action + " , " + data);
        if ("event".equals(action)) {
            triggerEventFromWebView(view, data);
        } else if ("callback".equals(action)) {
            triggerCallbackForMessage(data.id, data.entity);
        } else {
            Logs.w(" there is something wrong with action=" + action + " -> " + data);
        }
    }

    protected void triggerEventFromWebView(final WebView webView, final HybridData data) {
        final int actionId = data.id;
        final String type = data.type;
        final CompleteListener listener = new CompleteListener() {
            @Override
            public void complete(final String result) {
                Logs.i("complete ---> " + type);
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        triggerCallbackOnWebView(webView, actionId, result);
                    }
                });
            }
        };
        if (handlerMap.containsKey(type)) {
            try {
                ActionHandler actionHandler = handlerMap.get(type);
                actionHandler.handle(data.entity, listener);
            } catch (Exception e) {
                Logs.e("fail to handle " + e);
                listener.complete("{}");
            }
        } else {
            listener.complete("{}");
        }
    }

    protected void triggerCallbackForMessage(int actionId, JSONObject entity) {
        try {
            CallbackListener complete = callbacks.get(actionId);
            if (complete != null) {
                complete.callback(entity);
            }
        } catch (Exception e) {
            Logs.e("fail to callback " + e);
        } finally {
            callbacks.remove(actionId);
        }
    }

    public void triggerCallbackOnWebView(WebView webView, int actionId, String data) {
        String url = String.format(Locale.getDefault(), "javascript:Hybrid.triggerCallback(\"%d\", %s)", actionId, data);
        webView.loadUrl(url);
    }

    public void send(WebView webView, String type, String json, CallbackListener listener) {
        final int actionId = actionIndex;

        if (listener != null) {
            callbacks.put(actionId, listener);
        }

        String url = String.format(Locale.getDefault(), "javascript:Hybrid.trigger(\"%s\", %d, %s)", type, actionId, json);
        webView.loadUrl(url);

        ++actionIndex;
    }

    public void on(String type, ActionHandler handler) {
        handlerMap.put(type, handler);
    }

}
