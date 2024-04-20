package com.magicianguo.mediaprojectiondemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Surface;
import android.view.WindowManager;

import com.magicianguo.mediaprojectiondemo.App;
import com.magicianguo.mediaprojectiondemo.view.ProjectionView;
import com.magicianguo.mediaprojectiondemo.view.ScreenshotView;

public class WindowHelper {
    private static final WindowManager WINDOW_MANAGER = (WindowManager) App.getApp().getSystemService(Context.WINDOW_SERVICE);
    private static final ScreenshotView SCREENSHOT_VIEW = new ScreenshotView(App.getApp());
    private static final WindowManager.LayoutParams SCREENSHOT_VIEW_PARAMS = newLayoutParams();
    private static final ProjectionView PROJECTION_VIEW = new ProjectionView(App.getApp());
    private static final WindowManager.LayoutParams PROJECTION_VIEW_PARAMS = newLayoutParams();

    private static boolean mScreenshotViewShowing = false;
    private static boolean mProjectionViewShowing = false;

    private static int mProjectionViewScale = 3;

    static {
        SCREENSHOT_VIEW.setLayoutListener((x, y) -> {
            SCREENSHOT_VIEW_PARAMS.x = x;
            SCREENSHOT_VIEW_PARAMS.y = y;
            WINDOW_MANAGER.updateViewLayout(SCREENSHOT_VIEW, SCREENSHOT_VIEW_PARAMS);
        });
        PROJECTION_VIEW.setLayoutListener((x, y) -> {
            PROJECTION_VIEW_PARAMS.x = x;
            PROJECTION_VIEW_PARAMS.y = y;
            WINDOW_MANAGER.updateViewLayout(PROJECTION_VIEW, PROJECTION_VIEW_PARAMS);
        });
    }

    public static void showScreenshotView() {
        if (mScreenshotViewShowing) {
            return;
        }
        WINDOW_MANAGER.addView(SCREENSHOT_VIEW, SCREENSHOT_VIEW_PARAMS);
        mScreenshotViewShowing = true;
    }

    public static void hideScreenshotView() {
        if (!mScreenshotViewShowing) {
            return;
        }
        WINDOW_MANAGER.removeView(SCREENSHOT_VIEW);
        mScreenshotViewShowing = false;
    }

    public static void showProjectionView() {
        if (mProjectionViewShowing) {
            return;
        }
        DisplayMetrics realMetrics = getRealMetrics();
        PROJECTION_VIEW_PARAMS.width = realMetrics.widthPixels / mProjectionViewScale;
        PROJECTION_VIEW_PARAMS.height = realMetrics.heightPixels / mProjectionViewScale;
        WINDOW_MANAGER.addView(PROJECTION_VIEW, PROJECTION_VIEW_PARAMS);
        mProjectionViewShowing = true;
    }

    public static void hideProjectionView() {
        if (!mProjectionViewShowing) {
            return;
        }
        WINDOW_MANAGER.removeView(PROJECTION_VIEW);
        mProjectionViewShowing = false;
    }

    public static boolean checkOverlay(Activity activity) {
        if (Settings.canDrawOverlays(activity)) {
            return true;
        } else {
            ToastUtils.longCall("请开启悬浮窗权限！");
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    .setData(Uri.parse("package:"+activity.getPackageName()));
            activity.startActivity(intent);
            return false;
        }
    }

    public static Surface getProjectionSurface() {
        return PROJECTION_VIEW.getSurface();
    }

    public static DisplayMetrics getRealMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WINDOW_MANAGER.getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics;
    }

    private static WindowManager.LayoutParams newLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params.gravity = Gravity.START | Gravity.TOP;
        params.format = PixelFormat.TRANSLUCENT;
        return params;
    }
}
