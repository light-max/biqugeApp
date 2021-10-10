package com.lifengqiang.biquge.ui.search;

import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.base.mvp.BaseModel;
import com.lifengqiang.biquge.data.SearchBook;
import com.lifengqiang.biquge.net.BiqugeApi;
import com.lifengqiang.biquge.net.result.Result;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class BookSearchModel extends BaseModel {
    public Async.Builder<List<SearchBook>> generateSearchRequest(String value) {
        return Async.<List<SearchBook>>builder()
                .error((message, e) -> base.toast(message))
                .task(() -> {
                    Result result = BiqugeApi.search(value).execute();
                    if (result.error() != null) {
                        return result.makeAsyncResultError();
                    }
                    Document doc = result.document();
                    Element ul = doc.selectFirst("#main").selectFirst("ul");
                    return parseSearchBooks(ul);
                });
    }

    /**
     * <li>
     * <p class="s1"><a href="http://www.biquw.com/book/85/" target="_blank">武炼巅峰</a></p>
     * <p class="s2"><a href="/book/85/55781580.html" target="_blank">第五千九百八十三章 第九层境界</a></p>
     * <p class="s3">莫默</p>
     * <p class="s4">6742K</p>
     * <p class="s5">连载中</p>
     * <p class="s6">21-08-30</p>
     * </li>
     */
    private List<SearchBook> parseSearchBooks(Element element) {
        return new ArrayList<SearchBook>() {{
            for (Element li : element.select("li")) {
                SearchBook book = new SearchBook();
                Element s1a = li.child(0).child(0);
                Element s2a = li.child(1).child(0);
                book.name = s1a.text();
                book.url = s1a.attr("href");
                book.lastUpdateNode = s2a.text();
                book.lastUpdateUrl = s2a.attr("href");
                book.author = li.child(2).text();
                book.textCount = li.child(3).text();
                book.status = li.child(4).text();
                book.dateTime = li.child(5).text();
                add(book);
            }
        }};
    }
}
