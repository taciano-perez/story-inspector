package com.o3.storyinspector.gui.core;

import com.o3.storyinspector.storydom.Book;

public class ReportEvent {

    public static int OPEN_REPORT_BOOK_STRUCTURE = 0;
    public static int OPEN_REPORT_CHARACTER = 1;
    public static int OPEN_REPORT_EMOTION = 2;
    public static int OPEN_REPORT_READABILITY = 3;
    public static int OPEN_REPORT_SENTENCE_VARIETY = 4;

    int type;
    Book book;

    public ReportEvent(int type, Book book) {
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
