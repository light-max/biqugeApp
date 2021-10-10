package com.lifengqiang.biquge.ui.content;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseView;
import com.lifengqiang.biquge.book.ReadAnimationSetting;
import com.lifengqiang.biquge.book.bean.BookChapter;
import com.lifengqiang.biquge.ui.chapter.ChapterActivity;
import com.lifengqiang.biquge.utils.ChapterControllerView;
import com.lifengqiang.biquge.utils.PageFlipTextView;

public class ContentView extends BaseView implements PageFlipTextView.OnActionListener, ChapterControllerView.OnActionListener {
    private PageFlipTextView view;
    private TextView miniTitle;
    private TextView pageNumber;
    private ChapterControllerView controller;
    private OnChapterChangeListener onChapterChangeListener;

    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        view = get(R.id.content);
        boolean isScroll = ReadAnimationSetting.isScroll(base.getContext());
        view.setAnimationEnable(isScroll);
        miniTitle = get(R.id.mini_title);
        pageNumber = get(R.id.page_number);
        controller = get(R.id.controller);
        view.setListener(this);
        view.setOnPageNumberChangeListener((count, index) -> {
            pageNumber.setText(String.format("%d/%d", index + 1, count));
        });
        controller.setOnActionListener(this);
    }

    public void setContent(BookChapter chapter, boolean tailPage, boolean animation) {
        view.setText(chapter.getHtmlContent(), tailPage, animation);
    }

    public void setMaxChapterNumber(int count) {
        controller.setProgressMax(count);
    }

    public void setProgress(int progress) {
        controller.setProgress(progress);
    }

    public void setMiniTitle(String chapterTitle) {
        miniTitle.setText(chapterTitle);
        if (controller.isVisible()) {
            controller.setTitleText(chapterTitle);
        }
    }

    public void setEnable(boolean enable) {
        if (view != null) {
            view.setEnabled(enable);
        }
    }

    public ChapterControllerView getController() {
        return controller;
    }

    @Override
    public void onAction(PageFlipTextView view, int action) {
        switch (action) {
            case PageFlipTextView.ACTION_LEFT_PAGE:
                if (onChapterChangeListener != null) {
                    onChapterChangeListener.onChange(true);
                }
                break;
            case PageFlipTextView.ACTION_RIGHT_PAGE:
                if (onChapterChangeListener != null) {
                    onChapterChangeListener.onChange(false);
                }
                break;
            case PageFlipTextView.ACTION_CLICK_CENTER:
                controller.toggleVisible();
                if (controller.isVisible()) {
                    controller.setTitleText(miniTitle.getText());
                }
                break;
        }
    }

    @Override
    public void onPreviousChapter() {
        if (onChapterChangeListener != null) {
            onChapterChangeListener.onChange(true);
        }
    }

    @Override
    public void onNextChapter() {
        if (onChapterChangeListener != null) {
            onChapterChangeListener.onChange(false);
        }
    }

    @Override
    public void onSetTextColor(int color) {
        view.setTextColor(color);
        controller.setTextColor(color);
        miniTitle.setTextColor(color);
        pageNumber.setTextColor(color);
    }

    @Override
    public void onSetBackgroundColor(int color) {
        get(R.id.root).setBackgroundColor(color);
        controller.setWidgetBackgroundColor(color);
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        boolean dark = darkness > 0.5;
        base.map("dark", dark);
        ((ContentActivity) base.getActivity()).setStatusBar(dark);
    }

    @Override
    public void onSetFontSize(float size) {
        view.setTextSize(size);
    }

    @Override
    public void onBack() {
        base.getActivity().finish();
    }

    @Override
    public void onOpenList() {
        FragmentActivity activity = base.getActivity();
        ChapterActivity.start(activity,
                ((ContentActivity) activity).model().getParser().getBookUrl(),
                base.map("dark"));
        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public void onSeekChapter(boolean checked, int index) {
        if (onChapterChangeListener != null) {
            if (checked) {
                onChapterChangeListener.setByPosition(index);
            } else {
                onChapterChangeListener.findTitle(index);
            }
        }
    }

    public interface OnChapterChangeListener {
        void onChange(boolean previous);

        void findTitle(int position);

        void setByPosition(int position);
    }

    public void setOnChapterChangeListener(OnChapterChangeListener onChapterChangeListener) {
        this.onChapterChangeListener = onChapterChangeListener;
    }
}
