package com.lifengqiang.biquge.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;

public class VerticalSeekBar extends View {
    private final Paint paint;
    private final float scrollbarSize;
    private final float pointSize;

    private final float pointRadius;
    private final RectF rect;

    private int max = 100;
    private int progress = 1;

    private boolean touching = false;

    private OnSeekListener onSeekListener;

    public VerticalSeekBar(Context context) {
        this(context, null);
    }

    @SuppressLint({"Recycle", "ClickableViewAccessibility"})
    public VerticalSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar);
            scrollbarSize = array.getDimension(R.styleable.VerticalSeekBar_android_scrollbarSize, DisplayUtils.dp2px(context, 4));
        } else {
            scrollbarSize = DisplayUtils.dp2px(context, 4);
        }
        pointSize = scrollbarSize * 3;
        pointRadius = pointSize / 2;
        rect = new RectF();
        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float v1 = event.getY() / getHeight();
                int newCurrent = (int) (max * v1) + 1;
                if (newCurrent <= 0) {
                    newCurrent = 1;
                } else if (newCurrent > max) {
                    newCurrent = max;
                }
                if (newCurrent != progress) {
                    progress = newCurrent;
                    if (onSeekListener != null) {
                        onSeekListener.onSeek(max, progress);
                    }
                    invalidate();
                }
                touching = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (onSeekListener != null) {
                    onSeekListener.onStop(max, progress);
                }
                touching = false;
            }
            return true;
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension((int) pointSize, measuredHeight);
        rect.left = (pointSize - scrollbarSize) / 2;
        rect.right = rect.left + scrollbarSize;
        rect.top = pointRadius;
        rect.bottom = measuredHeight - pointRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float pointOffset = (float) progress / max * (getHeight() - pointSize) + pointRadius;
        canvas.drawRect(rect, paint);
        canvas.drawCircle(pointRadius, pointOffset, pointRadius, paint);
    }

    public void setColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public void setProgress(int progress) {
        if (!touching) {
            this.progress = Math.min(progress, max);
            invalidate();
        }
    }

    public boolean isTouching() {
        return touching;
    }

    /**
     * current从1开始
     */
    public interface OnSeekListener {
        void onSeek(int max, int current);

        void onStop(int max, int current);
    }

    public void setOnSeekListener(OnSeekListener onSeekListener) {
        this.onSeekListener = onSeekListener;
    }
}
