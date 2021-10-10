package com.lifengqiang.biquge.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lifengqiang.biquge.R;

public class ChapterTitleBarView extends LinearLayout {
    public ChapterTitleBarView(Context context) {
        this(context, null);
    }

    public ChapterTitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_chapter_title_bar, this);
    }

    public void setText(CharSequence text) {
        TextView view = findViewById(R.id.text);
        view.setText(text);
    }

    public TextView getTitleTextView() {
        return findViewById(R.id.text);
    }

    public TextView getPreviousPageButton() {
        return findViewById(R.id.previous);
    }

    public TextView getNextPageButton() {
        return findViewById(R.id.next);
    }

    public ImageView getBackButton() {
        return findViewById(R.id.back);
    }

    public ImageView getOpenList() {
        return findViewById(R.id.open_list);
    }

    public ChapterPositionBar getProgressBar() {
        return findViewById(R.id.progress_bar);
    }

    public void setTextColor(int color) {
        getTitleTextView().setTextColor(color);
        getBackButton().setColorFilter(color);
        getOpenList().setColorFilter(color);
        getPreviousPageButton().setTextColor(color);
        getNextPageButton().setTextColor(color);
        getProgressBar().setColor(color);
    }

    public void setWidgetBackground(int color) {
        setBackgroundColor(color);
    }
}
