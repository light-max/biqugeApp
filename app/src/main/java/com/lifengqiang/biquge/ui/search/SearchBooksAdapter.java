package com.lifengqiang.biquge.ui.search;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.recycler.SimpleSingleItemRecyclerAdapter;
import com.lifengqiang.biquge.data.SearchBook;

import java.util.List;

public class SearchBooksAdapter extends SimpleSingleItemRecyclerAdapter<SearchBook> {
    public SearchBooksAdapter(List<SearchBook> data) {
        super(data);
    }

    @Override
    protected int getItemViewLayout() {
        return R.layout.item_search_book;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, SearchBook data, int position) {
        holder.setText(R.id.name, data.name)
                .setText(R.id.status, data.status)
                .setText(R.id.author, "作者：" + data.author)
                .setText(R.id.text_count, "字数：" + data.textCount)
                .setText(R.id.last_node, "最新：" + data.lastUpdateNode)
                .setText(R.id.date_time, data.dateTime);
    }
}
