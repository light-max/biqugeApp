package com.lifengqiang.biquge.ui.bookadd.ranking;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseView;
import com.lifengqiang.biquge.net.BiqugeApi;
import com.lifengqiang.biquge.ui.bookadd.ranking.child.RankingChildFragment;

public class RankingView extends BaseView {
    private SwipeRefreshLayout refreshLayout;
    private RankingChildFragment[] fragments;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        fragments = new RankingChildFragment[]{
                new RankingChildFragment(),
                new RankingChildFragment(),
                new RankingChildFragment(),
        };
        fragments[0].map("request", BiqugeApi.rankingMonth());
        fragments[1].map("request", BiqugeApi.rankingWeek());
        fragments[2].map("request", BiqugeApi.rankingAll());
    }

    @Override
    public void onViewCreated(Base base, Bundle savedInstanceState) {
        refreshLayout = get(R.id.swipe);
        ViewPager pager = get(R.id.pager);
        pager.setAdapter(new FragmentPagerAdapter(base.getFragment().getChildFragmentManager(),
                FragmentPagerAdapter.POSITION_NONE) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments == null ? 0 : fragments.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return new String[]{
                        "月点击",
                        "周点击",
                        "总点击",
                }[position];
            }
        });
        TabLayout tab = get(R.id.tab);
        tab.setupWithViewPager(pager);

        getRefreshLayout().setOnRefreshListener(() -> {
            for (RankingChildFragment fragment : fragments) {
                if (fragment.presenter() != null) {
                    fragment.presenter().refresh();
                }
            }
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                getRefreshLayout().setRefreshing(false);
            }, 500);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragments = null;
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }
}
