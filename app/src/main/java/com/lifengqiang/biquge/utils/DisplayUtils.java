package com.lifengqiang.biquge.utils;

import android.content.Context;

public class DisplayUtils {
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

//    public static Size getScreenSize(Context context) {
//        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//        if (metrics.widthPixels > metrics.heightPixels) {
//            return new Size(metrics.heightPixels, metrics.widthPixels);
//        } else {
//            return new Size(metrics.widthPixels, metrics.heightPixels);
//        }
//    }

    public static int dp2px(Context context, float dpValue) {
        return dp2px(dpValue, getDensity(context));
    }

    public static int dp2px(float dpValue, float density) {
        return (int) (dpValue * density + 0.5f);
    }
}
