package com.lifengqiang.biquge.ui.bookshelf;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BasePresenter;
import com.lifengqiang.biquge.book.bean.Book;
import com.lifengqiang.biquge.book.parse.bookdetails.LocalBookDetailsParser;
import com.lifengqiang.biquge.ui.bookdetails.BookDetailsActivity;
import com.lifengqiang.biquge.ui.content.ContentActivity;

import java.util.HashSet;
import java.util.Set;

public class BookShelfPresenter extends BasePresenter<BookShelfModel, BookShelfView> {
    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        model.getBookShelf().observe(base, shelf -> {
            view.setBooks(shelf);
        });
        view.setOnActionListener(new BookShelfView.OnActionListener() {
            @Override
            public void openDetails(Book book) {
                BookDetailsActivity.startForUrl(base.getContext(), book.url, LocalBookDetailsParser.class);
            }

            @Override
            public void openRead(Book book) {
                ContentActivity.startForUrl(base.getContext(), book.url, null);
            }

            @Override
            public void delete(Book book, int position) {
                model.deleteBook(book);
                BooksAdapter adapter = view.getAdapter();
                adapter.getData().remove(position);
                adapter.notifyItemRemoved(position);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    adapter.notifyDataSetChanged();
                }, 500);
            }
        });
        view.getRefreshLayout().setOnRefreshListener(() -> {
            BooksAdapter adapter = view.getAdapter();
            model.updateBooks(adapter.getData()).success(data -> {
                Set<String> set = new HashSet<>();
                for (Book book : data) {
                    set.add(book.url);
                }
                adapter.setNewBooks(set);
                adapter.notifyDataSetChanged();
            }).before(() -> {
                view.getRefreshLayout().setRefreshing(true);
            }).after(() -> {
                view.getRefreshLayout().setRefreshing(false);
            }).run();
        });
    }
}
