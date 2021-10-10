package com.lifengqiang.biquge.base.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class BaseFragment<Model extends BaseModel, View extends BaseView, Presenter extends BasePresenter<Model, View>>
        extends Fragment implements Base, MvpObjectGetListener<Model, View, Presenter> {
    protected Handler mainHandler;
    protected Model model;
    protected View view;
    protected Presenter presenter;
    protected Map<Object, Object> globalVariableMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainHandler = new Handler(Looper.getMainLooper());
        if (createMVP()) {
            model.onCreate(getLifecycle(), this, savedInstanceState);
            view.onCreate(getLifecycle(), this, savedInstanceState);
            presenter.onCreate(getLifecycle(), this, savedInstanceState);
        }
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
            view = (View) constructors[1].newInstance();
            presenter = (Presenter) constructors[2].newInstance();
            return true;
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.view != null) {
            this.view.onViewCreated(this, savedInstanceState);
        }
        if (this.model != null) {
            this.model.onViewCreated(this, savedInstanceState);
        }
        if (this.presenter != null) {
            this.presenter.onViewCreated(this, savedInstanceState);
        }
    }

    protected void onBeforeDestroy() {
    }

    @Override
    public void onDestroy() {
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
    public View view() {
        return view;
    }

    @Override
    public Presenter presenter() {
        return presenter;
    }
}
