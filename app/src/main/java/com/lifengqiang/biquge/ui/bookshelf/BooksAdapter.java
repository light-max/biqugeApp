package com.lifengqiang.biquge.ui.bookshelf;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lifengqiang.biquge.R;
import com.lifengqiang.biquge.base.recycler.SimpleSingleItemRecyclerAdapter;
import com.lifengqiang.biquge.book.bean.Book;
import com.lifengqiang.biquge.net.BiqugeApi;

import java.util.Set;

public class BooksAdapter extends SimpleSingleItemRecyclerAdapter<Book> {
    private final int imageWidth;
    private final int imageHeight;
    private Set<String> newBooks;

    public BooksAdapter(int imageWidth) {
        super();
        this.imageWidth = imageWidth;
        this.imageHeight = (int) (imageWidth * 1.4f);
    }

    @Override
    protected int getItemViewLayout() {
        return R.layout.item_book_shelf_book;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, Book data, int position) {
        Glide.with(holder.itemView)
                .load(BiqugeApi.url(data.cover))
                .into(setImageLayoutParams(holder.getImage(R.id.cover)));
        holder.setText(R.id.name, data.name);
        holder.getImage(R.id.icon_new).setVisibility(isNewBook(data.url) ? View.VISIBLE : View.GONE);
    }

    private ImageView setImageLayoutParams(ImageView imageView) {
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = imageWidth;
        params.height = imageHeight;
        imageView.setLayoutParams(params);
        return imageView;
    }

    public void setNewBooks(Set<String> set) {
        this.newBooks = set;
    }

    public boolean isNewBook(String bookUrl) {
        if (newBooks == null) {
            return false;
        } else {
            return newBooks.contains(bookUrl);
        }
    }
}
