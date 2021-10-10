package com.lifengqiang.biquge.base.mvp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.lifengqiang.biquge.base.call.Base;

public class BasePresenter<Model extends BaseModel, View extends BaseView> implements MvpLifecycle {
    protected Handler mainHandler;
    protected Model model;
    protected View view;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        mainHandler = new Handler(Looper.getMainLooper());
        MvpObjectGetListener listener = (MvpObjectGetListener) base;
        model = (Model) listener.model();
        view = (View) listener.view();
    }

    @Override
    public void onViewCreated(Base base, Bundle savedInstanceState) {
    }

    @Override
    public void onStart(Base base, Bundle savedInstanceState) {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
        model = null;
        view = null;
        mainHandler = null;
    }
}
