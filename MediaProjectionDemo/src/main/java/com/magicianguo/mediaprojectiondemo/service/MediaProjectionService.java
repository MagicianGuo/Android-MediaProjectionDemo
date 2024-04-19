package com.magicianguo.mediaprojectiondemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.magicianguo.mediaprojectiondemo.util.MediaProjectionHelper;
import com.magicianguo.mediaprojectiondemo.util.NotificationHelper;

public class MediaProjectionService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        MediaProjectionHelper.configService(this);
        NotificationHelper.startMediaProjectionForeground(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaProjectionHelper.configService(null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}