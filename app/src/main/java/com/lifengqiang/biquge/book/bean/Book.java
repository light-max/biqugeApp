package com.lifengqiang.biquge.book.bean;

import com.lifengqiang.biquge.data.BookDetails;

public class Book extends BookDetails {
    private int id;

    public Book() {
    }

    public Book(int id, BookDetails book) {
        this.id = id;
        this.url = book.url;
        this.name = book.name;
        this.cover = book.cover;
        this.author = book.author;
        this.lastUpdateNode = book.lastUpdateNode;
        this.lastUpdateUrl = book.lastUpdateUrl;
        this.intro = book.intro;
        this.dateTime = book.dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
