package com.lifengqiang.biquge.ui.bookadd.ranking.child;

import android.os.Bundle;

import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BasePresenter;
import com.lifengqiang.biquge.base.recycler.OnLoadMoreListener;
import com.lifengqiang.biquge.net.request.RequestBuilder;
import com.lifengqiang.biquge.ui.bookdetails.BookDetailsActivity;
import com.lifengqiang.biquge.book.parse.bookdetails.OnlineBookDetailsParser;

import java.io.Serializable;

public class RankingChildPresenter extends BasePresenter<RankingChildModel, RankingChildView> {
    private RequestBuilder request;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        request = base.map("request");
        // 防止内存重启后没有request对象导致无法发送请求导致没有内容
        if (savedInstanceState != null) {
            Serializable serializable = savedInstanceState.getSerializable("request");
            if (serializable != null) {
                request = (RequestBuilder) serializable;
            }
            base.map("request", request);
        }
        if (request != null) {
            model.wrapAsyncRequest(request).run();
        }
        model.getNewBook().observe(base, books -> {
            view.addBooks(books);
        });
        view.setOnLoadMoreListener(new OnLoadMoreListener(listener -> {
            if (request != null && model.getNextPageUrl() != null) {
                model.wrapAsyncRequest(request.url(model.getNextPageUrl()))
                        .after(() -> listener.setLoadMoreIng(false))
                        .run();
            }
        }));
        view.setOnClickBookListener(url -> {
            BookDetailsActivity.startForUrl(
                    base.getContext(),
                    url,
                    OnlineBookDetailsParser.class
            );
        });
    }

    public void refresh() {
        if (request != null) {
            model.wrapAsyncRequest(request).run();
        }
    }
}
