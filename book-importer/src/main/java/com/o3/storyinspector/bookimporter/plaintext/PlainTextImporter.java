package com.o3.storyinspector.bookimporter.plaintext;

import com.o3.storyinspector.bookimporter.breakdown.ChapterTokenizer;
import com.o3.storyinspector.storydom.Book;

public class PlainTextImporter {

    public static Book importBook(final String path) {
        Book book = new Book();
        book.setTitle(path);
        book.getChapters().addAll(ChapterTokenizer.tokenize(path));
        return book;
    }
}
