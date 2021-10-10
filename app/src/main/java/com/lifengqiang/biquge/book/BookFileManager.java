package com.lifengqiang.biquge.book;

import android.content.Context;

import java.io.File;

public class BookFileManager {
    private static final String BOOK_SHELF_PATH = "book_shelf.json";

    private static Context applicationContext;

    public static void setApplicationContext(Context applicationContext) {
        BookFileManager.applicationContext = applicationContext;
    }

    /**
     * 书架文件
     */
    public static File getBookShelfFile() {
        File file = applicationContext.getExternalFilesDir(null);
        file.mkdirs();
        return new File(file, BOOK_SHELF_PATH);
    }

    /**
     * 书籍目录
     */
    public static File getBooksDir() {
        File dir = applicationContext.getExternalFilesDir("books");
        dir.mkdirs();
        return dir;
    }

    /**
     * 章节目录
     */
    public static File getChaptersDir() {
        File dir = applicationContext.getExternalFilesDir("chapters");
        dir.mkdirs();
        return dir;
    }

    /**
     * 书本文件
     * http://www.biquw.com/book/951/
     */
    public static File getBookFile(String bookUrl) {
        String[] s = bookUrl.split("/");
        String bookId = s[s.length - 1];
        if (bookId.endsWith(".html")) {
            return new File(getBooksDir(), bookId);
        } else {
            return new File(getBooksDir(), bookId + ".html");
        }
    }

    /**
     * http://www.biquw.com/book/951/11509408.html
     * /book/951/11509408.html
     */
    public static File getBookChapterFile(String chapterUrl) {
        String[] split = chapterUrl.split("/");
        int chapterIndex = split.length - 1;
        int bookIndex = split.length - 2;
        File chapterDir = new File(getChaptersDir(), split[bookIndex]);
        chapterDir.mkdirs();
        return new File(chapterDir, split[chapterIndex]);
    }
}
