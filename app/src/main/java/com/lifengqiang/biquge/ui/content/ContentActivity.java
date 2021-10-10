package com.lifengqiang.biquge.ui.content;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;

import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.activity.BaseActivity;
import com.lifengqiang.biquge.book.ChapterReadRecorder;
import com.lifengqiang.biquge.ui.chapter.ChapterActivity;

public class ContentActivity extends BaseActivity<ContentModel, ContentView, ContentPresenter> {
    // 声明PowerManager.WakeLock
    private PowerManager.WakeLock mWakeLock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setContentView(R.layout.activity_content);
        int statusBarHeight = immersiveStatusBar();
        addStatusBarFillView(statusBarHeight);
        map("bookUrl", getIntent().getStringExtra("bookUrl"));
        map("nodeUrl", getIntent().getStringExtra("nodeUrl"));

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "keep_screen_on_tag");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChapterActivity.SELECT_NODE && resultCode == RESULT_OK) {
            String nodeUrl = data.getStringExtra(ChapterActivity.NODE_URL);
            presenter().setNewChapterUrl(this, nodeUrl);
            presenter().invalidateProgressBar();
        }
    }

    @Override
    public void onBackPressed() {
        if (view().getController().isVisible()) {
            view().getController().toggleVisible();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWakeLock.acquire();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWakeLock.release();
    }

    public static void startForUrl(Context context, String bookUrl, String nodeUrl) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra("bookUrl", bookUrl);
        intent.putExtra("nodeUrl", nodeUrl == null ?
                ChapterReadRecorder.getLastRead(context, bookUrl) :
                nodeUrl);
        context.startActivity(intent);
    }
}
