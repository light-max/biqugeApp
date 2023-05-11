package com.lifengqiang.biquge.net;

import com.lifengqiang.biquge.net.request.Method;
import com.lifengqiang.biquge.net.request.RequestBuilder;

public class BiqugeApi {
    public static final String baseUrl = "http://www.biquw.info";
    private static final String mBaseUrl = "http://m.biquw.com";

    public static final String oldBaseUrl = "http://www.biquw.com";

    public static RequestBuilder home() {
        return BiqugeRequest.get("/");
    }

    public static RequestBuilder rankingMonth() {
        return MobileBiqugeRequest.get("/top/monthvisit_1/");
    }

    public static RequestBuilder rankingWeek() {
        return MobileBiqugeRequest.get("/top/weekvisit_1/");
    }

    public static RequestBuilder rankingAll() {
        return MobileBiqugeRequest.get("/top/allvisit_1/");
    }

    public static RequestBuilder classification() {
        return MobileBiqugeRequest.get("/sort");
    }

    public static RequestBuilder search(String searchKey) {
        return new BiqugeRequest().method(Method.POST)
                .url("/modules/article/search.php")
                .form()
                .field("searchkey", searchKey);
    }

    public static RequestBuilder book(String url) {
        if (url.contains(mBaseUrl)) {
            url = url.replace(mBaseUrl, baseUrl);
        }
        return BiqugeRequest.get(url);
    }

    public static RequestBuilder chapter(String url) {
        if (url.contains(mBaseUrl)) {
            url = url.replace(mBaseUrl, baseUrl);
        }
        return BiqugeRequest.get(url);
    }

    public static RequestBuilder mbiquge(String url) {
        return MobileBiqugeRequest.get(url);
    }

    public static String url(String argcBaseUrl, String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        } else {
            if (url.startsWith("/")) {
                return argcBaseUrl + url;
            } else {
                return argcBaseUrl + "/" + url;
            }
        }
    }

    public static String url(String url) {
        return url(baseUrl, url);
    }

    public static String murl(String url) {
        return url(mBaseUrl, url);
    }

    static class BiqugeRequest extends RequestBuilder {
        @Override
        protected String modifyBuildUrl(String url) {
            return BiqugeApi.url(url);
        }

        public static RequestBuilder get(String url) {
            return new BiqugeRequest().method(Method.GET).url(url);
        }
    }

    static class MobileBiqugeRequest extends RequestBuilder {
        @Override
        protected String modifyBuildUrl(String url) {
            return BiqugeApi.murl(url);
        }

        public static RequestBuilder get(String url) {
            return new MobileBiqugeRequest().method(Method.GET).url(url);
        }
    }
}
