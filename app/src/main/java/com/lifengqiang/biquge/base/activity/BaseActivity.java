package com.lifengqiang.biquge.base.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseModel;
import com.lifengqiang.biquge.base.mvp.BasePresenter;
import com.lifengqiang.biquge.base.mvp.BaseView;
import com.lifengqiang.biquge.base.mvp.MvpObjectGetListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class BaseActivity<Model extends BaseModel, VView extends BaseView, Presenter extends BasePresenter<Model, VView>>
        extends AppCompatActivity implements Base, MvpObjectGetListener<Model, VView, Presenter> {
    protected Handler mainHandler;
    protected Model model;
    protected VView view;
    protected Presenter presenter;
    protected Map<Object, Object> globalVariableMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainHandler = new Handler(Looper.getMainLooper());
        if (createMVP()) {
            model.onCreate(getLifecycle(), this, savedInstanceState);
            view.onCreate(getLifecycle(), this, savedInstanceState);
            presenter.onCreate(getLifecycle(), this, savedInstanceState);
        }
    }

    public void open(Class<?> aClass) {
        startActivity(new Intent(this, aClass));
    }

    public void hideActionBar() {
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    public void hideStatusBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public int immersiveStatusBar() {
        Window window = getWindow();
        android.view.View decorView = window.getDecorView();
//        decorView.setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
//                | android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.setStatusBarColor(Color.TRANSPARENT);
        decorView.setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        int resourceId = getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        } else {
            return 64;
        }
    }

    public void setStatusBar(boolean dark) {
        Window window = getWindow();
        View decorView = window.getDecorView();
        if (dark) {
            window.setStatusBarColor(Color.TRANSPARENT);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            window.setStatusBarColor(Color.TRANSPARENT);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    protected void addStatusBarFillView(int statusBarHeight) {
        android.view.View rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        android.view.View empty = new android.view.View(getContext());
        empty.setLayoutParams(new ViewGroup.LayoutParams(0, statusBarHeight));
        ((ViewGroup) rootView).addView(empty, 0);
    }

    protected void onBeforeDestroy() {
    }

    private boolean createMVP() {
        try {
            ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
            assert superclass != null;
            Type[] types = superclass.getActualTypeArguments();
            Class<?>[] classes = new Class[]{
                    (Class<?>) types[0],
                    (Class<?>) types[1],
                    (Class<?>) types[2],
            };
            Constructor<?>[] constructors = new Constructor[]{
                    classes[0].getConstructor(),
                    classes[1].getConstructor(),
                    classes[2].getConstructor(),
            };
            model = (Model) constructors[0].newInstance();
            view = (VView) constructors[1].newInstance();
            presenter = (Presenter) constructors[2].newInstance();
            return true;
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onBeforeDestroy();
        model = null;
        view = null;
        presenter = null;
        if (globalVariableMap != null) {
            globalVariableMap.clear();
            globalVariableMap = null;
        }
    }

    @Override
    public Map<Object, Object> map() {
        if (globalVariableMap == null) {
            globalVariableMap = new HashMap<>();
        }
        return globalVariableMap;
    }

    @Override
    public Model model() {
        return model;
    }

    @Override
    public VView view() {
        return view;
    }

    @Override
    public Presenter presenter() {
        return presenter;
    }
}
