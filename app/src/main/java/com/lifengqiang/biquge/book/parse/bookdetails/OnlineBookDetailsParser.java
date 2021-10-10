package com.lifengqiang.biquge.book.parse.bookdetails;

import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.book.BookFileManager;
import com.lifengqiang.biquge.book.LocalBookLocks;
import com.lifengqiang.biquge.data.BookDetails;
import com.lifengqiang.biquge.net.BiqugeApi;
import com.lifengqiang.biquge.net.result.Result;

import java.io.File;

public class OnlineBookDetailsParser extends BookDetailsParser {
    public OnlineBookDetailsParser(String url) {
        super(url);
    }

    @Override
    public Async.Builder<BookDetails> generateTask() {
        return Async.<BookDetails>builder()
                .task(() -> {
                    Result result = BiqugeApi.book(url).execute();
                    if (result.error() != null) {
                        return result.makeAsyncResultError();
                    }
//                    return parserBookDetails(result.document());
                    // 在线书籍也做一个缓存，防止阅读章节时读取不到书籍信息
                    BookDetails book = parserBookDetails(result.document());
                    File file = BookFileManager.getBookFile(url);
                    synchronized (LocalBookLocks.getInstance().getLock(file)) {
                        documentWriteFile(file, getDocument());
                    }
                    return book;
                });
    }


}
