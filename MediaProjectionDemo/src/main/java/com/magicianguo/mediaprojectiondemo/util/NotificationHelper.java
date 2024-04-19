package com.magicianguo.mediaprojectiondemo.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;

import com.magicianguo.mediaprojectiondemo.App;
import com.magicianguo.mediaprojectiondemo.R;

public class NotificationHelper {
    private static final String CHANNEL_ID_MEDIA_PROJECTION = "CHANNEL_ID_MEDIA_PROJECTION";
    private static final String CHANNEL_NAME_MEDIA_PROJECTION = "屏幕录制";
    private static final int NOTIFICATION_ID_MEDIA_PROJECTION = 1;
    private static final NotificationManager NOTIFICATION_MANAGER = (NotificationManager) App.getApp().getSystemService(Context.NOTIFICATION_SERVICE);

    /**
     * 检查通知权限
     *
     * @param activity
     */
    public static void check(Activity activity) {
        if (!NotificationManagerCompat.from(activity).areNotificationsEnabled()) {
            new AlertDialog.Builder(activity)
                    .setTitle("通知")
                    .setMessage("请授予通知权限。")
                    .setNegativeButton("取消", (dialog, which) -> {
                    })
                    .setPositiveButton("确认", (dialog, which) -> {
                        Intent intent;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
                        } else {
                            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.parse("package:" + activity.getPackageName()));
                        }
                        activity.startActivity(intent);
                    }).create().show();
        }
    }

    public static void startMediaProjectionForeground(Service service) {
        Notification.Builder notificationBuilder = new Notification.Builder(service)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("屏幕录制已启动");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_MEDIA_PROJECTION, CHANNEL_NAME_MEDIA_PROJECTION, NotificationManager.IMPORTANCE_HIGH);
            NOTIFICATION_MANAGER.createNotificationChannel(channel);

            notificationBuilder.setChannelId(CHANNEL_ID_MEDIA_PROJECTION);
        }
        Notification notification = notificationBuilder.build();
        service.startForeground(NOTIFICATION_ID_MEDIA_PROJECTION, notification);
    }
}
