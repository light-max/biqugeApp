package com.lifengqiang.biquge.book;

import android.content.Context;
import android.content.SharedPreferences;

public class ReadAnimationSetting {
    public static final int NONE = 0;
    public static final int HORIZONTAL = 1;
//    public static final int VERTICAL = 2;

    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences("read_animation", 0);
    }

    public static void set(Context context, int mode) {
        if (mode == NONE || mode == HORIZONTAL) {
            getSp(context).edit()
                    .putInt("mode", mode)
                    .apply();
        } else {
            throw new RuntimeException();
        }
    }

    public static int get(Context context) {
        return getSp(context).getInt("mode", HORIZONTAL);
    }

    public static boolean isScroll(Context context) {
        return get(context) != NONE;
    }
}
