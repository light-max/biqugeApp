package com.lifengqiang.biquge.book.bean;

import com.google.gson.Gson;
import com.lifengqiang.biquge.data.BookDetails;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class BookShelf {
    private long lastUpdateTime;
    private LinkedHashMap<Integer, Book> map;

    public LinkedHashMap<Integer, Book> getNotNullMap() {
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return map;
    }

    /**
     * @return 0:成功, 1:已存在, -1:错误
     */
    public int addBook(BookDetails book) {
        for (String s : book.url.split("/")) {
            int sum = 0;
            for (char c : s.toCharArray()) {
                int n = c - '0';
                if (n >= 0 && n < 10) {
                    sum *= 10;
                    sum += n;
                } else {
                    break;
                }
            }
            if (sum != 0) {
                LinkedHashMap<Integer, Book> map = getNotNullMap();
                if (map.containsKey(sum)) {
                    return 1;
                } else {
                    map.put(sum, new Book(sum, book));
                    return 0;
                }
            }
        }
        return -1;
    }

    public void removeBook(Book book) {
        getNotNullMap().remove(book.getId());
    }

    public List<Book> getBooks() {
        return new ArrayList<>(getNotNullMap().values());
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public void writeToFile(File file) {
        try {
            String json = toJson();
            OutputStream out = new FileOutputStream(file);
            out.write(json.getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BookShelf reader(File file) {
        try {
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(fileInputStream);
                return new Gson().fromJson(reader, BookShelf.class);
            } else {
                BookShelf shelf = new BookShelf();
                shelf.setLastUpdateTime(System.currentTimeMillis());
                return shelf;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
