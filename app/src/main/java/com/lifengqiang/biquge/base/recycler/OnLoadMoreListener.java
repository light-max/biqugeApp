package com.lifengqiang.biquge.base.recycler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OnLoadMoreListener extends RecyclerView.OnScrollListener {
    private boolean isLoadMoreIng = false;
    private final Call call;

    public OnLoadMoreListener(Call call) {
        this.call = call;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        if (!isLoadMoreIng) {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (manager != null) {
                int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                int itemCount = manager.getItemCount();
                if (lastItemPosition >= (itemCount - 3) && dy >= 0) {
                    isLoadMoreIng = true;
                    call.onLoadMore(this);
                }
            }
        }
    }

    public void setLoadMoreIng(boolean loadMoreIng) {
        isLoadMoreIng = loadMoreIng;
    }

    public boolean isLoadMoreIng() {
        return isLoadMoreIng;
    }

    public interface Call {
        void onLoadMore(OnLoadMoreListener listener);
    }
}
