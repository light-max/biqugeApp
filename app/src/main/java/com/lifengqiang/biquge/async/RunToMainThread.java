package com.lifengqiang.biquge.async;

import android.os.Handler;
import android.os.Looper;

public interface RunToMainThread {
    default void runToMainThread(Runnable runnable) {
        if (runnable != null) {
            if (Looper.getMainLooper() != Looper.myLooper()) {
                new Handler(Looper.getMainLooper()).post(runnable);
            } else {
                runnable.run();
            }
        }
    }
}
