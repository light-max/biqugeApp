package com.lifengqiang.biquge.ui.search;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseView;
import com.lifengqiang.biquge.data.SearchBook;
import com.lifengqiang.biquge.ui.bookadd.recommend.SearchView;
import com.lifengqiang.biquge.utils.DisplayUtils;

import java.util.List;

public class BookSearchView extends BaseView {
    private SearchView searchView;
    private FrameLayout progressBarLayout;
    private ProgressBar progressBar;
    private RecyclerView recycler;
    private OnClickBookListener onClickBookListener;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        searchView = get(R.id.search_view);
        searchView.setText(base.map("searchKey"));
        progressBarLayout = get(R.id.progress_bar_layout);
        progressBar = get(R.id.progress_bar);
        progressBarLayout.setOnClickListener(v -> {
        });
        recycler = get(R.id.recycler);
        recycler.addItemDecoration(new ItemDecoration(base.getContext()));
    }

    public void setSearchBooks(List<SearchBook> books) {
        SearchBooksAdapter adapter = new SearchBooksAdapter(books);
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener((data, position) -> {
            if (onClickBookListener != null) {
                onClickBookListener.onClickBook(data.url);
            }
        });
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public void showProgress() {
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressBarLayout.setVisibility(View.GONE);
    }

    public void setOnClickBookListener(OnClickBookListener onClickBookListener) {
        this.onClickBookListener = onClickBookListener;
    }

    static class ItemDecoration extends RecyclerView.ItemDecoration {
        private final int dp16;
        private final int dp8;
        private final int dp4;

        public ItemDecoration(Context context) {
            dp16 = DisplayUtils.dp2px(context, 16);
            dp8 = dp16 / 2;
            dp4 = dp8 / 2;
        }

        @Override
        public void getItemOffsets(
                @NonNull Rect outRect,
                @NonNull View view,
                @NonNull RecyclerView parent,
                @NonNull RecyclerView.State state) {
            int i = parent.getChildLayoutPosition(view);
            if (i == 0) {
                outRect.top = dp8;
                outRect.bottom = dp4;
            } else if (i == state.getItemCount() - 1) {
                outRect.top = dp4;
                outRect.bottom = dp8;
            } else {
                outRect.top = dp4;
                outRect.bottom = dp4;
            }
            outRect.left = dp16;
            outRect.right = dp16;
        }
    }

    interface OnClickBookListener {
        void onClickBook(String url);
    }
}
