package com.lifengqiang.biquge.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.activity.BaseActivity;

public class SearchBookActivity extends BaseActivity<BookSearchModel, BookSearchView, BookSearchPresenter> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setContentView(R.layout.activity_search_book);
        int statusBarHeight = immersiveStatusBar();
        addStatusBarFillView(statusBarHeight);
        String searchKey = getIntent().getStringExtra("searchKey");
        map("searchKey", searchKey);
    }

    public static void startForSearchKey(Context context, String searchKey) {
        Intent intent = new Intent(context, SearchBookActivity.class);
        intent.putExtra("searchKey", searchKey);
        context.startActivity(intent);
    }
}
