package com.lifengqiang.biquge.ui.bookadd.recommend;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseView;
import com.lifengqiang.biquge.data.NewestBook;
import com.lifengqiang.biquge.data.PopularBook;
import com.lifengqiang.biquge.data.RecommendBook;

import java.util.List;

public class RecommendView extends BaseView {

    private SearchView searchView;
    private RecyclerView recommendView;
    private RecyclerView popularView;
    private RecyclerView newestView;
    private SwipeRefreshLayout refreshLayout;
    private OnClickBookListener onClickBookListener;

    @Override
    public void onViewCreated(Base base, Bundle savedInstanceState) {
        searchView = get(R.id.search_view);
        recommendView = get(R.id.recommend);
        popularView = get(R.id.popular);
        newestView = get(R.id.newest);
        refreshLayout = get(R.id.swipe);
        recommendView.setNestedScrollingEnabled(false);
        popularView.setNestedScrollingEnabled(false);
        newestView.setNestedScrollingEnabled(false);
        recommendView.addItemDecoration(new Adapter.ItemDecoration(base.getContext()));
        popularView.addItemDecoration(new Adapter.ItemDecoration(base.getContext()));
        newestView.addItemDecoration(new Adapter.ItemDecoration(base.getContext()));
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public void setRecommend(List<RecommendBook> recommendBooks) {
        Point point = new Point();
        base.getActivity().getWindowManager().getDefaultDisplay().getSize(point);
        Adapter.Recommend adapter = new Adapter.Recommend(recommendBooks, Math.min(point.x, point.y));
        adapter.setHeadView(createListHeader("书友推荐阅读"));
        recommendView.setAdapter(adapter);
        adapter.setOnItemClickListener((data, position) -> {
            if (onClickBookListener != null) {
                onClickBookListener.onClickBook(data.url);
            }
        });
    }

    public void setPopular(List<PopularBook> popularBooks) {
        Adapter.Popular adapter = new Adapter.Popular(popularBooks);
        adapter.setHeadView(createListHeader("热门小说"));
        popularView.setAdapter(adapter);
        adapter.setOnItemClickListener((data, position) -> {
            if (onClickBookListener != null) {
                onClickBookListener.onClickBook(data.url);
            }
        });
    }

    public void setNewest(List<NewestBook> newestBooks) {
        Adapter.Newest adapter = new Adapter.Newest(newestBooks);
        adapter.setHeadView(createListHeader("最新小说"));
        newestView.setAdapter(adapter);
        adapter.setOnItemClickListener((data, position) -> {
            if (onClickBookListener != null) {
                onClickBookListener.onClickBook(data.url);
            }
        });
    }

    private View createListHeader(String text) {
        View view = View.inflate(base.getContext(),
                R.layout.view_recommend_book_list_header, null);
        TextView name = view.findViewById(R.id.name);
        name.setText(text);
        return view;
    }

    public void setOnClickBookListener(OnClickBookListener onClickBookListener) {
        this.onClickBookListener = onClickBookListener;
    }

    interface OnClickBookListener {
        void onClickBook(String url);
    }
}
