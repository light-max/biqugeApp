package com.lifengqiang.biquge.ui.bookadd.category.child;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;

import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.async.AsyncTaskError;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseModel;
import com.lifengqiang.biquge.data.ClassifiedBook;
import com.lifengqiang.biquge.net.BiqugeApi;
import com.lifengqiang.biquge.net.ResultError;
import com.lifengqiang.biquge.net.result.Result;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class CategoryChildModel extends BaseModel {
    private MutableLiveData<List<ClassifiedBook>> newBooks;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        newBooks = new MutableLiveData<>();
    }

    public Async.Builder<?> generateAsyncRequest(String url) {
        return Async.<List<ClassifiedBook>>builder()
                .error((message, e) -> base.toast(message))
                .success(data -> newBooks.postValue(data))
                .task(() -> {
                    Result result = BiqugeApi.mbiquge(url).execute();
                    ResultError e;
                    if ((e = result.error()) != null) {
                        return new AsyncTaskError(e.getMessage(), e.getException());
                    }
                    Document doc = result.document();
                    Elements sections = doc.getElementsByTag("section");
                    Elements section = sections.select(".list.fk");
                    Element page = doc.selectFirst(".page");
                    base.map("nextPageUrl", selectNextPageUrl(page));
                    return parseRankingBooks(section);
                });
    }

    public Async.Builder<?> generateNextPageAsyncRequest() {
        String nextPageUrl = base.map("nextPageUrl");
        if (nextPageUrl != null) {
            return generateAsyncRequest(nextPageUrl);
        }else {
            return Async.builder();
        }
    }

    /**
     * <ul class="xbk">
     *   <li class="tjimg">
     *     <a href="/book/89994/">
     *       <img src="http://www.biquw.com/files/article/image/89/89994/89994s.jpg" onerror="this.src='http://m.biquw.com/css/noimg.jpg'">
     *     </a>
     *   </li>
     *   <li class="tjxs">
     *     <span class="xsm"><a href="/book/89994/">深空彼岸</a></span>
     *     <span class="">作者：<a href="/author/辰东">辰东</a></span>
     *     <span class=""> 浩瀚的宇宙中，一片星系的生灭，也不过是刹那的斑驳流光。仰望星空，总有种结局已注定的伤感，千百年后你我在哪里？ 家国，文明火光，地球，都不过是深空中的一粒尘埃。星空一瞬，人间千年。 虫鸣一世不过秋，你我一样在争渡。深空尽头到底有什么？</span>
     *     <span class="tjrs"><i>连载中</i>&nbsp;</span>
     *   </li>
     * </ul>
     */
    private List<ClassifiedBook> parseRankingBooks(Elements element) {
        return new ArrayList<ClassifiedBook>() {{
            for (Element ul : element.select("ul")) {
                ClassifiedBook book = new ClassifiedBook();
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
            if (child.text().equals("下一页")) {
                return child.attr("href");
            }
        }
        return null;
    }

    public MutableLiveData<List<ClassifiedBook>> getNewBooks() {
        return newBooks;
    }
}
