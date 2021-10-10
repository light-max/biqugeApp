package com.lifengqiang.biquge.book;

import android.content.Context;
import android.content.SharedPreferences;

public class ChapterReadRecorder {
    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences("read_recorder", 0);
    }

    public static String getLastRead(Context context, String bookUrl) {
        return getSp(context).getString(bookUrl, null);
    }

    public static void putLastRead(Context context, String bookUrl, String nodeUrl) {
        getSp(context).edit()
                .putString(bookUrl,nodeUrl)
                .apply();
    }
}
