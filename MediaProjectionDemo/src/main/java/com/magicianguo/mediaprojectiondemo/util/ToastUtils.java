package com.magicianguo.mediaprojectiondemo.util;

import android.widget.Toast;

import com.magicianguo.mediaprojectiondemo.App;

public class ToastUtils {
    private static Toast mToast;

    public static void shortCall(String text) {
        cancel();
        mToast = Toast.makeText(App.getApp(), text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void longCall(String text) {
        cancel();
        mToast = Toast.makeText(App.getApp(), text, Toast.LENGTH_LONG);
        mToast.show();
    }

    private static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
