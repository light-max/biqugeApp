package com.lifengqiang.biquge.ui.bookdetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.activity.BaseActivity;
import com.lifengqiang.biquge.book.parse.bookdetails.BookDetailsParser;

public class BookDetailsActivity extends BaseActivity<BookDetailsModel, BookDetailsView, BookDetailsPresenter> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setContentView(R.layout.activity_book_details);
        int statusBarHeight = immersiveStatusBar();
        addStatusBarFillView(statusBarHeight);
        Intent intent = getIntent();
        map("url", intent.getStringExtra("url"));
        map("parser", intent.getSerializableExtra("parser"));
    }

    @Override
    public void onBackPressed() {
        if (view().allNodeLayoutIsHide()) {
            super.onBackPressed();
        }
    }

    public static void startForUrl(Context context, String url, Class<? extends BookDetailsParser> parser) {
        Intent intent = new Intent(context, BookDetailsActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("parser", parser);
        context.startActivity(intent);
    }
}
