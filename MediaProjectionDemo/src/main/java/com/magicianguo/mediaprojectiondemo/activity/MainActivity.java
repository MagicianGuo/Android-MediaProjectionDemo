package com.magicianguo.mediaprojectiondemo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.magicianguo.mediaprojectiondemo.R;
import com.magicianguo.mediaprojectiondemo.constant.RequestCode;
import com.magicianguo.mediaprojectiondemo.constant.ServiceType;
import com.magicianguo.mediaprojectiondemo.databinding.ActivityMainBinding;
import com.magicianguo.mediaprojectiondemo.service.MediaProjectionService;
import com.magicianguo.mediaprojectiondemo.util.MediaProjectionHelper;
import com.magicianguo.mediaprojectiondemo.util.NotificationHelper;
import com.magicianguo.mediaprojectiondemo.util.TaskPool;
import com.magicianguo.mediaprojectiondemo.util.ToastUtils;
import com.magicianguo.mediaprojectiondemo.util.WindowHelper;

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
            if (MediaProjectionService.serviceType == ServiceType.VIDEO &&
                    checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ToastUtils.shortCall("录屏需要录音权限！");
                TaskPool.MAIN.postDelayed(() -> {
                    requestPermissions(new String[]{ Manifest.permission.RECORD_AUDIO }, RequestCode.RECORD_AUDIO);
                }, 1000);
                return;
            }
            MediaProjectionHelper.start(this);
        });
        binding.btnStop.setOnClickListener(v -> {
            MediaProjectionHelper.stop();
        });
        binding.btnShowScreenshot.setOnClickListener(v -> {
            if (WindowHelper.checkOverlay(this)) {
                WindowHelper.showScreenshotView();
            }
        });
        binding.btnHideScreenshot.setOnClickListener(v -> {
            if (WindowHelper.checkOverlay(this)) {
                WindowHelper.hideScreenshotView();
            }
        });
        binding.btnShowProjection.setOnClickListener(v -> {
            if (WindowHelper.checkOverlay(this)) {
                WindowHelper.showProjectionView(this);
            }
        });
        binding.btnHideProjection.setOnClickListener(v -> {
            if (WindowHelper.checkOverlay(this)) {
                WindowHelper.hideProjectionView();
            }
        });
        binding.btnShowVideo.setOnClickListener(v -> {
            if (WindowHelper.checkOverlay(this)) {
                WindowHelper.showVideoRecordView();
            }
        });
        binding.btnHideVideo.setOnClickListener(v -> {
            if (WindowHelper.checkOverlay(this)) {
                WindowHelper.hideVideoRecordView();
            }
        });
        binding.rgServiceType.check(R.id.rb_screenshot);
        binding.rgServiceType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_screenshot) {
                MediaProjectionService.serviceType = ServiceType.SCREENSHOT;
            } else if (checkedId == R.id.rb_projection) {
                MediaProjectionService.serviceType = ServiceType.PROJECTION;
            } else if (checkedId == R.id.rb_video) {
                MediaProjectionService.serviceType = ServiceType.VIDEO;
            }
        });
        binding.rgProjectionScale.check(R.id.rb_scale_2);
        binding.rgProjectionScale.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_scale_1) {
                WindowHelper.projectionViewScale = 1 / 2F;
            } else if (checkedId == R.id.rb_scale_2) {
                WindowHelper.projectionViewScale = 1 / 3F;
            } else if (checkedId == R.id.rb_scale_3) {
                WindowHelper.projectionViewScale = 1 / 4F;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCode.RECORD_AUDIO) {
            String txt;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                txt = "授予";
            } else {
                txt = "拒绝";
            }
            ToastUtils.shortCall("已" + txt + "录音权限！");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MediaProjectionHelper.onStartResult(requestCode, resultCode, data);
    }
}