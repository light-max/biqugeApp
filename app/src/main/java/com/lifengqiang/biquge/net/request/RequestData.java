package com.lifengqiang.biquge.net.request;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RequestData {
    public String type = BodyType.NONE;

    public Map<String, Object> paths;
    public Map<String, Object> params;
    public Map<String, String> headers;
    public Map<String, Object> field;

    public void setType(@BodyType String type) {
        this.type = type;
    }

    public void setPath(String key, Object value) {
        if (paths == null) {
            paths = new HashMap<>();
        }
        paths.put(key, value);
    }

    public void setParam(String key, Object value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
    }

    public void setHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(key, value);
    }


    public void setField(String key, Object value) {
        if (field == null) {
            field = new HashMap<>();
        }
        field.put(key, value);
    }

    @Override
    public String toString() {
        return "{\n" +
                "   type:" + type + "\n" +
                "   field:" + field + "\n" +
                "}";
    }

    public static RequestBody buildDataForm(RequestData data) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        data.field.forEach((key, value) -> {
            if (value != null) {
                if (value instanceof String) {
                    builder.addFormDataPart(key, (String) value);
                } else if (value instanceof Number) {
                    builder.addFormDataPart(key, value.toString());
                } else if (value instanceof Object[]) {
                    Object[] objects = (Object[]) value;
                    for (Object o : objects) {
                        builder.addFormDataPart(key, o.toString());
                    }
                } else if (value instanceof Collection) {
                    Collection<?> list = (Collection<?>) value;
                    for (Object o : list) {
                        builder.addFormDataPart(key, o.toString());
                    }
                } else if (value instanceof File) {
                    String absolutePath = ((File) value).getAbsolutePath();
                    RequestBody r = RequestBody.create(MediaType.parse("application/octet-stream"),
                            new File(absolutePath));
                    builder.addFormDataPart(key, absolutePath, r);
                } else {
                    throw new RequestDataException(value);
                }
            }
        });
        return builder.build();
    }

    public static RequestBody buildUrlEncoded(RequestData data) {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        StringBuilder content = new StringBuilder();
        data.field.forEach((key, value) -> {
            if (value instanceof String || value instanceof Number) {
                content.append("&").append(key).append("=").append(value);
            } else if (value instanceof Object[]) {
                Object[] objects = (Object[]) value;
                for (Object o : objects) {
                    content.append("&").append(key).append("=").append(o);
                }
            } else if (value instanceof Collection) {
                Collection<?> list = (Collection<?>) value;
                for (Object o : list) {
                    content.append("&").append(key).append("=").append(o);
                }
            } else {
                throw new RequestDataException(value);
            }
        });
        return RequestBody.create(mediaType, content.substring(1));
    }

    public static RequestBody buildEmptyBody() {
        MediaType mediaType = MediaType.parse("text/plain");
        return RequestBody.create(mediaType, "");
    }
}
