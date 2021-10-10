package com.lifengqiang.biquge.ui.bookadd.recommend;

import android.os.Bundle;

import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BasePresenter;
import com.lifengqiang.biquge.ui.bookdetails.BookDetailsActivity;
import com.lifengqiang.biquge.book.parse.bookdetails.OnlineBookDetailsParser;
import com.lifengqiang.biquge.ui.search.SearchBookActivity;

public class RecommendPresenter extends BasePresenter<RecommendModel, RecommendView> {
    @Override
    public void onViewCreated(Base base, Bundle savedInstanceState) {
        view.getRefreshLayout().setOnRefreshListener(() -> {
            model.generateHomeRequest().after(() -> {
                view.getRefreshLayout().setRefreshing(false);
            }).run();
        });
        view.getSearchView().setOnSearchListener(text -> {
            SearchBookActivity.startForSearchKey(base.getContext(), text);
        });
        model.getRecommend().observe(base, recommendBooks -> {
            view.setRecommend(recommendBooks);
        });
        model.getPopular().observe(base, popularBooks -> {
            view.setPopular(popularBooks);
        });
        model.getNewest().observe(base, newestBooks -> {
            view.setNewest(newestBooks);
        });
        view.setOnClickBookListener(url -> {
            BookDetailsActivity.startForUrl(
                    base.getContext(),
                    url,
                    OnlineBookDetailsParser.class
            );
        });
    }
}
