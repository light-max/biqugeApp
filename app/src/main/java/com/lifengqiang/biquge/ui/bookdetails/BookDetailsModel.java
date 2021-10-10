package com.lifengqiang.biquge.ui.bookdetails;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;

import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseModel;
import com.lifengqiang.biquge.data.BookDetails;
import com.lifengqiang.biquge.book.parse.bookdetails.BookDetailsParser;

import java.lang.reflect.Constructor;

public class BookDetailsModel extends BaseModel {
    private BookDetailsParser parser;
    private MutableLiveData<BookDetails> book;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        book = new MutableLiveData<>();
        if (makeParser()) {
            parser.setSaveDocument(true);
            parser.generateTask().success(data -> {
                book.postValue(data);
            }).run();
        }
    }

    private boolean makeParser() {
        String url = base.map("url");
        if (url == null) {
            base.toast("没有url");
            return false;
        }
        Class<?> parserClass = base.map("parser");
        if (parserClass == null) {
            base.toast("没有解析器");
            return false;
        }
        try {
            Constructor<?> constructor = parserClass.getConstructor(String.class);
            parser = (BookDetailsParser) constructor.newInstance(url);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            base.toast("解析器初始化失败");
            return false;
        }
    }

    public MutableLiveData<BookDetails> getBook() {
        return book;
    }

    public BookDetailsParser getParser() {
        return parser;
    }
}
