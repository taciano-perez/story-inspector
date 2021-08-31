package com.o3.storyinspector.gui.core;

import com.o3.storyinspector.storydom.Book;

public class BookEvent {

    public static int BOOK_ADDED = 0;
    public static int BOOK_QUEUED = 1;

    int type;
    Book book;

    public BookEvent(final int type) {
        this.type = type;
    }

    public BookEvent(final int type, final Book book) {
        this.type = type;
        this.book = book;
    }

    public int getType() {
        return type;
    }

    public Book getBook() {
        return book;
    }
}
