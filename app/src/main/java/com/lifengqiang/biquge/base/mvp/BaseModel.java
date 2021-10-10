package com.lifengqiang.biquge.base.mvp;

import android.os.Bundle;

import com.lifengqiang.biquge.async.RunToMainThread;
import com.lifengqiang.biquge.base.call.Base;

public class BaseModel implements MvpLifecycle, RunToMainThread {
    protected Base base;

    @Override
    public void onViewCreated(Base base, Bundle savedInstanceState) {
    }

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
        base = null;
    }
}
