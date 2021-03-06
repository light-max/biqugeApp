package com.lifengqiang.biquge.ui.bookadd.ranking.child;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;

import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.async.AsyncTaskError;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseModel;
import com.lifengqiang.biquge.data.RankingBook;
import com.lifengqiang.biquge.net.ResultError;
import com.lifengqiang.biquge.net.request.RequestBuilder;
import com.lifengqiang.biquge.net.result.Result;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class RankingChildModel extends BaseModel {
    private MutableLiveData<List<RankingBook>> newBook;
    private String nextPageUrl = null;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        newBook = new MutableLiveData<>();
    }

    public Async.Builder<?> wrapAsyncRequest(RequestBuilder request) {
        return Async.<List<RankingBook>>builder().error((message, e) -> {
            base.toast(message);
        }).success(books -> {
            newBook.postValue(books);
        }).task(() -> {
            Result result = request.execute();
            if (result.error() != null) {
                ResultError e = result.error();
                return new AsyncTaskError(e.getMessage(), e.getException());
            }
            Document doc = result.document();
            Elements sections = doc.getElementsByTag("section");
            Elements section = sections.select(".list.fk");
            Element page = doc.selectFirst(".page");
            nextPageUrl = selectNextPageUrl(page);
            return parseRankingBooks(section.first());
        });
    }

    /**
     * <ul class="xbk">
     * <li class="tjimg">
     *   <a href="/book/19110/">
     *     <img src="http://www.biquw.com/files/article/image/19/19110/19110s.jpg" onerror="this.src='http://m.biquw.com/css/noimg.jpg'" />
     *   </a>
     * <li class="tjxs">
     *   <span class="xsm"><a href="/book/19110/">????????????</a></span>
     *   <span class="">?????????<a href="/author/???????????????">???????????????</a></span>
     *   <span class="">??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????</span>
     *   <span class="tjrs"><i>?????????</i>&nbsp;</span>
     * </li>
     * </ul>
     */
    private List<RankingBook> parseRankingBooks(Element element) {
        return new ArrayList<RankingBook>() {{
            for (Element ul : element.select("ul")) {
                RankingBook book = new RankingBook();
                book.cover = ul.selectFirst("img").attr("src");
                Element li = ul.selectFirst(".tjxs");
                Element a = li.child(0).child(0);
                book.name = a.text();
                book.url = a.attr("href");
                book.author = li.child(1).child(0).text();
                book.intro = li.child(2).text();
                book.status = li.child(3).text().trim();
                add(book);
            }
        }};
    }

    private String selectNextPageUrl(Element element) {
        for (Element child : element.children()) {
            if (child.text().equals("?????????")) {
                return child.attr("href");
            }
        }
        return null;
    }

    public MutableLiveData<List<RankingBook>> getNewBook() {
        return newBook;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }
}
