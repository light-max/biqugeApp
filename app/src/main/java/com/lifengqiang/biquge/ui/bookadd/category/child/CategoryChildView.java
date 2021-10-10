package com.lifengqiang.biquge.ui.bookadd.category.child;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseView;
import com.lifengqiang.biquge.base.recycler.OnLoadMoreListener;
import com.lifengqiang.biquge.data.ClassifiedBook;
import com.lifengqiang.biquge.utils.DisplayUtils;

import java.util.List;

public class CategoryChildView extends BaseView {
    private RecyclerView recycler;
    private ClassifiedBooksAdapter adapter;
    private ItemDecoration itemDecoration;
    private OnLoadMoreListener onLoadMoreListener;
    private OnClickBookListener onClickBookListener;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        adapter = new ClassifiedBooksAdapter(base.getContext());
        itemDecoration = new ItemDecoration();
    }

    @Override
    public void onViewCreated(Base base, Bundle savedInstanceState) {
        recycler = get(R.id.recycler);
        recycler.setAdapter(adapter);
        recycler.addItemDecoration(itemDecoration);
        recycler.addOnScrollListener(onLoadMoreListener);
        adapter.setOnItemClickListener((data, position) -> {
            if (onClickBookListener != null) {
                onClickBookListener.onClickBook(data.url);
            }
        });
    }

    public void addBooks(List<ClassifiedBook> books) {
        int positionStart = adapter.getItemCount();
        adapter.getData().addAll(books);
        adapter.notifyItemRangeInserted(positionStart, books.size());
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        if (recycler != null) {
            recycler.addOnScrollListener(onLoadMoreListener);
        }
    }

    public void setOnClickBookListener(OnClickBookListener onClickBookListener) {
        this.onClickBookListener = onClickBookListener;
    }

    class ItemDecoration extends RecyclerView.ItemDecoration {
        private final int dp8 = DisplayUtils.dp2px(base.getContext(), 8);
        private final int dp4 = dp8 / 2;
        private final int dp2 = dp4 / 2;

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
            outRect.left = dp8;
            outRect.right = dp8;
        }
    }

    interface OnClickBookListener {
        void onClickBook(String url);
    }
}
