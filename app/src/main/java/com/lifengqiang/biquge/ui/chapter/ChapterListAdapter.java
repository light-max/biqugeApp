package com.lifengqiang.biquge.ui.chapter;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.recycler.SimpleSingleItemRecyclerAdapter;
import com.lifengqiang.biquge.book.BookFileManager;
import com.lifengqiang.biquge.data.BookDetails;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ChapterListAdapter extends SimpleSingleItemRecyclerAdapter<BookDetails.Node> {
    private final int textColor;
    private final Map<String, Boolean> map = new HashMap<>();
    private OnCacheListener onCacheListener;
    private boolean desc = false;

    public ChapterListAdapter(int textColor) {
        this.textColor = textColor;
    }

    @Override
    protected int getItemViewLayout() {
        return R.layout.item_chapter;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (desc) {
            super.onBindViewHolder(holder, getItemCount() - position - 1);
        } else {
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, BookDetails.Node data, int position) {
        TextView name = holder.getText(R.id.name);
        name.setText(data.name);
        name.setTextColor(textColor);
        TextView cache = holder.getText(R.id.cache);
        cache.setTextColor(textColor);
        Boolean isCache = map.get(data.url);
        if (isCache == null) {
            File file = BookFileManager.getBookChapterFile(data.url);
            isCache = file.exists();
            map.put(data.url, isCache);
        }
        cache.setText(isCache ? "已缓存" : "缓存");
        if (!isCache) {
            cache.setOnClickListener(v -> {
                if (onCacheListener != null) {
                    onCacheListener.onCache(data, position);
                }
            });
        }
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    public Map<String, Boolean> getMap() {
        return map;
    }

    public interface OnCacheListener {
        void onCache(BookDetails.Node node, int position);
    }

    public void setOnCacheListener(OnCacheListener onCacheListener) {
        this.onCacheListener = onCacheListener;
    }
}
