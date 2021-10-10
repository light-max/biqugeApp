package com.lifengqiang.biquge.book.parse.content;

import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.book.bean.BookChapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class ContentParser {
    protected String url;

    public ContentParser(String url) {
        this.url = url;
    }

    public ContentParser setUrl(String url) {
        this.url = url;
        return this;
    }

    public abstract Async.Builder<BookChapter> generateTask();

    protected Document readDocument(File file) {
        try {
            return Jsoup.parse(file, "utf8");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected void documentWriteFile(File file, Document doc) {
        try {
            String html = doc.outerHtml();
            OutputStream out = new FileOutputStream(file);
            out.write(html.getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected BookChapter parserBookChapter(Document doc) {
        BookChapter chapter = new BookChapter();
        parserContent(chapter, doc.selectFirst("#htmlContent"));
        parserNodes(chapter, doc.selectFirst(".chapter_Turnpage"));
        return chapter;
    }

    protected void parserContent(BookChapter chapter, Element element) {
        chapter.setHtmlContent(element.html());
    }

    protected void parserNodes(BookChapter chapter, Element element) {
        for (Element a : element.select("a")) {
            if (a.text().contains("上一页")) {
                chapter.setPrevious(a.attr("href"));
                // 如果是/book/{bookId} 而不是/book/{bookId}/{chapterId}.html
                if (!chapter.getPrevious().endsWith(".html")) {
                    chapter.setPrevious(null);
                }
            } else if (a.text().contains("下一页")) {
                chapter.setNext(a.attr("href"));
                if (!chapter.getNext().endsWith(".html")) {
                    chapter.setNext(null);
                }
            }
        }
        chapter.setSelf(url);
    }
}
