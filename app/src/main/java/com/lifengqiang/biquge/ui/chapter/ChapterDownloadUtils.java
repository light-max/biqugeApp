package com.lifengqiang.biquge.ui.chapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.async.AsyncTaskError;
import com.lifengqiang.biquge.async.Call;
import com.lifengqiang.biquge.book.BookFileManager;
import com.lifengqiang.biquge.data.BookDetails;
import com.lifengqiang.biquge.net.BiqugeApi;
import com.lifengqiang.biquge.net.result.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChapterDownloadUtils {
    private static ChapterDownloadUtils instance;
    private final Map<String, Async> asyncMap;
    private final Map<String, BatchDownloadThread> threadMap;

    private ChapterDownloadUtils() {
        asyncMap = new HashMap<>();
        threadMap = new HashMap<>();
    }

    public static ChapterDownloadUtils getInstance() {
        if (instance == null) {
            instance = new ChapterDownloadUtils();
        }
        return instance;
    }

    public void download(BookDetails.Node node, Call.OnSuccess success) {
        File file = BookFileManager.getBookChapterFile(node.url);
        if (file.exists()) {
            return;
        }
        synchronized (ChapterDownloadUtils.this) {
            if (asyncMap.containsKey(node.url)) {
                return;
            }
        }
        Async async = Async.builder()
                .success(success)
                .after(() -> {
                    synchronized (ChapterDownloadUtils.this) {
                        asyncMap.remove(node.url);
                    }
                })
                .task(() -> {
                    Result result = BiqugeApi.chapter(node.url).execute();
                    if (result.error() != null) {
                        return result.makeAsyncResultError();
                    } else {
                        byte[] bytes = result.bytes();
                        try {
                            OutputStream out = new FileOutputStream(file);
                            out.write(bytes);
                            out.close();
                        } catch (IOException e) {
                            return new AsyncTaskError("下载失败", e);
                        }
                    }
                    return null;
                }).build();
        async.go();
        synchronized (ChapterDownloadUtils.this) {
            asyncMap.put(node.url, async);
        }
    }

    public void download(Context context, String bookUrl, List<BookDetails.Node> nodes, Runnable finishRunnable) {
        BatchDownloadThread thread;
        synchronized (ChapterDownloadUtils.this) {
            thread = threadMap.get(bookUrl);
        }
        if (thread == null) {
            thread = new BatchDownloadThread(nodes);
            thread.start();
            synchronized (ChapterDownloadUtils.this) {
                threadMap.put(bookUrl, thread);
            }
        }
        Handler handler = new Handler(Looper.getMainLooper());
        BatchDownloadDialog dialog = new BatchDownloadDialog(context);
        dialog.getDialog().show();
        thread.setOnDownloadListener(new OnDownloadListener() {
            @Override
            public void start(int max) {
                handler.post(() -> dialog.getProgressBar().setMax(max));
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void progress(int max, int progress) {
                handler.post(() -> {
                    dialog.getSchedule().setText(String.format("%d/%d", progress, max));
                    dialog.getProgressBar().setProgress(progress);
                });
            }

            @Override
            public void error(String message, Exception e) {
                handler.post(() -> {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    dialog.getDialog().dismiss();
                    synchronized (ChapterDownloadUtils.this) {
                        threadMap.remove(bookUrl);
                    }
                    finishRunnable.run();
                });
                e.printStackTrace();
            }

            @Override
            public void finish() {
                handler.post(() -> {
                    Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
                    dialog.getDialog().dismiss();
                    synchronized (ChapterDownloadUtils.this) {
                        threadMap.remove(bookUrl);
                    }
                    finishRunnable.run();
                });
            }
        });
        dialog.getQuit().setOnClickListener(v -> {
            synchronized (ChapterDownloadUtils.this) {
                threadMap.get(bookUrl).quit = true;
            }
        });
        dialog.getHide().setOnClickListener(v -> {
            dialog.getDialog().dismiss();
        });
    }

    static class BatchDownloadThread extends Thread {
        private int max;
        private int progress;
        private List<String> urls;
        private List<BookDetails.Node> nodes;
        private OnDownloadListener listener;

        private boolean quit = false;

        public BatchDownloadThread(List<BookDetails.Node> nodes) {
            this.nodes = nodes;
        }

        @Override
        public void run() {
            urls = new ArrayList<>();
            for (BookDetails.Node node : nodes) {
                File file = BookFileManager.getBookChapterFile(node.url);
                if (!file.exists()) {
                    urls.add(node.url);
                }
            }
            nodes = null;
            max = urls.size();
            if (listener != null) {
                listener.start(max);
            }
            try {
                for (String url : urls) {
                    if (quit) {
                        break;
                    }
                    Result result = BiqugeApi.chapter(url).execute();
                    if (result.error() != null) {
                        throw result.makeAsyncResultError().getException();
                    } else {
                        File file = BookFileManager.getBookChapterFile(url);
                        byte[] bytes = result.bytes();
                        OutputStream out = new FileOutputStream(file);
                        out.write(bytes);
                        out.close();
                        if (listener != null) {
                            listener.progress(max, ++progress);
                        }
                    }
                }
                if (listener != null) {
                    listener.finish();
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.error("缓存失败", e);
                }
            }
        }

        public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
            this.listener = onDownloadListener;
            if (max != 0) {
                this.listener.start(max);
            }
        }
    }

    static class BatchDownloadDialog {
        private AlertDialog dialog;
        private TextView schedule;
        private ProgressBar progressBar;
        private TextView quit;
        private TextView hide;

        public BatchDownloadDialog(Context context) {
            View view = View.inflate(context, R.layout.view_batch_download_dialog, null);
            dialog = new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setView(view)
                    .show();
            schedule = view.findViewById(R.id.schedule);
            progressBar = view.findViewById(R.id.progress_bar);
            quit = view.findViewById(R.id.quit);
            hide = view.findViewById(R.id.hide);
        }

        public AlertDialog getDialog() {
            return dialog;
        }

        public TextView getSchedule() {
            return schedule;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public TextView getQuit() {
            return quit;
        }

        public TextView getHide() {
            return hide;
        }
    }

    interface OnDownloadListener {
        void start(int max);

        void progress(int max, int progress);

        void error(String message, Exception e);

        void finish();
    }
}
