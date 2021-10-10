package com.lifengqiang.biquge.ui.bookdetails;

import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.recycler.SimpleSingleItemRecyclerAdapter;
import com.lifengqiang.biquge.data.BookDetails;

public class NodesAdapter extends SimpleSingleItemRecyclerAdapter<BookDetails.Node> {
    @Override
    protected int getItemViewLayout() {
        return R.layout.item_book_details_node;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, BookDetails.Node data, int position) {
        holder.setText(R.id.name, data.name);
    }
}
