package com.lifengqiang.biquge.ui.bookadd;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.activity.BaseActivity;

public class BookAddActivity extends BaseActivity<BookAddModel, BookAddView, BookAddPresenter> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setContentView(R.layout.activity_book_add);
        int statusBarHeight = immersiveStatusBar();
        addStatusBarFillView(statusBarHeight);
    }
}
