package com.o3.storyinspector.bookimporter.plaintext;

import com.o3.storyinspector.bookimporter.breakdown.ChapterTokenizer;
import com.o3.storyinspector.storydom.Book;

import java.io.Reader;

public class PlainTextImporter {

    public static Book importBookFromFile(final String path) {
        final Book book = new Book();
        book.setTitle(path);
        book.getChapters().addAll(ChapterTokenizer.tokenizeFromFile(path));
        return book;
    }

    public static Book importBookFromReader(final String title, final Reader reader) {
        final Book book = new Book();
        book.setTitle(title);
        book.getChapters().addAll(ChapterTokenizer.tokenizeFromReader(reader));
        return book;
    }

}
