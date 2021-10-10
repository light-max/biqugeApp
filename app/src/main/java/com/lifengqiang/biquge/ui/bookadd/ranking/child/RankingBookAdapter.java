package com.lifengqiang.biquge.ui.bookadd.ranking.child;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.recycler.SimpleSingleItemRecyclerAdapter;
import com.lifengqiang.biquge.data.RankingBook;
import com.lifengqiang.biquge.net.BiqugeApi;
import com.lifengqiang.biquge.utils.DisplayUtils;

public class RankingBookAdapter extends SimpleSingleItemRecyclerAdapter<RankingBook> {
    private final int coverWidth;
    private final int coverHeight;

    public RankingBookAdapter(Context context){
        coverWidth = DisplayUtils.dp2px(context,72);
        coverHeight = (int) (coverWidth * 1.5);
    }

    @Override
    protected int getItemViewLayout() {
        return R.layout.item_home_ranking_book;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, RankingBook data, int position) {
        holder.setText(R.id.name, data.name)
                .setText(R.id.author, "作者：" + data.author)
                .setText(R.id.status, data.status)
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
