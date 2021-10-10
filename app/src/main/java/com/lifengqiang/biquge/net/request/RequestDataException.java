package com.lifengqiang.biquge.net.request;

public class RequestDataException extends RuntimeException {
    public RequestDataException(Object field) {
        super("无法判断" + field.getClass() + "的类型");
    }
}
