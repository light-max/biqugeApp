package com.lifengqiang.biquge.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;

public class SimpleColorPickerView extends View {
    private final Paint paint;
    private final float plateSize;
    private final float drawRadius;
    private final float interval;

    private final float dp1;

    private int[] colors;
    private int[] rColors;
    private int color;
    private int selectIndex = 1;

    private float dx;
    private float dy;

    private OnColorChangeListener onColorChangeListener;

    public SimpleColorPickerView(Context context) {
        this(context, null);
    }

    @SuppressLint({"Recycle", "ClickableViewAccessibility"})
    public SimpleColorPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SimpleColorPickerView);
            plateSize = array.getDimension(R.styleable.SimpleColorPickerView_plateSize, DisplayUtils.dp2px(context, 24));
        } else {
            plateSize = DisplayUtils.dp2px(context, 24);
        }
        drawRadius = plateSize / 2;
        interval = plateSize * 0.4f;
        dp1 = DisplayUtils.dp2px(context, 1);
        setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dx = event.getX();
                    dy = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float ax = Math.abs(dx - event.getX());
                    float ay = Math.abs(dy - event.getY());
                    if (ax < 24 && ay < 24) {
                        if (dy > interval && dy < plateSize + interval) {
                            int n = (int) (dx / (plateSize + interval));
                            if (n < colors.length &&
                                    dx > n * (plateSize + interval) + interval &&
                                    dx < (n + 1) * (plateSize + interval)) {
                                setSelectIndex(n);
                            }
                        }
                    }
                    break;
            }
            return true;
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, (int) (plateSize + interval * 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (colors != null) {
            float mx = drawRadius + interval;
            float my = drawRadius + interval;
            for (int i = 0; i < colors.length; i++) {
                paint.setColor(colors[i]);
                if (colors[i] == color) {
                    canvas.drawCircle(mx, my, (plateSize + interval) / 2 + dp1 * 2, paint);
                } else {
                    canvas.drawCircle(mx, my, drawRadius + dp1 * 2, paint);
                }

                paint.setColor(rColors[i]);
                if (colors[i] == color) {
                    canvas.drawCircle(mx, my, (plateSize + interval) / 2 + dp1, paint);
                } else {
                    canvas.drawCircle(mx, my, drawRadius + dp1, paint);
                }

                paint.setColor(colors[i]);
                if (colors[i] == color) {
                    canvas.drawCircle(mx, my, (plateSize + interval) / 2, paint);
                } else {
                    canvas.drawCircle(mx, my, drawRadius, paint);
                }

                mx += plateSize + interval;
            }
        }
    }

    public void setColors(int[] colors) {
        color = colors[1];
        this.colors = colors;
        rColors = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            int c = colors[i];
            int r = (255 - ((c & 0x00ff0000) >> 16));
            int g = (255 - ((c & 0x0000ff00) >> 8));
            int b = (255 - ((c & 0x000000ff)));
            rColors[i] = 0xff000000 + (r << 16) + (g << 8) + b;
//            System.out.printf("%x\n", rColors[i]);
        }
        invalidate();
    }

    public int[] getColors() {
        return colors;
    }

    /**
     * 会触发{@link OnColorChangeListener}接口
     */
    public void setColor(int color) {
        for (int i = 0; i < colors.length; i++) {
            if (color == colors[i]) {
                this.color = color;
                selectIndex = i;
                invalidate();
                if (onColorChangeListener != null) {
                    onColorChangeListener.onColorChange(this);
                }
                return;
            }
        }
        throw new RuntimeException("color exception");
    }

    /**
     * 会触发{@link OnColorChangeListener}接口
     */
    public void setSelectIndex(int index) {
        selectIndex = index;
        color = colors[index];
        invalidate();
        if (onColorChangeListener != null) {
            onColorChangeListener.onColorChange(this);
        }
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public int getColor() {
        return color;
    }

    public interface OnColorChangeListener {
        void onColorChange(SimpleColorPickerView view);
    }

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.onColorChangeListener = onColorChangeListener;
    }

    public void callOnColorChange() {
        if (onColorChangeListener != null) {
            onColorChangeListener.onColorChange(this);
        }
    }
}
