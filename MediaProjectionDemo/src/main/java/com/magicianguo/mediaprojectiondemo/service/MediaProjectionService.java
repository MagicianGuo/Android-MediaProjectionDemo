package com.magicianguo.mediaprojectiondemo.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import androidx.annotation.Nullable;

import com.magicianguo.mediaprojectiondemo.App;
import com.magicianguo.mediaprojectiondemo.util.MediaProjectionHelper;
import com.magicianguo.mediaprojectiondemo.util.NotificationHelper;
import com.magicianguo.mediaprojectiondemo.util.ToastUtils;
import com.magicianguo.mediaprojectiondemo.util.WindowHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Locale;

public class MediaProjectionService extends Service {
    private static final String TAG = "MediaProjectionService";
    @Nullable
    private static MediaProjection mMediaProjection;
    @Nullable
    private static ImageReader mImageReader;
    @Nullable
    private static VirtualDisplay mVirtualDisplayImageReader;
    public static int resultCode;
    public static Intent resultData;
    private static boolean mImageAvailable = false;

    private static final MediaProjection.Callback MEDIA_PROJECTION_CALLBACK = new MediaProjection.Callback() {
    };

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.startMediaProjectionForeground(this);
        mMediaProjection = MediaProjectionHelper.getManager().getMediaProjection(resultCode, resultData);
        createImageReaderVirtualDisplay();
    }

    private static void createImageReaderVirtualDisplay() {
        if (mMediaProjection != null) {
            DisplayMetrics dm = WindowHelper.getRealMetrics();
            mImageReader = ImageReader.newInstance(dm.widthPixels, dm.heightPixels, PixelFormat.RGBA_8888, 1);
            mImageReader.setOnImageAvailableListener(reader -> {
                mImageAvailable = true;
            }, null);
            mMediaProjection.registerCallback(MEDIA_PROJECTION_CALLBACK, null);
            mVirtualDisplayImageReader = mMediaProjection.createVirtualDisplay("ImageReader", dm.widthPixels, dm.heightPixels, dm.densityDpi, Display.FLAG_ROUND, mImageReader.getSurface(), null, null);
        }
    }

    public static void screenshot() {
        if (!mImageAvailable) {
            Log.e(TAG, "screenshot: mImageAvailable is false");
            ToastUtils.shortCall("截屏失败");
            return;
        }
        if (mImageReader == null) {
            Log.e(TAG, "screenshot: mImageReader is null");
            ToastUtils.shortCall("截屏失败");
            return;
        }
        try {
            Image image = mImageReader.acquireLatestImage();
            Image.Plane plane = image.getPlanes()[0];
            ByteBuffer buffer = plane.getBuffer();
            Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            image.close();

            String fileName = createScreenshotFileName();
            File file = new File(App.getApp().getExternalFilesDir(null).getParent(), fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.close();
            bitmap.recycle();
            ToastUtils.longCall("截图成功！"+fileName);
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.longCall("截图失败！");
        }
    }

    private static String createScreenshotFileName() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return String.format("Screenshot-%d%02d%02d%02d%02d%02d.png", year, month, day, hour, minute, second);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVirtualDisplayImageReader != null) {
            mVirtualDisplayImageReader.release();
            mVirtualDisplayImageReader = null;
        }
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
        mImageAvailable = false;
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(MEDIA_PROJECTION_CALLBACK);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}