package com.lifengqiang.biquge.ui.bookshelf;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;

import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseModel;
import com.lifengqiang.biquge.book.BookShelfLoadUtils;
import com.lifengqiang.biquge.book.bean.Book;
import com.lifengqiang.biquge.book.bean.BookShelf;
import com.lifengqiang.biquge.book.parse.bookdetails.BookUpdateParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookShelfModel extends BaseModel {
    private final MutableLiveData<BookShelf> shelf = new MutableLiveData<>();
    private Long lastUpdateTime = null;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        readBooks();
    }

    private void readBooks() {
        BookShelfLoadUtils.readBooks((shelf, s) -> {
            if (shelf != null) {
                getBookShelf().postValue(shelf);
                System.out.println("shelf:" + shelf);
            }
        });
    }

    public void deleteBook(Book book) {
        BookShelfLoadUtils.deleteBook(book, (value, s) -> {
            if (value != null) {
                lastUpdateTime = value;
            }
        });
    }

    public Async.Builder<List<Book>> updateBooks(List<Book> books) {
        return Async.<List<Book>>builder().task(() -> {
            List<Book> newBooks = new ArrayList<>();
            List<Async> tasks = new ArrayList<>();
            for (Book book : books) {
                Async async = new BookUpdateParser(book).update()
                        .useMainHandler(false)
                        .success(data -> {
                            if (data.isUpdate()) {
                                Book newBook = data.getBook();
                                newBooks.add(newBook);
                                book.lastUpdateNode = newBook.lastUpdateNode;
                                book.lastUpdateUrl = newBook.lastUpdateUrl;
                                book.dateTime = newBook.dateTime;
                            }
                        }).build();
                tasks.add(async);
                async.go();
            }
            for (Async task : tasks) {
                task.join();
            }
            BookShelfLoadUtils.saveNewBookShelf(newBooks);
            return newBooks;
        });
    }

    @Override
    public void onStart(Base base, Bundle savedInstanceState) {
        BookShelfLoadUtils.readLastUpdateTime((value, s) -> {
            if (lastUpdateTime == null) {
                lastUpdateTime = value;
            } else if (!Objects.equals(lastUpdateTime, value)) {
                lastUpdateTime = value;
                readBooks();
            }
        });
    }

    public MutableLiveData<BookShelf> getBookShelf() {
        return shelf;
    }
}
