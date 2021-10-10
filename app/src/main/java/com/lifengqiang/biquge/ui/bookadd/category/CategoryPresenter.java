package com.lifengqiang.biquge.ui.bookadd.category;

import android.os.Bundle;

import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BasePresenter;

public class CategoryPresenter extends BasePresenter<CategoryModel, CategoryView> {
    @Override
    public void onViewCreated(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        model.getClassification().observe(base, bookClassifications -> {
            view.setClassification(bookClassifications);
        });
        view.getRefreshLayout().setOnRefreshListener(() -> {
            model.generateAsyncRequest().after(() -> {
                view.clearFragments();
                view.getRefreshLayout().setRefreshing(false);
            }).run();
        });
    }
}
