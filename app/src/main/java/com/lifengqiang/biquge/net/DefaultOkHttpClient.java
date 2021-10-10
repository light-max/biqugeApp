package com.lifengqiang.biquge.net;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class DefaultOkHttpClient {
    private static OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
            client = new OkHttpClient().newBuilder().cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                    cookieStore.put(httpUrl.host(), list);
                }

                @NotNull
                @Override
                public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                    List<Cookie> cookies = cookieStore.get(httpUrl.host());
                    return cookies != null ? cookies : new ArrayList<>();
                }
            }).connectTimeout(3, TimeUnit.SECONDS).build();
        }
        return client;
    }
}
