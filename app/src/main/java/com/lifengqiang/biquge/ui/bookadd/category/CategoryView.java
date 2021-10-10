package com.lifengqiang.biquge.ui.bookadd.category;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseView;
import com.lifengqiang.biquge.data.BookClassification;
import com.lifengqiang.biquge.ui.bookadd.category.child.CategoryChildFragment;
import com.lifengqiang.biquge.utils.CategoryVerticalLayout;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryView extends BaseView implements CategoryVerticalLayout.OnSelectedListener {
    private SwipeRefreshLayout refreshLayout;
    private CategoryVerticalLayout category;
    private TextView title;
    private Map<String, CategoryChildFragment> fragments;
    private CategoryChildFragment currentFragment;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        fragments = new HashMap<>();
    }

    @Override
    public void onViewCreated(Base base, Bundle savedInstanceState) {
        refreshLayout = get(R.id.swipe);
        category = get(R.id.category);
        title = get(R.id.title);
        category.setOnSelectedListener(this);
    }

    public void setClassification(List<BookClassification> classification) {
        category.setBookClassifications(classification);
        category.setCurrentSelectItem(0);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSelected(BookClassification c, int position) {
        title.setText(c.name + "小说");
        FragmentManager manager = base.getFragment().getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        CategoryChildFragment fragment = fragments.get(c.name);
        if (fragment == null) {
            fragment = new CategoryChildFragment();
            fragment.map("url", c.url);
            fragments.put(c.name, fragment);
        }
        if (fragment != currentFragment) {
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            if (fragment.isAdded()) {
                transaction.show(fragment);
            } else {
                transaction.add(R.id.container, fragment);
            }
            currentFragment = fragment;
            transaction.commit();
        }
    }

    public void clearFragments() {
        title.setText("");
        currentFragment = null;
        category.setBookClassifications(Collections.emptyList());
        FragmentTransaction transaction = base.getFragment()
                .getChildFragmentManager()
                .beginTransaction();
        for (CategoryChildFragment value : fragments.values()) {
            if (value != null && value.isAdded()) {
                transaction.remove(value);
            }
        }
        transaction.commit();
        fragments.clear();
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }
}
