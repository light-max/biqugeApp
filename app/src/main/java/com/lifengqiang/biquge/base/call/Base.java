package com.lifengqiang.biquge.base.call;

import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.lifengqiang.biquge.async.RunToMainThread;
import com.lifengqiang.biquge.async.RunToMainThreadImpl;

public interface Base extends ViewGet, KeyValue, LifecycleOwner {
    default Context getContext() {
        if (this instanceof Context) {
            return (Context) this;
        } else if (this instanceof Fragment) {
            return ((Fragment) this).requireContext();
        }
        throw new RuntimeException(getClass().getTypeName() + "没有实现此方法");
    }

    default FragmentActivity getActivity() {
        if (this instanceof FragmentActivity) {
            return (FragmentActivity) this;
        } else if (this instanceof Fragment) {
            return ((Fragment) this).getActivity();
        }
        throw new RuntimeException(getClass().getTypeName() + "没有实现此方法");
    }

    default Fragment getFragment() {
        if (this instanceof Fragment) {
            return (Fragment) this;
        }
        throw new RuntimeException(getClass().getTypeName() + "无法转换为fragment");
    }

    default void toast(String message) {
        if (getContext() != null) {
            new RunToMainThreadImpl().runToMainThread(() -> {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            });
        }
    }
}
