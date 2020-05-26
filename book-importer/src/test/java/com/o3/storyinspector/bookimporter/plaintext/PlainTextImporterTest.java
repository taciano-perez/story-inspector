package com.o3.storyinspector.bookimporter.plaintext;

import com.o3.storyinspector.storydom.Book;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlainTextImporterTest {

    private static final String SAMPLE_BOOK_PATH = PlainTextImporterTest.class.getResource("/a-study-in-scarlett-244-0.txt").getPath().replaceFirst("/", "");

    @Test
    void importBook() throws IOException {
        // given

        // when
        Book importedBook = PlainTextImporter.importBook(SAMPLE_BOOK_PATH);

        // then
        assertTrue(importedBook.getTitle().endsWith("a-study-in-scarlett-244-0.txt"));
        assertEquals(14, importedBook.getChapters().size());
        importedBook.getChapters().forEach(ch -> System.out.println("TITLE: " + ch.getTitle() + "\n" + ch.getBody()));
    }
}