package com.wytings.hybrid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.wytings.hybrid.bridge.CallbackListener;
import com.wytings.hybrid.bridge.ActionHandler;
import com.wytings.hybrid.bridge.CompleteListener;
import com.wytings.hybrid.bridge.HybridWebView;
import com.wytings.hybrid.utils.JsonBuilder;
import com.wytings.hybrid.utils.Toasts;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    private CompleteListener tempListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final HybridWebView webView = (HybridWebView) findViewById(R.id.webView);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.send("notify", new JsonBuilder().put("status", 100).toString(), new CallbackListener() {
                    @Override
                    public void callback(JSONObject entity) {
                        Toasts.show("callback result:" + entity);
                    }
                });
            }
        });

        configMineWebView(webView);
        String url = "file:///android_asset/native_web.html";
        webView.loadUrl(url);
    }

    private void configMineWebView(HybridWebView webView) {
        webView.on("closePage", new ActionHandler() {
            @Override
            public void handle(JSONObject entity, final CompleteListener listener) {
                new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Holo_InputMethod).setMessage(entity.optString("msg"))
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                listener.complete(new JsonBuilder().put("code", MainActivity.this.isFinishing() ? 0 : -1).toString());
                            }
                        }).show();

            }
        });
        webView.on("takePicture", new ActionHandler() {
            @Override
            public void handle(JSONObject entity, CompleteListener listener) {
                String hint = entity.optString("hint");
                if (!TextUtils.isEmpty(hint)) {
                    Toasts.show(hint);
                }
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 200);
                tempListener = listener;
            }
        });
        webView.on("getNetworkInfo", new ActionHandler() {
            @Override
            public void handle(JSONObject entity, CompleteListener listener) {
                listener.complete(getActiveNetworkInfo());
            }
        });
    }

    private String getActiveNetworkInfo() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        int type = -1;
        String msg = "unknown";
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                type = 1;
                msg = "wifi";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                type = 2;
                msg = "mobile";
            } else {
                type = 3;
            }
        } else {
            msg = "network is disconnected";
        }
        return new JsonBuilder().put("type", type).put("msg", msg).toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && tempListener != null) {
            JsonBuilder jsonBuilder = new JsonBuilder();
            if (data == null) {
                jsonBuilder.put("code", -1).put("msg", "action is canceled");
            } else {
                Bitmap bitmap = data.getParcelableExtra("data");
                jsonBuilder.put("code", 0).put("img", bitmapToBase64(bitmap));
            }
            tempListener.complete(jsonBuilder.toString());
            tempListener = null;
        }
    }

    public String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream out = null;
        try {
            if (bitmap != null) {
                out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                out.flush();
                out.close();

                byte[] bitmapBytes = out.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
