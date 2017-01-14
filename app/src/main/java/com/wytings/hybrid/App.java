package com.wytings.hybrid;

import android.app.Application;
import android.content.Context;

/**
 * Created by rex on 12/29/16.
 */

public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
