package com.lifengqiang.biquge.book.parse.content;

import android.util.Pair;

import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.async.AsyncTaskError;
import com.lifengqiang.biquge.book.BookFileManager;
import com.lifengqiang.biquge.book.bean.BookChapter;
import com.lifengqiang.biquge.book.parse.bookdetails.LocalBookDetailsParser;
import com.lifengqiang.biquge.data.BookDetails;
import com.lifengqiang.biquge.net.BiqugeApi;
import com.lifengqiang.biquge.net.result.Result;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ChapterContentParser extends ContentParser {
    private static final Object lock = new Object();
    private final String bookUrl;
    private BookChapter chapter;
    private BookDetails book;
    private Map<String, Pair<String, Integer>> titleMap;
    private int chapterCount = 0;

    public ChapterContentParser(String url, String bookUrl) {
        super(url);
        this.bookUrl = bookUrl;
    }

    @Override
    public Async.Builder<BookChapter> generateTask() {
        return Async.<BookChapter>builder().task(() -> {
            if (book == null) {
                Async async = new LocalBookDetailsParser(bookUrl)
                        .generateTask()
                        .useMainHandler(false)
                        .success((data) -> book = data)
                        .build();
                async.go();
                async.join();

                if (book != null) {
                    titleMap = new HashMap<>();
                    int i = 0;
                    for (BookDetails.Node node : book.getNodes()) {
                        titleMap.put(node.url, new Pair<>(node.name, i++));
                    }
                    chapterCount = book.nodes.size();
                }
            }
            synchronized (lock) {
                if (url == null) {
                    if (book != null) {
                        url = book.getNodes().get(0).url;
                    } else {
                        throw new RuntimeException("网络异常");
                    }
                }
                File file = BookFileManager.getBookChapterFile(url);
                if (!file.exists()) {
                    Result result = BiqugeApi.chapter(url).execute();
                    if (result.error() != null) {
                        return result.makeAsyncResultError();
                    }
                    documentWriteFile(file, result.document());
                }
                // 解析节点出错时删除本地文件，这也算是一种修复
                try {
                    chapter = parserBookChapter(readDocument(file));
                } catch (Exception e) {
                    file.delete();
                    return new AsyncTaskError("已尝试自动修复章节，请重新打开", e);
                }
                chapter.setChapterName(getChapterName(chapter.getSelf()));
                return chapter;
            }
        });
    }

    private Pair<String, Integer> getNotNullChapter(String chapterUrl) {
        Pair<String, Integer> pair = titleMap.get(chapterUrl);
        if (pair == null) {
            pair = titleMap.get(BiqugeApi.url(chapterUrl));
        }
        if (pair == null) {
            pair = new Pair<>("", 0);
        }
        return pair;
    }

    public String getChapterName(String chapterUrl) {
        return getNotNullChapter(chapterUrl).first;
//        return titleMap.get(BiqugeApi.url(chapterUrl)).first;
    }

    public int indexOfChapter(String chapterUrl) {
        return getNotNullChapter(chapterUrl).second;
//        return titleMap.get(BiqugeApi.url(chapterUrl)).second;
    }

    public BookChapter getChapter() {
        return chapter;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public BookDetails.Node getNode(int position) {
        return book.getNodes().get(position);
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public String getChapterUrl() {
        return BiqugeApi.url(url);
    }
}
