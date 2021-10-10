package com.lifengqiang.biquge.base.call;

import java.util.Map;

public interface KeyValue {
    Map<Object, Object> map();

    default <T> T map(Object key) {
        return (T) map().get(key);
    }

    default <T> T map(Object key, Object value) {
        return (T) map().put(key, value);
    }
}
