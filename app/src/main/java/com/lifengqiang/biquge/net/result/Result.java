package com.lifengqiang.biquge.net.result;

import com.google.gson.Gson;
import com.lifengqiang.biquge.async.AsyncTaskError;
import com.lifengqiang.biquge.net.ResultError;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Result {
    protected Response response;
    protected ResponseBody body;
    protected ResultError error;
    protected byte[] byteData;

    private boolean isResponse = false;

    public void parseContent() throws IOException {
        InputStream in = body().byteStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len;
        byte[] bytes = new byte[1024 * 10];
        while ((len = in.read(bytes)) != -1) {
            out.write(bytes, 0, len);
        }
        byteData = out.toByteArray();
        isResponse = true;
    }

    public boolean isResponse() {
        return isResponse;
    }

    public String string() {
        try {
            if (byteData == null) {
                parseContent();
            }
            return new String(byteData);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] bytes() {
        try {
            if (byteData == null) {
                parseContent();
            }
            return byteData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public char[] chars() {
        try {
            if (byteData == null) {
                parseContent();
            }
            return new String(byteData).toCharArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T json(Class<T> tClass) {
        try {
            if (byteData == null) {
                parseContent();
            }
            return new Gson().fromJson(string(), tClass);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Document document() {
        try {
            if (byteData == null) {
                parseContent();
            }
            return Jsoup.parse(new String(byteData));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void parseBody() {
        if (body == null) {
            body = response.body();
        }
    }

    public ResponseBody body() {
        parseBody();
        return body;
    }

    public Response response() {
        return response;
    }

    public Reader charStream() {
        return body().charStream();
    }

    public InputStream inputStream() {
        return body().byteStream();
    }

    public int code() {
        return response.code();
    }

    public String message() {
        return response.message();
    }

    public Headers headers() {
        return response.headers();
    }

    public ResultError error() {
        return error;
    }

    public AsyncTaskError makeAsyncResultError() {
        if (error != null) {
            return new AsyncTaskError(error.getMessage(), error.getException());
        }
        return null;
    }

    public static Result exception(Exception e) {
        Result result = new Result();
        if (e instanceof UnknownHostException) {
            result.error = new ResultError("无法连接到服务器, 请重试...", e);
        } else if (e instanceof SocketTimeoutException) {
            result.error = new ResultError("连接超时, 请重试...", e);
        } else if (e instanceof IOException) {
            result.error = new ResultError("网络错误", e);
        } else {
            result.error = new ResultError("网络错误, 请重试...", e);
        }
        return result;
    }

    public static Result response(Response response) {
        Result result = new Result();
        result.response = response;
        return result;
    }
}
