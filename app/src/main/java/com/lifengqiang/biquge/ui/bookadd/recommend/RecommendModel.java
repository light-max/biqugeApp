package com.lifengqiang.biquge.ui.bookadd.recommend;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;

import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseModel;
import com.lifengqiang.biquge.data.NewestBook;
import com.lifengqiang.biquge.data.PopularBook;
import com.lifengqiang.biquge.data.RecommendBook;
import com.lifengqiang.biquge.net.BiqugeApi;
import com.lifengqiang.biquge.net.result.Result;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class RecommendModel extends BaseModel {
    private MutableLiveData<List<RecommendBook>> recommend;
    private MutableLiveData<List<PopularBook>> popular;
    private MutableLiveData<List<NewestBook>> newest;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        recommend = new MutableLiveData<>();
        popular = new MutableLiveData<>();
        newest = new MutableLiveData<>();
        generateHomeRequest().run();
    }

    public Async.Builder<?> generateHomeRequest() {
        return Async.builder()
                .error((message, e) -> base.toast(message))
                .task(() -> {
                    Result result = BiqugeApi.home().execute();
                    if (result.error() != null) {
                        return result.makeAsyncResultError();
                    }
                    Document doc = result.document();
                    parseDocument(doc);
                    return null;
                });
    }

    private void parseDocument(Document doc) {
        Element main = doc.getElementById("main");
        // 查找推荐阅读节点
        Element recommend = main.selectFirst(".col");
        List<RecommendBook> recommendBooks = parseRecommend(recommend);
        getRecommend().postValue(recommendBooks);
        // 查找热门书籍节点
        Element popular = main.selectFirst(".side");
        List<PopularBook> popularBooks = parsePopular(popular);
        getPopular().postValue(popularBooks);
        // 查找最新小说节点
        Element newest = main.selectFirst(".news.section-cols");
        List<NewestBook> newestBooks = parseNewest(newest);
        getNewest().postValue(newestBooks);
    }

    public MutableLiveData<List<RecommendBook>> getRecommend() {
        return recommend;
    }

    public MutableLiveData<List<PopularBook>> getPopular() {
        return popular;
    }

    public MutableLiveData<List<NewestBook>> getNewest() {
        return newest;
    }

    /**
     * <div class="bk">
     * <div class="pic">
     * <a class="img" href="http://www.biquw.com/book/16583/" title="终极斗罗"> <img src="/files/article/image/16/16583/16583s.jpg" alt="终极斗罗" width="120" height="150"> </a>
     * </div>
     * <h3><a href="http://www.biquw.com/book/16583/" title="终极斗罗">终极斗罗</a></h3>
     * <p class="info"> <span>作者：唐家三少</span> <span class="update">更新：<a href="/book/16583/54035435.html" title="后记">后记</a></span> </p>
     * <p class="intro"> 一万年后，冰化了。斗罗联邦科考队在极北之地科考时发现了一个有着金银双色花纹的蛋，用仪器探察之后，发现里面居然有生命体征，赶忙将其带回研究所进行孵化。 蛋孵化出来了，可孵出来的却是一个婴儿，和人类一模一样的婴儿，一个蛋生的孩子。...</p>
     * </div>
     */
    private List<RecommendBook> parseRecommend(Element element) {
        return new ArrayList<RecommendBook>() {{
            for (Element bk : element.select(".bk")) {
                RecommendBook book = new RecommendBook();
                Element title = bk.selectFirst("h3");
                book.name = title.text();
                book.url = title.child(0).attr("href");
                book.cover = bk.selectFirst("img").attr("src");
                Element info = bk.selectFirst(".info");
                book.author = info.child(0).text().replaceFirst("作者：", "");
                Element lastUpdate = info.child(1).selectFirst("a");
                book.lastUpdateNode = lastUpdate.text();
                book.lastUpdateUrl = lastUpdate.attr("href");
                book.intro = bk.selectFirst(".intro").text();
                add(book);
            }
        }};
    }

    /**
     * <li>
     * <span class="s1">
     * <a href="http://www.biquw.com/book/951/" title="万古神帝">万古神帝</a>
     * </span>
     * <span class="s2">飞天鱼</span>
     * </li>
     */
    private List<PopularBook> parsePopular(Element element) {
        return new ArrayList<PopularBook>() {{
            for (Element li : element.select("li")) {
                PopularBook book = new PopularBook();
                Element a = li.selectFirst("a");
                book.name = a.text();
                book.url = a.attr("href");
                book.author = li.child(1).text();
                add(book);
            }
        }};
    }

    /**
     * <li>
     * <span class="s1">言情小说</span>
     * <span class="s2">
     * <a href="http://www.biquw.com/book/139873/" title="团宠假千金轰动全京城">团宠假千金轰动全京城</a>
     * </span>
     * <span class="s3"><a href="/book/139873/55631212.html">九十 无理花公子</a></span>
     * <span class="s4">麦田里的麦子</span>
     * <span class="s5">08-21 10:47</span>
     * </li>
     */
    private List<NewestBook> parseNewest(Element element) {
        return new ArrayList<NewestBook>() {{
            for (Element li : element.select("li")) {
                NewestBook book = new NewestBook();
                book.type = li.selectFirst(".s1").text();
                Element s1a = li.selectFirst(".s2>a");
                book.name = s1a.text();
                book.url = s1a.attr("href");
                Element s3a = li.selectFirst(".s3>a");
                book.lastUpdateName = s3a.text();
                book.lastUpdateUrl = s3a.attr("href");
                book.author = li.selectFirst(".s4").text();
                book.dateTime = li.selectFirst(".s5").text();
                add(book);
            }
        }};
    }
}
