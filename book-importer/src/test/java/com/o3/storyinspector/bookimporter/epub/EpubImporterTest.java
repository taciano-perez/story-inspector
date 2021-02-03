package com.o3.storyinspector.bookimporter.epub;

import com.o3.storyinspector.bookimporter.util.FileUtils;
import com.o3.storyinspector.storydom.Book;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpubImporterTest {

    private static final String SAMPLE_BOOK_PATH = EpubImporterTest.class.getResource("/a-study-in-scarlett-pg244.epub").getPath();

    @org.junit.jupiter.api.Test
    void importBook() throws IOException {
        // given

        // when
        final Book importedBook = EpubImporter.importBook(FileUtils.getPathFromUri(SAMPLE_BOOK_PATH));

        // then
        assertEquals("A Study in Scarlet", importedBook.getTitle());
        assertEquals(6, importedBook.getChapters().size());
    }
}