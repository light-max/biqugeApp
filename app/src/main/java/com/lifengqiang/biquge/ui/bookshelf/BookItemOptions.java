package com.lifengqiang.biquge.ui.bookshelf;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.lifengqiang.biquge.R;

public class BookItemOptions {
    public static void show(View parent, Runnable details,  Runnable read, Runnable delete) {
        View view = View.inflate(parent.getContext(), R.layout.view_book_options, null);
        PopupWindow window = new PopupWindow(
                view,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        window.setBackgroundDrawable(new ColorDrawable());
        window.setFocusable(true);
        window.showAtLocation(parent, 0, 0, 0);
        view.setOnClickListener(v -> window.dismiss());
        view.findViewById(R.id.details).setOnClickListener(v -> {
            window.dismiss();
            details.run();
        });
        view.findViewById(R.id.read).setOnClickListener(v -> {
            window.dismiss();
            read.run();
        });
        view.findViewById(R.id.delete).setOnClickListener(v -> {
            new AlertDialog.Builder(parent.getContext())
                    .setMessage("你确定要删除这本书?")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("删除", (dialog, which) -> delete.run())
                    .show();
            window.dismiss();
        });
    }
}
