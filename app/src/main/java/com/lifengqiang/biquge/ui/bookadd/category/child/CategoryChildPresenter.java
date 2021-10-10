package com.lifengqiang.biquge.ui.bookadd.category.child;

import android.os.Bundle;

import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BasePresenter;
import com.lifengqiang.biquge.base.recycler.OnLoadMoreListener;
import com.lifengqiang.biquge.ui.bookdetails.BookDetailsActivity;
import com.lifengqiang.biquge.book.parse.bookdetails.OnlineBookDetailsParser;

public class CategoryChildPresenter extends BasePresenter<CategoryChildModel, CategoryChildView> {
    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        String url = base.map("url");
        // 内存重启
        if (url == null && savedInstanceState != null) {
            url = savedInstanceState.getString("url");
            if (url != null) {
                base.map("url", url);
            }
        }
        if (url != null) {
            model.generateAsyncRequest(url).run();
        }
        model.getNewBooks().observe(base, books -> {
            view.addBooks(books);
        });
        view.setOnLoadMoreListener(new OnLoadMoreListener(listener -> {
            model.generateNextPageAsyncRequest().after(() -> {
                listener.setLoadMoreIng(false);
            }).run();
        }));
        view.setOnClickBookListener(bookUrl -> {
            BookDetailsActivity.startForUrl(
                    base.getContext(),
                    bookUrl,
                    OnlineBookDetailsParser.class
            );
        });
    }
}
