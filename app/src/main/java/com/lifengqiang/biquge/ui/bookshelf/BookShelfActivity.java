package com.lifengqiang.biquge.ui.bookshelf;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.activity.BaseActivity;

public class BookShelfActivity extends BaseActivity<BookShelfModel, BookShelfView, BookShelfPresenter> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setContentView(R.layout.activity_book_shelf);
        int statusBarHeight = immersiveStatusBar();
        addStatusBarFillView(statusBarHeight);
    }
}
