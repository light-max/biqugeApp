package com.lifengqiang.biquge.ui.chapter;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lifengqiang.biquge.R;

public class ChapterMenuOptions {
    public static void showMenu(
            View parent, int backgroundColor, int textColor,
            Runnable cacheRunnable, Runnable ascRunnable, Runnable descRunnable, Runnable lastReadRunnable) {
        View view = View.inflate(parent.getContext(), R.layout.view_chapter_menu, null);
        PopupWindow window = new PopupWindow(
                view,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        window.setBackgroundDrawable(new ColorDrawable());
        window.setFocusable(true);
        window.showAtLocation(parent, 0, 0, 0);
        view.setOnClickListener(v -> window.dismiss());
        view.findViewById(R.id.bg).setBackgroundColor(backgroundColor);
        TextView cache = view.findViewById(R.id.cache);
        TextView asc = view.findViewById(R.id.asc);
        TextView desc = view.findViewById(R.id.desc);
        TextView lastRead = view.findViewById(R.id.last_read);
        TextView cancel = view.findViewById(R.id.cancel);
        cache.setTextColor(textColor);
        asc.setTextColor(textColor);
        desc.setTextColor(textColor);
        lastRead.setTextColor(textColor);
        cancel.setTextColor(textColor);
        cache.setOnClickListener(v -> {
            window.dismiss();
            cacheRunnable.run();
        });
        asc.setOnClickListener(v -> {
            window.dismiss();
            ascRunnable.run();
        });
        desc.setOnClickListener(v -> {
            window.dismiss();
            descRunnable.run();
        });
        lastRead.setOnClickListener(v -> {
            window.dismiss();
            lastReadRunnable.run();
        });
    }
}
