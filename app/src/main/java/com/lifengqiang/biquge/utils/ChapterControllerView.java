package com.lifengqiang.biquge.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;

public class ChapterControllerView extends LinearLayout {
    private final FrameLayout mask;
    private final ChapterTitleBarView page;
    private final ChapterReaderControllerView reader;
    private OnActionListener onActionListener;

    public ChapterControllerView(Context context) {
        this(context, null);
    }

    public ChapterControllerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_chapter_controller, this);
        mask = findViewById(R.id.mask);
        page = findViewById(R.id.page);
        reader = findViewById(R.id.reader);
        setTitleBarEvent();
        setColorPicker();
        setFontSizePicker();
    }

    private SharedPreferences getThemesSharedPreferences() {
        return getContext().getSharedPreferences("themes", 0);
    }

    private void setTitleBarEvent() {
        page.getBackButton().setOnClickListener(v -> {
            if (onActionListener != null) {
                onActionListener.onBack();
            }
        });
        page.getOpenList().setOnClickListener(v -> {
            if (onActionListener != null) {
                onActionListener.onOpenList();
            }
        });
        page.getPreviousPageButton().setOnClickListener(v -> {
            if (onActionListener != null) {
                onActionListener.onPreviousChapter();
            }
        });
        page.getNextPageButton().setOnClickListener(v -> {
            if (onActionListener != null) {
                onActionListener.onNextChapter();
            }
        });
        page.getProgressBar().setOnSeekListener(new ChapterPositionBar.OnSeekListener() {
            @Override
            public void onSeek(int max, int current) {
                if (onActionListener != null) {
                    onActionListener.onSeekChapter(false, current - 1);
                }
            }

            @Override
            public void onStop(int max, int current) {
                if (onActionListener != null) {
                    onActionListener.onSeekChapter(true, current - 1);
                }
            }
        });
    }

    private void setColorPicker() {
        String textColorSelectIndex = "textColorSelectIndex";
        String backgroundColorSelectIndex = "backgroundColorSelectIndex";
        reader.getTextColorPicker().setOnColorChangeListener(view -> {
            if (onActionListener != null) {
                getThemesSharedPreferences().edit()
                        .putInt(textColorSelectIndex, view.getSelectIndex())
                        .apply();
                onActionListener.onSetTextColor(view.getColor());
            }
        });
        reader.getBackgroundColorPicker().setOnColorChangeListener(view -> {
            if (onActionListener != null) {
                getThemesSharedPreferences().edit()
                        .putInt(backgroundColorSelectIndex, view.getSelectIndex())
                        .apply();
                onActionListener.onSetBackgroundColor(view.getColor());
            }
        });
        SharedPreferences preferences = getThemesSharedPreferences();
        int textColor = preferences.getInt(textColorSelectIndex, 0);
        int backgroundColor = preferences.getInt(backgroundColorSelectIndex, 0);
        reader.getTextColorPicker().setSelectIndex(textColor);
        reader.getBackgroundColorPicker().setSelectIndex(backgroundColor);
    }

    private void setFontSizePicker() {
        String[] fontNumberTextArray = getResources().getStringArray(R.array.font_number);
        int[] fontNumberIdArray = new int[]{
                R.dimen.sp12, R.dimen.sp14, R.dimen.sp16, R.dimen.sp18,
                R.dimen.sp20, R.dimen.sp22, R.dimen.sp24, R.dimen.sp26, R.dimen.sp28,
                R.dimen.sp30, R.dimen.sp32, R.dimen.sp36,
                R.dimen.sp40, R.dimen.sp48,
                R.dimen.sp52, R.dimen.sp56,
                R.dimen.sp60, R.dimen.sp64,
        };
        String fontNumberIndex = "fontNumberIndex";
        reader.getFontSizeReduceButton().setOnClickListener(v -> {
            SharedPreferences sp = getThemesSharedPreferences();
            int index = sp.getInt(fontNumberIndex, 5);
            if (index > 0) {
                --index;
                sp.edit().putInt(fontNumberIndex, index).apply();
                reader.callOnFontSizeChange();
            } else {
                Toast.makeText(getContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
        reader.getFontSizeAddButton().setOnClickListener(v -> {
            SharedPreferences sp = getThemesSharedPreferences();
            int index = sp.getInt(fontNumberIndex, 4);
            if (index < fontNumberTextArray.length - 1) {
                ++index;
                sp.edit().putInt(fontNumberIndex, index).apply();
                reader.callOnFontSizeChange();
            } else {
                Toast.makeText(getContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
        reader.setFontSizeChangeRunnable(() -> {
            int index = getThemesSharedPreferences().getInt(fontNumberIndex, 3);
            reader.getFontSizeValue().setText(fontNumberTextArray[index]);
            if (onActionListener != null) {
                onActionListener.onSetFontSize(getResources().getDimension(fontNumberIdArray[index]));
            }
        });
    }

    public void toggleVisible() {
        if (isVisible()) {
            page.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.chapter_controller_top_out));
            reader.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.chapter_controller_bottom_out));
            mask.setVisibility(GONE);
            page.setVisibility(GONE);
            reader.setVisibility(GONE);
        } else {
            page.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.chapter_controller_top_in));
            reader.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.chapter_controller_bottom_in));
            mask.setVisibility(VISIBLE);
            page.setVisibility(VISIBLE);
            reader.setVisibility(VISIBLE);
        }
    }

    public void setTitleText(CharSequence title) {
        page.setText(title);
    }

    public void setProgressMax(int max) {
        page.getProgressBar().setMax(max);
    }

    public void setProgress(int progress) {
        page.getProgressBar().setCurrent(progress);
    }

    public void setTextColor(int color) {
        page.setTextColor(color);
        reader.setTextColor(color);
    }

    public void setWidgetBackgroundColor(int color) {
        page.setWidgetBackground(color);
        reader.setWidgetBackground(color);
    }

    public boolean isVisible() {
        return page.getVisibility() == reader.getVisibility() &&
                page.getVisibility() == VISIBLE;
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
        reader.callOnColorChange();
        reader.callOnFontSizeChange();
    }

    public interface OnActionListener {
        /**
         * ??????????????????????????????
         */
        void onPreviousChapter();

        /**
         * ?????????????????????????????????
         */
        void onNextChapter();

        /**
         * ???????????????????????????????????????
         */
        void onSetTextColor(int color);

        /**
         * ????????????????????????????????????
         */
        void onSetBackgroundColor(int color);

        /**
         * ????????????????????????????????????
         */
        void onSetFontSize(float size);

        /**
         * ?????????????????????
         */
        void onBack();

        /**
         * ???????????????????????????????????????
         */
        void onOpenList();

        /**
         * ??????????????????????????????????????????
         *
         * @param checked ?????????????????????????????????
         * @param index   ????????????
         */
        void onSeekChapter(boolean checked, int index);
    }
}
