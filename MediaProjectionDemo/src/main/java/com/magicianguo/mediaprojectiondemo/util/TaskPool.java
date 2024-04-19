package com.magicianguo.mediaprojectiondemo.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskPool {
    public static final Handler MAIN = new Handler(Looper.getMainLooper());
    public static final ExecutorService CACHE = Executors.newCachedThreadPool();
}
