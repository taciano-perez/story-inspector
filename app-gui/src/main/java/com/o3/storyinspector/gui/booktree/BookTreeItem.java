package com.o3.storyinspector.gui.booktree;

import com.o3.storyinspector.storydom.Book;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

public class BookTreeItem<T> extends TreeItem<T> {

    Book book;

    public BookTreeItem(T t, Node node, Book book) {
        super(t, node);
        this.book = book;
    }

    public Book getBook() {
        return book;
    }
}
