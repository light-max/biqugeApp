package com.lifengqiang.biquge.book;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LocalBookLocks {
    private final Map<File, Object> map;

    private static LocalBookLocks instance;

    public static LocalBookLocks getInstance() {
        synchronized (LocalBookLocks.class) {
            if (instance == null) {
                instance = new LocalBookLocks();
            }
        }
        return instance;
    }

    private LocalBookLocks() {
        map = new HashMap<>();
    }

    public Object getLock(File file) {
        synchronized (this) {
            Object lock = map.get(file);
            if (lock == null) {
                lock = new Object();
                map.put(file, lock);
            }
            return lock;
        }
    }
}
