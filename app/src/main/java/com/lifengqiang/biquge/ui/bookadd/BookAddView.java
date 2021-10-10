package com.lifengqiang.biquge.ui.bookadd;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseView;
import com.lifengqiang.biquge.ui.bookadd.category.CategoryFragment;
import com.lifengqiang.biquge.ui.bookadd.ranking.RankingFragment;
import com.lifengqiang.biquge.ui.bookadd.recommend.RecommendFragment;
import com.lifengqiang.biquge.utils.NoScrollViewPager;

public class BookAddView extends BaseView {
    private RecommendFragment recommendFragment;
    private RankingFragment rankingFragment;
    private CategoryFragment categoryFragment;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        FragmentManager manager = base.getActivity().getSupportFragmentManager();
        recommendFragment = new RecommendFragment();
        rankingFragment = new RankingFragment();
        categoryFragment = new CategoryFragment();
        NoScrollViewPager pager = base.get(R.id.pager);
        pager.setOffscreenPageLimit(3);
        pager.setScroll(true);
        pager.setAdapter(new FragmentPagerAdapter(manager, 1) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return recommendFragment;
                    case 1:
                        return rankingFragment;
                    default:
                        return categoryFragment;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        BottomNavigationView nav = base.get(R.id.nav);
        nav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.recommend:
                    pager.setCurrentItem(0);
                    break;
                case R.id.ranking:
                    pager.setCurrentItem(1);
                    break;
                case R.id.category:
                    pager.setCurrentItem(2);
                    break;
            }
            return true;
        });
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        nav.setSelectedItemId(R.id.recommend);
                        break;
                    case 1:
                        nav.setSelectedItemId(R.id.ranking);
                        break;
                    case 2:
                        nav.setSelectedItemId(R.id.category);
                        break;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recommendFragment = null;
        rankingFragment = null;
        categoryFragment = null;
    }
}
