package com.lifengqiang.biquge.ui.content;

import com.lifengqiang.biquge.base.mvp.BaseModel;
import com.lifengqiang.biquge.book.parse.content.ChapterContentParser;

public class ContentModel extends BaseModel {
    private ChapterContentParser parser;

    public boolean makeParser() {
        if (parser == null) {
            String bookUrl = base.map("bookUrl");
            String nodeUrl = base.map("nodeUrl");
            if (bookUrl == null) {
                base.toast("没有url");
                return false;
            } else {
                parser = new ChapterContentParser(nodeUrl, bookUrl);
                return true;
            }
        } else {
            return true;
        }
    }

    public ChapterContentParser getParser() {
        return parser;
    }
}
