package com.lifengqiang.biquge.base.call;

import android.app.Activity;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public interface ViewGet {
    default <T extends View> T get(@IdRes int viewId) {
        if (this instanceof Activity) {
            return ((Activity) this).findViewById(viewId);
        } else if (this instanceof Fragment) {
            return Objects.requireNonNull(((Fragment) this).getView()).findViewById(viewId);
        }
        throw new RuntimeException(getClass().getTypeName() + "没有实现此方法");
    }

    default ViewGet click(@IdRes int viewId, Runnable listener) {
        get(viewId).setOnClickListener(v -> listener.run());
        return this;
    }

    default ViewGet click(@IdRes int viewId, View.OnClickListener listener) {
        get(viewId).setOnClickListener(listener);
        return this;
    }

    default ViewGet click(View view, Runnable listener) {
        view.setOnClickListener(v -> listener.run());
        return this;
    }

    default ViewGet click(View view, View.OnClickListener listener) {
        view.setOnClickListener(listener);
        return this;
    }
}
