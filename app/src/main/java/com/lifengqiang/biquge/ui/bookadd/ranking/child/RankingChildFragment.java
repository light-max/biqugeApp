package com.lifengqiang.biquge.ui.bookadd.ranking.child;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.fragment.BaseFragment;

public class RankingChildFragment extends BaseFragment<RankingChildModel, RankingChildView, RankingChildPresenter> {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_ranking_child, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 防止内存重启后没有request对象导致无法发送请求导致没有内容
        outState.putSerializable("request", map("request"));
    }
}
