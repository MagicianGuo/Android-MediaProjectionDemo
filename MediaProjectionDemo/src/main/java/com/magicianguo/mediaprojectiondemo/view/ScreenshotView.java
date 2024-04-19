package com.magicianguo.mediaprojectiondemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.magicianguo.mediaprojectiondemo.R;
import com.magicianguo.mediaprojectiondemo.service.MediaProjectionService;
import com.magicianguo.mediaprojectiondemo.util.TaskPool;

public class ScreenshotView extends FrameLayout {
    @Nullable
    private ILayoutListener mListener;

    public ScreenshotView(@NonNull Context context) {
        super(context);
        init();
    }

    public ScreenshotView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScreenshotView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_screenshot_view, this);
        findViewById(R.id.tv_screenshot).setOnTouchListener(new OnTouchListener() {
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
                            ScreenshotView.this.setVisibility(GONE);
                            TaskPool.CACHE.execute(() -> {
                                try {
                                    Thread.sleep(200L);
                                    TaskPool.MAIN.post(MediaProjectionService::screenshot);
                                    Thread.sleep(200L);
                                    TaskPool.MAIN.post(() -> ScreenshotView.this.setVisibility(VISIBLE));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                }
                return true;
            }

            private boolean isMoving(MotionEvent event) {
                return Math.abs(event.getX() - mDownX) > MIN_MOVING_PIXELS || Math.abs(event.getY() - mDownY) > MIN_MOVING_PIXELS;
            }
        });
    }

    public void setLayoutListener(ILayoutListener listener) {
        mListener = listener;
    }

    public interface ILayoutListener {
        void onLayout(int x, int y);
    }
}
