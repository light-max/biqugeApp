package com.lifengqiang.biquge.ui.bookadd.ranking.child;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseView;
import com.lifengqiang.biquge.base.recycler.OnLoadMoreListener;
import com.lifengqiang.biquge.data.RankingBook;
import com.lifengqiang.biquge.utils.DisplayUtils;

import java.util.List;

public class RankingChildView extends BaseView {
    private RankingBookAdapter adapter;
    private ItemDecoration itemDecoration;
    private RecyclerView recycler;
    private OnLoadMoreListener onLoadMoreListener;
    private OnClickBookListener onClickBookListener;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        adapter = new RankingBookAdapter(base.getContext());
        itemDecoration = new ItemDecoration();
        adapter.setOnItemClickListener((data, position) -> {
            if (onClickBookListener != null) {
                onClickBookListener.onClickBook(data.url);
            }
        });
    }

    @Override
    public void onViewCreated(Base base, Bundle savedInstanceState) {
        recycler = get(R.id.recycler);
        recycler.addItemDecoration(itemDecoration);
        recycler.setAdapter(adapter);
        if (onLoadMoreListener != null) {
            recycler.addOnScrollListener(onLoadMoreListener);
        }
    }

    public void addBooks(List<RankingBook> books) {
        int positionStart = adapter.getItemCount();
        adapter.getData().addAll(books);
        adapter.notifyItemRangeInserted(positionStart, books.size());
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        if (this.recycler != null) {
            this.recycler.addOnScrollListener(onLoadMoreListener);
        }
    }

    public void setOnClickBookListener(OnClickBookListener onClickBookListener) {
        this.onClickBookListener = onClickBookListener;
    }

    class ItemDecoration extends RecyclerView.ItemDecoration {
        private final int dp16 = DisplayUtils.dp2px(base.getContext(), 16);
        private final int dp8 = dp16 / 2;
        private final int dp4 = dp8 / 2;

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int i = parent.getChildAdapterPosition(view);
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
