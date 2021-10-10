package com.lifengqiang.biquge.ui.chapter;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;

import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseModel;
import com.lifengqiang.biquge.book.parse.bookdetails.LocalBookDetailsParser;
import com.lifengqiang.biquge.data.BookDetails;

public class ChapterModel extends BaseModel {
    private LocalBookDetailsParser parser;
    private final MutableLiveData<BookDetails> book = new MutableLiveData<>();

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        parser = new LocalBookDetailsParser(base.map("bookUrl"));
        getParser().generateTask()
                .error((message, e) -> base.toast(message))
                .success(data -> getBook().postValue(data))
                .run();
    }

    public LocalBookDetailsParser getParser() {
        return parser;
    }

    public MutableLiveData<BookDetails> getBook() {
        return book;
    }
}
