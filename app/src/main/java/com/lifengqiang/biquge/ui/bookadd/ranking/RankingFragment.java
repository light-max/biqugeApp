package com.lifengqiang.biquge.ui.bookadd.ranking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.fragment.BaseFragment;

public class RankingFragment extends BaseFragment<RankingModel, RankingView, RankingPresenter> {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_ranking, container, false);
    }
}
