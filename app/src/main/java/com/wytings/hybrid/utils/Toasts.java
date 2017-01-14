package com.wytings.hybrid.utils;

import android.view.Gravity;
import android.widget.Toast;

import com.wytings.hybrid.App;

/**
 * Created by rex.wei on 2017/1/11.
 */

public class Toasts {

    public static void show(CharSequence text) {
        Toast toast = Toast.makeText(App.getContext(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
