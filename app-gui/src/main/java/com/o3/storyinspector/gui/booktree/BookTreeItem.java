package com.o3.storyinspector.gui.booktree;

import com.o3.storyinspector.storydom.Book;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

public class BookTreeItem<T> extends TreeItem<T> {

    public static final int TYPE_BOOK = 0;
    public static final int TYPE_REPORT_STRUCTURE = 1;
    public static final int TYPE_REPORT_CHARACTER = 2;
    public static final int TYPE_REPORT_EMOTION = 3;

    int type;
    Book book;

    public BookTreeItem(int type, T t, Node node, Book book) {
        super(t, node);
        this.type = type;
        this.book = book;
    }

    public Book getBook() {
        return book;
    }

    public int getType() {
        return type;
    }
}
