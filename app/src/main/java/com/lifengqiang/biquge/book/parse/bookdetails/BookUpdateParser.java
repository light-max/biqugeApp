package com.lifengqiang.biquge.book.parse.bookdetails;

import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.book.BookFileManager;
import com.lifengqiang.biquge.book.LocalBookLocks;
import com.lifengqiang.biquge.book.bean.Book;
import com.lifengqiang.biquge.data.BookDetails;
import com.lifengqiang.biquge.net.BiqugeApi;
import com.lifengqiang.biquge.net.result.Result;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class BookUpdateParser extends BookDetailsParser {
    private final UpdateData updateData = new UpdateData();

    public BookUpdateParser(Book book) {
        super(book.url);
        updateData.book = book;
    }

    @Deprecated
    @Override
    public Async.Builder<BookDetails> generateTask() {
        return Async.<BookDetails>builder().task(() -> {
            Result result = BiqugeApi.book(url).execute();
            if (result.error() != null) {
                result.makeAsyncResultError().print();
            } else {
                updateData.document = result.document();
            }
            return parserBookDetails(updateData.document);
        });
    }

    public Async.Builder<UpdateData> update() {
        return Async.<UpdateData>builder().task(() -> {
//            Async task = generateTask().useMainHandler(false).success(data -> {
//                updateData.update = !Objects.equals(
//                        data.lastUpdateNode,
//                        updateData.book.lastUpdateNode
//                );
//                if (updateData.update) {
//                    updateData.book = new Book(updateData.book.getId(), data);
//                }
//            }).build();
//            task.go();
//            task.join();
            try {
                boolean isFind = true;// 是否继续查找url
                StringBuilder builder = new StringBuilder();
                Result result = BiqugeApi.book(url).useStream().execute();
                BufferedReader reader = new BufferedReader(result.charStream());
                String line;
                while ((line = reader.readLine()) != null) {
                    //<meta property="og:novel:latest_chapter_url" content="/book/951/56285894.html" />
                    if (isFind && line.contains("meta") && line.contains("og:novel:latest_chapter_url")) {
                        // url不一样说明有更新
                        if (!line.contains(updateData.book.lastUpdateUrl)) {
                            updateData.update = true;
                            // 不再继续比对url
                            isFind = false;
                        } else {
                            updateData.update = false;
                            break;
                        }
                    }
                    builder.append(line);
                }
                if (updateData.isUpdate()) {
                    updateData.document = Jsoup.parse(builder.toString());
                    updateData.book = new Book(updateData.book.getId(), parserBookDetails(updateData.document));
                    File file = BookFileManager.getBookFile(updateData.book.url);
                    synchronized (LocalBookLocks.getInstance().getLock(file)) {
                        documentWriteFile(file, updateData.document);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return updateData;
        });
    }

    public static class UpdateData {
        private Book book;
        private Document document;
        private boolean update;

        public Book getBook() {
            return book;
        }

        public Document getDocument() {
            return document;
        }

        public boolean isUpdate() {
            return update;
        }
    }
}
