package com.lifengqiang.biquge.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;

public class ChapterPositionBar extends View {
    private final Paint paint;

    private int max = 100;
    private int current = 1;
    private final float scrollBarSize;

    private OnSeekListener onSeekListener;
    private final float f12 = 1.2f;

    public ChapterPositionBar(Context context) {
        this(context, null);
    }

    @SuppressLint({"ClickableViewAccessibility", "Recycle"})
    public ChapterPositionBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ChapterPositionBar);
            scrollBarSize = array.getDimension(R.styleable.ChapterPositionBar_android_scrollbarSize, DisplayUtils.dp2px(context, 12));
        } else {
            scrollBarSize = DisplayUtils.dp2px(context, 12);
        }
        paint = new Paint();
        paint.setTextSize(DisplayUtils.dp2px(context, 12));
        paint.setColor(Color.RED);
        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float v1 = event.getX() / getWidth();
                int newCurrent = (int) (max * v1) + 1;
                if (newCurrent <= 0) {
                    newCurrent = 1;
                } else if (newCurrent > max) {
                    newCurrent = max;
                }
                if (newCurrent != current) {
                    current = newCurrent;
                    if (onSeekListener != null) {
                        onSeekListener.onSeek(max, current);
                    }
                    invalidate();
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (onSeekListener != null) {
                    onSeekListener.onStop(max, current);
                }
            }
            return true;
        });
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(scrollBarSize * f12, (getHeight() - scrollBarSize) / 2);
        String text = String.format("%d/%d", current, max);
        canvas.drawText(text, (getWidth() - paint.measureText(text)) / 2, -scrollBarSize, paint);
        float dx = (getWidth() - scrollBarSize * f12 * 2) * ((float) current / max);
        canvas.drawRect(0, 0, dx, scrollBarSize, paint);
        canvas.drawCircle(dx, scrollBarSize / 2, scrollBarSize * f12, paint);
        canvas.drawRect(dx, scrollBarSize * 0.25f, getWidth(), scrollBarSize * 0.75f, paint);
    }

    public void setColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public void setCurrent(int current) {
        this.current = current;
        invalidate();
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
