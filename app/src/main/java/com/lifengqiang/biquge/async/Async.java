package com.lifengqiang.biquge.async;

import android.os.Handler;
import android.os.Looper;

public class Async implements RunToMainThread {
    private static final boolean debug = true;

    private Thread mThread;

    private Call.OnBefore before;
    private Call.OnReturnData success;
    private Call.OnSuccess success2;
    private Call.OnError error;
    private Call.OnAfter after;

    private AsyncTaskRunnable runnable;

    private boolean useMainHandler = true;

    public void go() {
        runToMainThread(before);
        mThread = new Thread(() -> {
            try {
                Object result = runnable == null ? null : runnable.run();
                if (result instanceof AsyncTaskError) {
                    runToMainThread(() -> {
                        AsyncTaskError e = (AsyncTaskError) result;
                        if (debug) {
                            if (e.getException() != null) {
                                e.getException().printStackTrace();
                            }
                        }
                        if (error != null) {
                            error.onError(e.getMessage(), e.getException());
                        }
                    });
                } else {
                    if (success != null) {
                        runToMainThread(() -> {
                            success.onSuccess(result);
                        });
                    }
                    runToMainThread(success2);
                }
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
                if (error != null) {
                    runToMainThread(() -> {
                        error.onError(null, e);
                    });
                }
            }
            runToMainThread(after);
        }, this.toString());
        mThread.start();
    }

    @Override
    public void runToMainThread(Runnable runnable) {
        if (runnable != null) {
            if (useMainHandler && Looper.getMainLooper() != Looper.myLooper()) {
                new Handler(Looper.getMainLooper()).post(runnable);
            } else {
                runnable.run();
            }
        }
    }

    public void join() {
        try {
            mThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private Async async = new Async();

        public Builder<T> before(Call.OnBefore before) {
            async.before = before;
            return this;
        }

        public Builder<T> success(Call.OnReturnData<T> success) {
            async.success = success;
            return this;
        }

        public Builder<T> success(Call.OnSuccess success) {
            async.success2 = success;
            return this;
        }

        public Builder<T> error(Call.OnError error) {
            async.error = error;
            return this;
        }

        public Builder<T> after(Call.OnAfter after) {
            async.after = after;
            return this;
        }

        public Builder<T> task(AsyncTaskRunnable task) {
            async.runnable = task;
            return this;
        }

        /**
         * 为了解决{@link #join()}无法等待 success(data)回调执行的问题而产生的解决方法
         *
         * @param flag true:异步回调接口在主线程中运行, false:异步回调接口在任务线程中运行<b>默认值为true</b>
         */
        public Builder<T> useMainHandler(boolean flag) {
            async.useMainHandler = flag;
            return this;
        }

        public Async build() {
            return async;
        }

        public void run() {
            async.go();
        }

        public void run(AsyncTaskRunnable task) {
            async.runnable = task;
            async.go();
        }
    }
}
