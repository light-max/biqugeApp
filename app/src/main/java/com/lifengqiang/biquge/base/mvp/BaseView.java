package com.lifengqiang.biquge.base.mvp;

import android.os.Bundle;
import android.view.View;

import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.call.ViewGet;

public class BaseView implements MvpLifecycle, ViewGet {
    protected Base base;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        this.base = base;
    }

    @Override
    public void onStart(Base base, Bundle savedInstanceState) {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
        this.base = null;
    }

    @Override
    public void onViewCreated(Base base, Bundle savedInstanceState) {
    }

    @Override
    public <T extends View> T get(int viewId) {
        if (base != null) {
            return base.get(viewId);
        } else {
            return null;
        }
    }
}
