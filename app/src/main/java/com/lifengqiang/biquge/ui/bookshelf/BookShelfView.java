package com.lifengqiang.biquge.ui.bookshelf;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseView;
import com.lifengqiang.biquge.book.bean.Book;
import com.lifengqiang.biquge.book.bean.BookShelf;
import com.lifengqiang.biquge.ui.bookadd.BookAddActivity;
import com.lifengqiang.biquge.ui.setting.SettingActivity;
import com.lifengqiang.biquge.utils.DisplayUtils;

public class BookShelfView extends BaseView {
    private BooksAdapter adapter;
    private OnActionListener onActionListener;
    private RecyclerView recycler;
    private SwipeRefreshLayout refreshLayout;
    private int dp16;
    private int dp8;
    private int dp4;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        dp16 = DisplayUtils.dp2px(base.getContext(), 16);
        dp8 = DisplayUtils.dp2px(base.getContext(), 8);
        dp4 = DisplayUtils.dp2px(base.getContext(), 4);
        recycler = get(R.id.recycler);
        refreshLayout = get(R.id.swipe);
        setRecyclerViewLayoutManger();
        adapter = new BooksAdapter((int) ((getWindowWidth() - dp16 * 2 - dp4 * 2) / 2f));
        recycler.setAdapter(adapter);
        adapter.setOnItemLongClickListener((data, position) -> {
            if (onActionListener != null) {
                BookItemOptions.show(recycler,
                        () -> onActionListener.openDetails(data),
                        () -> onActionListener.openRead(data),
                        () -> onActionListener.delete(data, position)
                );
            }
        });
        adapter.setOnItemClickListener((data, position) -> {
            if (onActionListener != null) {
                onActionListener.openRead(data);
            }
        });
        click(R.id.add, () -> {
            ((BookShelfActivity) base.getActivity()).open(BookAddActivity.class);
        });
        click(R.id.setting, () -> {
            ((BookShelfActivity) base.getActivity()).open(SettingActivity.class);
        });
    }

    private void setRecyclerViewLayoutManger() {
        GridLayoutManager layoutManager = new GridLayoutManager(base.getContext(), 2);
        recycler.setLayoutManager(layoutManager);
        recycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int i = parent.getChildAdapterPosition(view);
                if (i % 2 == 0) {
                    outRect.left = dp16;
                    outRect.right = dp4;
                } else {
                    outRect.left = dp4;
                    outRect.right = dp16;
                }
                if (i / 2 == 0) {
                    outRect.top = dp8;
                } else {
                    outRect.top = dp4;
                }
                outRect.bottom = dp4;
            }
        });
    }

    private int getWindowWidth() {
        Point point = new Point();
        base.getActivity().getWindowManager()
                .getDefaultDisplay()
                .getSize(point);
        return point.x;
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public void setBooks(BookShelf shelf) {
        adapter.setData(shelf.getBooks());
        adapter.notifyDataSetChanged();
    }

    public BooksAdapter getAdapter() {
        return adapter;
    }

    public interface OnActionListener {
        void openDetails(Book book);

        void openRead(Book book);

        void delete(Book book, int position);
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }
}
