package com.lifengqiang.biquge.ui.bookadd.category;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;

import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.base.call.Base;
import com.lifengqiang.biquge.base.mvp.BaseModel;
import com.lifengqiang.biquge.data.BookClassification;
import com.lifengqiang.biquge.net.BiqugeApi;
import com.lifengqiang.biquge.net.result.Result;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class CategoryModel extends BaseModel {
    private MutableLiveData<List<BookClassification>> classification;

    @Override
    public void onCreate(Base base, Bundle savedInstanceState) {
        super.onCreate(base, savedInstanceState);
        classification = new MutableLiveData<>();
        generateAsyncRequest().run();
    }

    public Async.Builder<?> generateAsyncRequest() {
//        BiqugeApi.classification().async().error((message, e) -> {
//            base.toast(message);
//        }).success(data -> {
//            List<BookClassification> classifications = new ArrayList<>();
//            try {
//                Document doc = data.document();
//                Element sort = doc.selectFirst(".sorttop");
//                for (Element a : sort.select("li>a")) {
//                    BookClassification c = new BookClassification();
//                    c.url = a.attr("href");
//                    a.child(0).remove();
//                    c.name = a.text().replace("小说", "");
//                    classifications.add(c);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            getClassification().postValue(classifications);
//        });
        return Async.<List<BookClassification>>builder()
                .error((message, e) -> base.toast(message))
                .success(data -> getClassification().postValue(data))
                .task(() -> {
                    Result result = BiqugeApi.classification().execute();
                    if (result.error() != null) {
                        return result.makeAsyncResultError();
                    }
                    List<BookClassification> classifications = new ArrayList<>();
                    Document doc = result.document();
                    Element sort = doc.selectFirst(".sorttop");
                    for (Element a : sort.select("li>a")) {
                        BookClassification c = new BookClassification();
                        c.url = a.attr("href");
                        a.child(0).remove();
                        c.name = a.text().replace("小说", "");
                        classifications.add(c);
                    }
                    return classifications;
                });
    }

    public MutableLiveData<List<BookClassification>> getClassification() {
        return classification;
    }
}
