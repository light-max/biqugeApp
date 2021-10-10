package com.lifengqiang.biquge.ui.search;

import android.os.Bundle;

import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BasePresenter;
import com.lifengqiang.biquge.ui.bookdetails.BookDetailsActivity;
import com.lifengqiang.biquge.book.parse.bookdetails.OnlineBookDetailsParser;

public class BookSearchPresenter extends BasePresenter<BookSearchModel, BookSearchView> {
    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        view.getSearchView().setOnSearchListener(text -> {
            model.generateSearchRequest(text)
                    .before(() -> view.showProgress())
                    .after(() -> view.hideProgress())
                    .success(data -> view.setSearchBooks(data))
                    .run();
        });
        view.getSearchView().callOnSearch();
        view.setOnClickBookListener(url -> {
            BookDetailsActivity.startForUrl(
                    base.getContext(),
                    url,
                    OnlineBookDetailsParser.class
            );
        });
    }
}
