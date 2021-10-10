package com.lifengqiang.biquge.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;

public class ChapterReaderControllerView extends LinearLayout {
    private Runnable fontSizeChangeRunnable;

    public ChapterReaderControllerView(Context context) {
        this(context, null);
    }

    public ChapterReaderControllerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_chapter_reader_controller, this);
        getTextColorPicker().setColors(context.getResources().getIntArray(R.array.textColors));
        getBackgroundColorPicker().setColors(context.getResources().getIntArray(R.array.backgroundColors));
    }

    public void callOnColorChange() {
        getTextColorPicker().callOnColorChange();
        getBackgroundColorPicker().callOnColorChange();
    }

    public void setTextColor(int color) {
        for (int id : new int[]{R.id.text0, R.id.text1, R.id.text2, R.id.text3, R.id.font_size_value}) {
            TextView t = findViewById(id);
            t.setTextColor(color);
        }
        for (int id : new int[]{R.id.font_size_reduce, R.id.font_size_add}) {
            ImageView image = findViewById(id);
            image.setColorFilter(color);
        }
    }

    public void setWidgetBackground(int color) {
        setBackgroundColor(color);
    }

    public SimpleColorPickerView getTextColorPicker() {
        return findViewById(R.id.text_color);
    }

    public SimpleColorPickerView getBackgroundColorPicker() {
        return findViewById(R.id.background_color);
    }

    public TextView getFontSizeValue() {
        return findViewById(R.id.font_size_value);
    }

    public ImageView getFontSizeReduceButton() {
        return findViewById(R.id.font_size_reduce);
    }

    public ImageView getFontSizeAddButton() {
        return findViewById(R.id.font_size_add);
    }

    public void setFontSizeChangeRunnable(Runnable runnable) {
        this.fontSizeChangeRunnable = runnable;
    }

    public void callOnFontSizeChange() {
        if (fontSizeChangeRunnable != null) {
            fontSizeChangeRunnable.run();
        }
    }
}
