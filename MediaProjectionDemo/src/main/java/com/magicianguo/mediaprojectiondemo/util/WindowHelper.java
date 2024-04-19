package com.magicianguo.mediaprojectiondemo.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.magicianguo.mediaprojectiondemo.App;

public class WindowHelper {
    private static final WindowManager WINDOW_MANAGER = (WindowManager) App.getApp().getSystemService(Context.WINDOW_SERVICE);

    public static DisplayMetrics getRealMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WINDOW_MANAGER.getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics;
    }
}
