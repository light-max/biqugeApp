package com.lifengqiang.biquge.base.mvp;

public interface MvpObjectGetListener<Model extends BaseModel, View extends BaseView, Presenter extends BasePresenter> {
    Model model();

    View view();

    Presenter presenter();
}
