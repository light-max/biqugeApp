package com.lifengqiang.biquge.ui.bookdetails;

import android.os.Bundle;

import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BasePresenter;
import com.lifengqiang.biquge.book.BookFileManager;
import com.lifengqiang.biquge.book.BookShelfLoadUtils;
import com.lifengqiang.biquge.book.LocalBookLocks;
import com.lifengqiang.biquge.data.BookDetails;
import com.lifengqiang.biquge.ui.content.ContentActivity;

import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class BookDetailsPresenter extends BasePresenter<BookDetailsModel, BookDetailsView> {
    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        model.getBook().observe(base, book -> {
            view.setBookDetails(book);
        });
        view.getAddButton().setOnClickListener(v -> {
            BookDetails bookDetails = model.getBook().getValue();
            if (bookDetails != null) {
                BookShelfLoadUtils.addBook(bookDetails, (value, e) -> {
                    if (e != null) {
                        base.toast(e);
                    } else {
                        base.toast("添加成功");
                        new Thread(() -> {
                            try {
                                File file = BookFileManager.getBookFile(bookDetails.url);
                                synchronized (LocalBookLocks.getInstance().getLock(file)) {
                                    OutputStream out = new FileOutputStream(file);
                                    Document document = model.getParser().getDocument();
                                    String outerHtml = document.outerHtml();
                                    out.write(outerHtml.getBytes());
                                    out.close();
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }).start();
                    }
                });
            } else {
                base.toast("无法添加到书架");
            }
        });
        view.setOpenNodeListener(node -> {
            ContentActivity.startForUrl(base.getContext(), base.map("url"), node.url);
        });
    }
}
