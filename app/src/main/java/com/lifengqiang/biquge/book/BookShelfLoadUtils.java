package com.lifengqiang.biquge.book;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.lifengqiang.biquge.base.call.ValueCall;
import com.lifengqiang.biquge.book.bean.Book;
import com.lifengqiang.biquge.book.bean.BookShelf;
import com.lifengqiang.biquge.data.BookDetails;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;

public class BookShelfLoadUtils {
    private static BookShelfLoadUtils instance;
    private final HandlerThread handlerThread;
    private final Handler handler;
    private final Handler mainHandler;

    public static BookShelfLoadUtils getInstance() {
        synchronized (BookShelfLoadUtils.class) {
            if (instance == null) {
                instance = new BookShelfLoadUtils();
            }
        }
        return instance;
    }

    private BookShelfLoadUtils() {
        handlerThread = new HandlerThread("BookShelfLoadUtils");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public static void post(Runnable runnable) {
        getInstance().handler.post(runnable);
    }

    public static void addBook(BookDetails book, ValueCall<Integer, String> call) {
        BookShelfLoadUtils utils = getInstance();
        utils.handler.post(() -> {
            File file = BookFileManager.getBookShelfFile();
            BookShelf bookShelf = BookShelf.reader(file);
            assert bookShelf != null;
            int result = bookShelf.addBook(book);
            bookShelf.setLastUpdateTime(result == 0 ?
                    System.currentTimeMillis() :
                    bookShelf.getLastUpdateTime()
            );
            bookShelf.writeToFile(file);
            utils.mainHandler.post(() -> {
                if (result == 0) {
                    call.call(result, null);
                } else if (result == -1) {
                    call.call(result, "错误");
                } else if (result == 1) {
                    call.call(result, "书籍已存在");
                }
            });
        });
    }

    public static void readBooks(ValueCall<BookShelf, String> call) {
        BookShelfLoadUtils utils = getInstance();
        utils.handler.post(() -> {
            File file = BookFileManager.getBookShelfFile();
            BookShelf bookShelf = BookShelf.reader(file);
            utils.mainHandler.post(() -> {
                if (bookShelf == null) {
                    call.call(null, "错误");
                } else {
                    call.call(bookShelf, null);
                }
            });
        });
    }

    public static void readLastUpdateTime(ValueCall<Long, String> call) {
        BookShelfLoadUtils utils = getInstance();
        utils.handler.post(() -> {
            File file = BookFileManager.getBookShelfFile();
            if (file.exists()) {
                try {
                    byte[] bytes = new byte[64];
                    InputStream in = new FileInputStream(file);
                    int len = in.read(bytes);
                    long sum = 0;
                    for (int i = "{\"lastUpdateTime\"}:".length() - 1; i < len; i++) {
                        int c = bytes[i] - '0';
                        if (c >= 0 && c < 10) {
                            sum *= 10;
                            sum += c;
                        } else {
                            break;
                        }
                    }
                    long finalSum = sum;
                    utils.mainHandler.post(() -> {
                        call.call(finalSum == 0 ? null : finalSum, null);
                    });
                } catch (IOException e) {
                    utils.mainHandler.post(() -> {
                        call.call(null, null);
                    });
                    e.printStackTrace();
                }
            } else {
                BookShelf shelf = new BookShelf();
                shelf.setLastUpdateTime(System.currentTimeMillis());
                shelf.writeToFile(file);
                utils.mainHandler.post(() -> {
                    call.call(shelf.getLastUpdateTime(), null);
                });
            }
        });
    }

    public static void deleteBook(Book book, ValueCall<Long, String> call) {
        BookShelfLoadUtils utils = getInstance();
        utils.handler.post(() -> {
            File file = BookFileManager.getBookShelfFile();
            BookShelf bookShelf = BookShelf.reader(file);
            assert bookShelf != null;
            bookShelf.removeBook(book);
            bookShelf.setLastUpdateTime(System.currentTimeMillis());
            bookShelf.writeToFile(file);
            utils.mainHandler.post(() -> {
                call.call(bookShelf.getLastUpdateTime(), null);
            });
        });
    }

    public static void saveNewBookShelf(List<Book> books) {
        BookShelfLoadUtils utils = getInstance();
        utils.handler.post(() -> {
            File file = BookFileManager.getBookShelfFile();
            BookShelf bookShelf = BookShelf.reader(file);
            LinkedHashMap<Integer, Book> map = bookShelf.getNotNullMap();
            for (Book book : books) {
                map.put(book.getId(), book);
            }
            bookShelf.writeToFile(file);
        });
    }
}
