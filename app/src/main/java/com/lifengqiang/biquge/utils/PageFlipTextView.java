package com.lifengqiang.biquge.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;

public class PageFlipTextView extends View {
    private final TextPaint textPaint;

    private boolean alreadySetLayoutParams = false;
    private StaticLayout layout;
    private StaticLayout buffer;
    private int index = 0;
    private int copyIndex = 0;
    private float mx = 0;
    private float my = 0;

    private float lineHeight = 0;
    private int maxLien = 0;
    private int pageCount = 0;

    private Animation animation;
    private OnActionListener listener;
    private OnPageNumberChangeListener onPageNumberChangeListener;

    private boolean animationEnable = true;

    public PageFlipTextView(Context context) {
        this(context, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    public PageFlipTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mx = event.getX();
                    my = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float absX = Math.abs(event.getX() - mx);
                    float absY = Math.abs(event.getY() - my);
                    if (absX < 150 && absY < 150) {
                        int split = getWidth() / 3;
                        if (mx >= 0 && mx < split) {
                            clickLeft();
                        } else if (mx >= split && mx < split * 2) {
                            clickCenter();
                        } else {
                            clickRight();
                        }
                    }
                    break;
            }
            return true;
        });
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PageFlipTextView);
            int textColor = array.getColor(R.styleable.PageFlipTextView_android_textColor, Color.BLACK);
            int textSize = array.getDimensionPixelSize(R.styleable.PageFlipTextView_android_textSize, 60);
            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize);
            array.recycle();
        } else {
            textPaint.setTextSize(60);
            textPaint.setColor(Color.BLACK);
        }
    }

    public void setText(CharSequence text, boolean tailPage, boolean animation) {
        if (layout != null) {
            buffer = layout;
        }
        if (makeLayout(text, true)) {
            if (lineHeight == 0) {
                lineHeight = (float) layout.getHeight() / layout.getLineCount();
            }
            if (maxLien == 0) {
                maxLien = (int) (getHeight() / lineHeight);
                setClipBounds(new Rect(0, 0, getWidth(), (int) (maxLien * lineHeight)));
            }
            pageCount = layout.getLineCount() / maxLien;
            pageCount += layout.getLineCount() % maxLien == 0 ? 0 : 1;
            copyIndex = index;
            index = tailPage ? index = pageCount - 1 : 0;
            if (onPageNumberChangeListener != null) {
                onPageNumberChangeListener.onPageSelected(pageCount, index);
            }
            // 第一次进来设置布局高度
            if (!alreadySetLayoutParams) {
                resetLayoutParams();
                alreadySetLayoutParams = true;
            }
            // 之后进来设置动画
            else if (animation) {
                int direction = tailPage ? Animation.DIRECTION_LEFT : Animation.DIRECTION_RIGHT;
                postAnimation(direction, null);
            }
        }
    }

    public void setText(CharSequence text, boolean tailPage) {
        setText(text, tailPage, true);
    }

    public void setTextSize(float size) {
        textPaint.setTextSize(size);
        if (layout != null && makeLayout(layout.getText(), false)) {
            lineHeight = (float) layout.getHeight() / layout.getLineCount();
            maxLien = (int) (getHeight() / lineHeight);
            pageCount = layout.getLineCount() / maxLien;
            pageCount += layout.getLineCount() % maxLien == 0 ? 0 : 1;
            index = 0;
            if (onPageNumberChangeListener != null) {
                onPageNumberChangeListener.onPageSelected(pageCount, index);
            }
            invalidate();
        }
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
        invalidate();
    }

    public void setAnimationEnable(boolean animationEnable) {
        this.animationEnable = animationEnable;
    }

    @SuppressLint("DrawAllocation")
    private boolean makeLayout(CharSequence text, boolean isHtml) {
        try {
            int widthSize = MeasureSpec.getSize(getMeasuredWidth());
            int paddingLeft = MeasureSpec.getSize(getPaddingLeft());
            int paddingRight = MeasureSpec.getSize(getPaddingRight());
            String source = isHtml ? translateHtml(text) : String.valueOf(text);
            if (source.isEmpty()) {
                return false;
            }
            layout = StaticLayout.Builder.obtain(
                    source, 0, source.length(),
                    textPaint,
                    widthSize - paddingLeft - paddingRight)
//                    .setLineSpacing(0, 1.2f)
//                    .setIncludePad(true)
                    .build();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String translateHtml(CharSequence text) {
        Spanned spanned = Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_COMPACT);
        return spanned.toString();
    }

    private void resetLayoutParams() {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = getWidth();
        params.height = (int) (maxLien * lineHeight);
        setLayoutParams(params);
    }

    protected void clickLeft() {
        if (index == 0) {
            if (listener != null) {
                listener.onAction(this, ACTION_LEFT_PAGE);
            }
        } else {
            postAnimation(Animation.DIRECTION_LEFT, () -> {
                index--;
                invalidate();
            });
        }
        if (onPageNumberChangeListener != null) {
            onPageNumberChangeListener.onPageSelected(pageCount, index);
        }
    }

    private void clickRight() {
        if (index < pageCount - 1) {
            postAnimation(Animation.DIRECTION_RIGHT, () -> {
                index++;
                invalidate();
            });
        } else {
            if (listener != null) {
                listener.onAction(this, ACTION_RIGHT_PAGE);
            }
        }
        if (onPageNumberChangeListener != null) {
            onPageNumberChangeListener.onPageSelected(pageCount, index);
        }
    }

    private void clickCenter() {
        if (listener != null) {
            listener.onAction(this, ACTION_CLICK_CENTER);
        }
    }

    /**
     * @param runnable 动画可执行时 在执行调用的回调
     */
    private void postAnimation(int direction, Runnable runnable) {
        if (animationEnable) {
            if (animation == null) {
                if (direction == Animation.DIRECTION_LEFT) {
                    animation = new LeftInAnimation(getWidth(), () -> {
                        new Handler(Looper.getMainLooper()).post(this::invalidate);
                    });
                } else /*if (direction == Animation.DIRECTION_RIGHT)*/ {
                    animation = new RightInAnimation(getWidth(), () -> {
                        new Handler(Looper.getMainLooper()).post(this::invalidate);
                    });
                }
                if (runnable != null) {
                    runnable.run();
                }
                animation.start();
            } else {
                if (!animation.isExec()) {
                    animation = null;
                    postAnimation(direction, runnable);
                }
            }
        } else {
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (layout != null) {
            canvas.translate(getPaddingLeft(), 0);
            float layoutHeight = maxLien * lineHeight;
            if (animation != null && animation.isExec()) {
                if (animation instanceof LeftInAnimation) {
                    canvas.translate(animation.offsetX - animation.width, layoutHeight * -index);
                    layout.draw(canvas);
                    canvas.translate(-(animation.offsetX - animation.width), -(layoutHeight * -index));
                    if (buffer == null) {
                        canvas.translate(animation.offsetX, layoutHeight * -(index + 1));
                        layout.draw(canvas);
                    } else {
                        canvas.translate(animation.offsetX, layoutHeight * -copyIndex);
                        buffer.draw(canvas);
                    }
                } else if (animation instanceof RightInAnimation) {
                    canvas.translate(animation.offsetX, layoutHeight * -index);
                    layout.draw(canvas);
                    canvas.translate(-(animation.offsetX), -(layoutHeight * -index));
                    if (buffer == null) {
                        canvas.translate(animation.offsetX - animation.width, layoutHeight * -(index - 1));
                        layout.draw(canvas);
                    } else {
                        canvas.translate(animation.offsetX - animation.width, layoutHeight * -copyIndex);
                        buffer.draw(canvas);
                    }
                }
            } else {
                canvas.translate(0, layoutHeight * -index);
                layout.draw(canvas);
                buffer = null;
            }
        }
    }

    public static final int ACTION_LEFT_PAGE = 1;
    public static final int ACTION_RIGHT_PAGE = 2;
    public static final int ACTION_CLICK_CENTER = 3;

    public interface OnActionListener {
        void onAction(PageFlipTextView view, int action);
    }

    public interface OnPageNumberChangeListener {
        void onPageSelected(int count, int index);
    }

    public void setListener(OnActionListener listener) {
        this.listener = listener;
    }

    public void setOnPageNumberChangeListener(OnPageNumberChangeListener onPageNumberChangeListener) {
        this.onPageNumberChangeListener = onPageNumberChangeListener;
    }

    static class LeftInAnimation extends Animation {
        public LeftInAnimation(int width, Runnable drawRunnable) {
            super(width, drawRunnable);
        }

        @Override
        public void run() {
            setExec(true);
            while (true) {
                offsetX += step;
                drawRunnable.run();
                if (offsetX < width) {
                    sleep();
                } else {
                    offsetX = width;
                    setExec(false);
                    drawRunnable.run();
                    break;
                }
            }
        }
    }

    static class RightInAnimation extends Animation {
        public RightInAnimation(int width, Runnable drawRunnable) {
            super(width, drawRunnable);
        }

        @Override
        public void run() {
            offsetX = width;
            setExec(true);
            while (true) {
                offsetX -= step;
                drawRunnable.run();
                if (offsetX > 0) {
                    sleep();
                } else {
                    offsetX = 0;
                    setExec(false);
                    drawRunnable.run();
                    break;
                }
            }
        }
    }

    static class Animation extends Thread {
        public static final int DIRECTION_LEFT = -1;
        public static final int DIRECTION_RIGHT = 1;

        protected final int width;
        protected int offsetX = 0;
        protected final int step;
        protected final Object lock = new Object();
        protected boolean exec = false;

        protected final Runnable drawRunnable;

        public Animation(int width, Runnable drawRunnable) {
            this.width = width;
            this.drawRunnable = drawRunnable;
            step = (int) (width / 30f);
        }

        protected void sleep() {
            try {
                Thread.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized boolean isExec() {
            synchronized (lock) {
                return exec;
            }
        }

        public synchronized void setExec(boolean exec) {
            synchronized (lock) {
                this.exec = exec;
            }
        }
    }
}
