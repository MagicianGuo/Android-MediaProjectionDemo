package com.magicianguo.mediaprojectiondemo.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.magicianguo.mediaprojectiondemo.R;
import com.magicianguo.mediaprojectiondemo.constant.ServiceType;
import com.magicianguo.mediaprojectiondemo.service.MediaProjectionService;
import com.magicianguo.mediaprojectiondemo.util.TaskPool;
import com.magicianguo.mediaprojectiondemo.util.ToastUtils;

public class VideoRecordView extends FrameLayout {
    private TextView tvRecord;
    private TextView tvTime;
    private int mTime = 0;
    private static final int MSG_START_RECORD = 1;
    private static final int MSG_TIME_INCREASE = 2;
    private static final int MSG_STOP_RECORD = 3;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_START_RECORD:
                    tvRecord.setVisibility(GONE);
                    tvTime.setVisibility(VISIBLE);
                    mTime = 0;
                    tvTime.setText(formatTime(mTime));
                    sendEmptyMessageDelayed(MSG_TIME_INCREASE, 1000);
                    break;
                case MSG_TIME_INCREASE:
                    mTime++;
                    tvTime.setText(formatTime(mTime));
                    sendEmptyMessageDelayed(MSG_TIME_INCREASE, 1000);
                    break;
                case MSG_STOP_RECORD:
                    tvTime.setVisibility(GONE);
                    tvRecord.setVisibility(VISIBLE);
                    removeMessages(MSG_TIME_INCREASE);
            }
        }
    };
    @Nullable
    private ILayoutListener mListener;
    private boolean mIsRecording = false;

    public VideoRecordView(@NonNull Context context) {
        super(context);
        init();
    }

    public VideoRecordView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoRecordView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_video_record_view, this);
        tvRecord = findViewById(R.id.tv_record);
        tvTime = findViewById(R.id.tv_time);
        setOnTouchListener(new OnTouchListener() {
            private float mDownX = 0F;
            private float mDownY = 0F;
            private boolean mIsMoving = false;
            private final int MIN_MOVING_PIXELS = getResources().getDimensionPixelSize(R.dimen.min_moving_pixels);

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        mDownX = event.getX();
                        mDownY = event.getY();
                        mIsMoving = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mIsMoving = mIsMoving || isMoving(event);
                        if (mIsMoving) {
                            v.setPressed(false);
                            if (mListener != null) {
                                int x = (int) (event.getRawX() - mDownX);
                                int y = (int) (event.getRawY() - mDownY);
                                mListener.onLayout(x, y);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        if (!mIsMoving) {
                            if (mIsRecording) {
                                MediaProjectionService.stopVideoRecord();
                                mIsRecording = false;
                                mHandler.sendEmptyMessage(MSG_STOP_RECORD);
                            } else {
                                if (MediaProjectionService.serviceType == ServiceType.VIDEO
                                && MediaProjectionService.running) {
                                    MediaProjectionService.startVideoRecord();
                                    mIsRecording = true;
                                    mHandler.sendEmptyMessage(MSG_START_RECORD);
                                } else {
                                    ToastUtils.shortCall("请启动录屏服务！");
                                }
                            }
                        }
                }
                return true;
            }

            private boolean isMoving(MotionEvent event) {
                return Math.abs(event.getX() - mDownX) > MIN_MOVING_PIXELS || Math.abs(event.getY() - mDownY) > MIN_MOVING_PIXELS;
            }
        });
    }

    private String formatTime(int timeSecond) {
        int minute = timeSecond / 60;
        int second = timeSecond - 60 * minute;
        String minuteFormat;
        if (minute >= 100) {
            minuteFormat = "%d";
        } else {
            minuteFormat = "%02d";
        }
        return String.format(minuteFormat + ":%02d", minute, second);
    }

    public void setLayoutListener(ILayoutListener listener) {
        mListener = listener;
    }

    public interface ILayoutListener {
        void onLayout(int x, int y);
    }
}
