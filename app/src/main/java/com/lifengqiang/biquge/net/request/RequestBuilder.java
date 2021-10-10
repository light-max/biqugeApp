package com.lifengqiang.biquge.net.request;

import androidx.annotation.Nullable;

import com.lifengqiang.biquge.async.Async;
import com.lifengqiang.biquge.async.AsyncTaskError;
import com.lifengqiang.biquge.net.DefaultOkHttpClient;
import com.lifengqiang.biquge.net.ResultError;
import com.lifengqiang.biquge.net.result.Result;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestBuilder implements Serializable {
    private static final boolean debug = true;

    private String url;
    private String method;
    private boolean useStream = false;
    private RequestData data;

    private final Map<String, Object> log = debug ? new HashMap<>() : null;

    public static RequestBuilder builder() {
        return new RequestBuilder();
    }

    public RequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    public RequestBuilder method(@Method String method) {
        this.method = method;
        return this;
    }

    public RequestBuilder useStream() {
        this.useStream = true;
        return this;
    }

    private RequestData getDataNotNull() {
        if (data == null) {
            data = new RequestData();
        }
        return data;
    }

    public RequestBuilder path(String key, @Nullable Object value) {
        getDataNotNull().setPath(key, value);
        return this;
    }

    public RequestBuilder param(String key, @Nullable Object value) {
        getDataNotNull().setParam(key, value);
        return this;
    }

    public RequestBuilder header(String key, String value) {
        getDataNotNull().setHeader(key, value);
        return this;
    }

    public RequestBuilder bodyType(@BodyType String bodyType) {
        getDataNotNull().setType(bodyType);
        return this;
    }

    public RequestBuilder form() {
        return bodyType(BodyType.FORM);
    }

    public RequestBuilder urlencoded() {
        return bodyType(BodyType.URLENCODED);
    }

    public RequestBuilder raw() {
        return bodyType(BodyType.RAW);
    }

    public RequestBuilder field(String key, Object value) {
        getDataNotNull().setField(key, value);
        return this;
    }

    private String buildUrl() {
        // 请求路径
        StringBuilder pathBuilder = new StringBuilder();
        // 如果有路径参数
        if (data != null && data.paths != null) {
            // 把路径按“/”分割
            String[] split = url.split("/");
            // 遍历分割好的每一段路径
            for (String s : split) {
                // 如果这段路径是用大括号包围的就把路径名当作key，从data.path中把路径值取出来拼接
                if (s.contains("{") && s.contains("}") && s.indexOf("{") < s.indexOf("}")) {
                    String key = s.substring(1, s.length() - 1);
                    Object value = data.paths.get(key);
                    // 并且value不为空
                    if (value != null) {
                        pathBuilder.append("/").append(value);
                    }
                } else {
                    // 按原来的样子拼接
                    pathBuilder.append("/").append(s);
                }
            }
        } else {
            // 不为空也把{}去掉
            String replaceAll = url.replaceAll("\\{+[a-zA-Z0-9]+\\}", "");
            pathBuilder.append(replaceAll);
        }
        // 请求参数
        if (data != null && data.params != null) {
            boolean first = true;
            for (Map.Entry<String, Object> entry : data.params.entrySet()) {
                if (entry.getValue() != null) {
                    if (first) {
                        pathBuilder.append("?");
                        first = false;
                    } else {
                        pathBuilder.append("&");
                    }
                    pathBuilder.append(entry.getKey())
                            .append("=")
                            .append(entry.getValue());
                }
            }

        }
        return pathBuilder.toString();
    }

    private RequestBody buildBody() {
        if (Method.GET.equals(method)) {
            return null;
        } else {
            if (data != null && data.field != null) {
                if (BodyType.FORM.equals(data.type)) {
                    return RequestData.buildDataForm(data);
                } else if (BodyType.URLENCODED.equals(data.type)) {
                    return RequestData.buildUrlEncoded(data);
                } else {
                    return RequestData.buildEmptyBody();
                }
            } else {
                return RequestData.buildEmptyBody();
            }
        }
    }

    protected Request buildRequest() {
        String httpUrl = modifyBuildUrl(buildUrl());
        RequestBody requestBody = buildBody();
        return new Request.Builder()
                .url(httpUrl)
                .method(method, requestBody)
                .build();
    }

    private void setLogInfo(Request request) {
        log.put("url", request.url().toString());
        log.put("method", request.method());
        if (data != null) {
            log.put("bodyData", data.toString());
        }
    }

    protected String modifyBuildUrl(String url) {
        return url;
    }

    public Result execute() {
        try {
            Request request = buildRequest();
            if (debug) {
                setLogInfo(request);
            }
            Response response = DefaultOkHttpClient.getClient()
                    .newCall(request)
                    .execute();
            Result result = Result.response(response);
            if (!useStream) {
                result.parseContent();
            }
            return result;
        } catch (Exception e) {
            if (debug) {
                System.out.println(log);
            }
            return Result.exception(e);
        }
    }

    public Async.Builder<Result> async() {
        return Async.<Result>builder().task(() -> {
            Result result = execute();
            ResultError error = result.error();
            if (error == null) {
                return result;
            }
            return new AsyncTaskError(error.getMessage(), error.getException());
        });
    }

    @Override
    public String toString() {
        return "RequestBuilder{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", useStream=" + useStream +
                ", data=" + data +
                ", log=" + log +
                '}';
    }
}
