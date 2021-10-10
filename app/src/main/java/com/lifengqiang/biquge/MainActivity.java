package com.lifengqiang.biquge;

import android.os.Bundle;

import com.lifengqiang.biquge.base.activity.NoMvpActivity;
import com.lifengqiang.biquge.book.BookFileManager;
import com.lifengqiang.biquge.ui.bookshelf.BookShelfActivity;

public class MainActivity extends NoMvpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BookFileManager.setApplicationContext(getApplicationContext());
        hideActionBar();
        setContentView(R.layout.activity_main);
        int statusBarHeight = immersiveStatusBar();
        addStatusBarFillView(statusBarHeight);
        mainHandler.postDelayed(() -> {
            open(BookShelfActivity.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1000);
    }
}