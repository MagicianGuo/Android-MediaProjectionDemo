package com.magicianguo.mediaprojectiondemo;

import android.app.Application;

public class App extends Application {
    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static App getApp() {
        return app;
    }
}
