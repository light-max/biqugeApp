package com.lifengqiang.biquge.book.parse.bookdetails;

import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.data.BookDetails;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public abstract class BookDetailsParser {
    protected String url;
    protected boolean saveDocument = false;
    protected Document document;

    public BookDetailsParser(String url) {
        this.url = url;
    }

    public void setSaveDocument(boolean saveDocument) {
        this.saveDocument = saveDocument;
    }

    public Document getDocument() {
        return document;
    }

    public abstract Async.Builder<BookDetails> generateTask();

    public String getUrl() {
        return url;
    }

    /**
     * http://www.biquw.com/book/85/
     */
    protected BookDetails parserBookDetails(Document doc) {
        Element main = doc.selectFirst("#main");
        Element bookInfo = main.selectFirst(".book_info");
        Element nodeList = main.selectFirst(".book_list");
        BookDetails book = new BookDetails();
        parserBookInfo(book, bookInfo);
        parserNodeList(book, nodeList);
        if (saveDocument) {
            this.document = doc;
        }
        return book;
    }

    protected void parserBookInfo(BookDetails book, Element bookInfo) {
        book.url = url;

        Element img = bookInfo.selectFirst(".pic").selectFirst("img");
        book.cover = img.attr("src");

        Element info = bookInfo.selectFirst("#info");
        Element c0 = info.child(0);
        book.name = c0.text();

        Element c1 = info.child(1);
        book.author = c1.child(0).text();

        Element c2 = info.child(2);
        Element s2a = c2.child(0);
        book.lastUpdateNode = s2a.text();
        book.lastUpdateUrl = s2a.attr("href");
        s2a.remove();
        book.dateTime = c2.text().replace("最新更新：(", "").replace(")", "");
        // 需要添加回去，因为document需要缓存
        s2a.appendTo(c2);

        Element c3 = info.child(3);

        Element c4 = info.child(4);
        book.intro = c4.childNode(2).outerHtml();
    }

    protected void parserNodeList(BookDetails book, Element nodeList) {
        List<BookDetails.Node> nodes = book.getNodes();
        for (Element a : nodeList.select("a")) {
            BookDetails.Node node = new BookDetails.Node();
            node.name = a.text();
            node.url = a.attr("href");
            if (!node.url.startsWith("/")) {
                if (book.url.endsWith("/")) {
                    node.url = book.url + node.url;
                } else {
                    node.url = String.format("%s/%s", book.url, node.url);
                }
            }
            nodes.add(node);
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

    protected Document readDocument(File file) {
        try {
            return Jsoup.parse(file, "utf8");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
