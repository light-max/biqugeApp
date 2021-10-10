package com.lifengqiang.biquge.ui.bookadd.recommend;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.recycler.SimpleSingleItemRecyclerAdapter;
import com.lifengqiang.biquge.data.NewestBook;
import com.lifengqiang.biquge.data.PopularBook;
import com.lifengqiang.biquge.data.RecommendBook;
import com.lifengqiang.biquge.net.BiqugeApi;
import com.lifengqiang.biquge.utils.DisplayUtils;

import java.util.List;

interface Adapter {
    class Recommend extends SimpleSingleItemRecyclerAdapter<RecommendBook> {
        private final int coverWidth;
        private final int coverHeight;

        public Recommend(List<RecommendBook> recommendBooks, int activityWidth) {
            super(recommendBooks);
            coverWidth = (int) (activityWidth / 3.6);
            coverHeight = (int) (coverWidth * 1.5);
        }

        @Override
        protected int getItemViewLayout() {
            return R.layout.item_home_recommend_book;
        }

        @Override
        protected void onBindViewHolder(SimpleSingleItemRecyclerAdapter.ViewHolder holder, RecommendBook data, int position) {
            holder.setText(R.id.name, data.name)
                    .setText(R.id.author, "作者：" + data.author)
                    .setText(R.id.last_node, "最新：" + data.lastUpdateNode)
                    .setText(R.id.intro, data.intro);
            ImageView cover = setImageSize(holder.getImage(R.id.cover));
            Glide.with(holder.itemView).load(BiqugeApi.url(data.cover)).into(cover);
        }

        private ImageView setImageSize(ImageView image) {
            ViewGroup.LayoutParams params = image.getLayoutParams();
            params.width = coverWidth;
            params.height = coverHeight;
            image.setLayoutParams(params);
            return image;
        }
    }

    class Popular extends SimpleSingleItemRecyclerAdapter<PopularBook> {
        public Popular(List<PopularBook> popularBooks) {
            super(popularBooks);
        }

        @Override
        protected int getItemViewLayout() {
            return R.layout.item_home_popular_book;
        }

        @Override
        protected void onBindViewHolder(ViewHolder holder, PopularBook data, int position) {
            holder.setText(R.id.name, data.name)
                    .setText(R.id.author, data.author);
        }
    }

    class Newest extends SimpleSingleItemRecyclerAdapter<NewestBook> {
        public Newest(List<NewestBook> data) {
            super(data);
        }

        @Override
        protected int getItemViewLayout() {
            return R.layout.item_home_newest_book;
        }

        @Override
        protected void onBindViewHolder(ViewHolder holder, NewestBook data, int position) {
            holder.setText(R.id.name, data.name)
                    .setText(R.id.date_time, data.dateTime)
                    .setText(R.id.type, data.type)
                    .setText(R.id.author, "作者：" + data.author)
                    .setText(R.id.last_node, "最新：" + data.lastUpdateName);
        }
    }

    class ItemDecoration extends RecyclerView.ItemDecoration {
        private final int dp16;
        private final int dp8;
        private final int dp4;

        public ItemDecoration(Context context) {
            dp16 = DisplayUtils.dp2px(context, 16);
            dp8 = dp16 / 2;
            dp4 = dp8 / 2;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int i = parent.indexOfChild(view);
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
}
