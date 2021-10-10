package com.lifengqiang.biquge.ui.chapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.activity.BaseActivity;

public class ChapterActivity extends BaseActivity<ChapterModel, ChapterView, ChapterPresenter> {
    public static final int SELECT_NODE = 0x33;
    public static final String NODE_URL = "node_url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        int statusBarHeight = immersiveStatusBar();
        setStatusBar(getIntent().getBooleanExtra("dark", false));
        map("bookUrl", getIntent().getStringExtra("bookUrl"));
        setContentView(R.layout.activity_chapter);
        addStatusBarFillView(statusBarHeight);
    }

    public static void start(Activity activity, String bookUrl, boolean isDark) {
        Intent intent = new Intent(activity, ChapterActivity.class);
        intent.putExtra("dark", isDark);
        intent.putExtra("bookUrl", bookUrl);
        activity.startActivityForResult(intent, SELECT_NODE);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
}
