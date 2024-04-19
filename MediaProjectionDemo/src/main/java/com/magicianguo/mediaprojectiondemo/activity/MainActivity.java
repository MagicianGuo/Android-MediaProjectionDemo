package com.magicianguo.mediaprojectiondemo.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.magicianguo.mediaprojectiondemo.databinding.ActivityMainBinding;
import com.magicianguo.mediaprojectiondemo.util.MediaProjectionHelper;
import com.magicianguo.mediaprojectiondemo.util.NotificationHelper;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NotificationHelper.check(this);
        initView();
    }

    private void initView() {
        binding.btnStart.setOnClickListener(v -> {
            MediaProjectionHelper.start(this);
        });
        binding.btnStop.setOnClickListener(v -> {
            MediaProjectionHelper.stop();
        });
        binding.btnScreenshot.setOnClickListener(v -> {
            MediaProjectionHelper.screenshot();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MediaProjectionHelper.onStartResult(requestCode, resultCode, data);
    }
}