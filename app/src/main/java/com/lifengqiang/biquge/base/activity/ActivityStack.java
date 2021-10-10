package com.lifengqiang.biquge.base.activity;

import android.app.Activity;

import java.util.Stack;

/**
 * 活动栈管理器
 *
 * @author lifengqiang
 */
public class ActivityStack {
    private static final ActivityStack instance = new ActivityStack();

    private Stack<Activity> activities = new Stack<>();

    public static void push(Activity activity){
        instance.activities.push(activity);
    }

    public static void pop(){
        instance.activities.pop();
    }

    /**
     * 跳转到此activity，将此activity置于栈顶，不生成新的实例
     */
    public static void jumpTo(Class<? extends Activity> aClass) {

    }

    /**
     * 打开新的activity，将此activity置于栈顶
     */
    public static void open(Class<? extends Activity> aClass) {

    }

    /**
     * 将栈顶的activity切换为此activity，并且将原先的栈顶activity移除
     */
    public static void replace(Class<? extends Activity> aClass) {

    }
}
