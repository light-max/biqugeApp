package com.lifengqiang.biquge.book.parse.bookdetails;

import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.async.AsyncTaskError;
import com.lifengqiang.biquge.book.BookFileManager;
import com.lifengqiang.biquge.book.LocalBookLocks;
import com.lifengqiang.biquge.data.BookDetails;
import com.lifengqiang.biquge.net.BiqugeApi;
import com.lifengqiang.biquge.net.result.Result;

import org.jsoup.nodes.Document;

import java.io.File;

public class LocalBookDetailsParser extends BookDetailsParser {
    public LocalBookDetailsParser(String url) {
        super(url);
    }

    @Override
    public Async.Builder<BookDetails> generateTask() {
        return Async.<BookDetails>builder().task(() -> {
            File file = BookFileManager.getBookFile(url);
            Object lock = LocalBookLocks.getInstance().getLock(file);
            if (!file.exists()) {
                Result result = BiqugeApi.book(url).execute();
                if (result.error() != null) {
                    return result.makeAsyncResultError();
                }
                Document document = result.document();
                synchronized (lock) {
                    documentWriteFile(file, document);
                }
            }
            Document document;
            synchronized (lock) {
                document = readDocument(file);
            }
            // 解析书籍出错时删除本地文件，这也算是一种修复
            try {
                return parserBookDetails(document);
            } catch (Exception e) {
                file.delete();
                return new AsyncTaskError("已尝试自动修复书籍，请重新打开", e);
            }
        });
    }
}
