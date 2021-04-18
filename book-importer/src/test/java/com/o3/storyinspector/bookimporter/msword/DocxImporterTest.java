package com.o3.storyinspector.bookimporter.msword;

import com.o3.storyinspector.bookimporter.util.FileUtils;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.io.XmlWriter;
import junitx.framework.FileAssert;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocxImporterTest {

    private static final String SAMPLE_BOOK_PATH = DocxImporterTest.class.getResource("/a-study-in-scarlett.docx").getPath();
    private static final String EXPECTED_XML_PATH = DocxImporterTest.class.getResource("/expected-storydom-a-study-in-scarlett-from-docx.xml").getPath();
    private static final String OUTPUT_XML_PATH = "./target/storydom-a-study-in-scarlett.xml";

    @Test
    void importBookFromFile() throws Exception {
        // given

        // when
        final Book importedBook = DocxImporter.importBookFromFile(FileUtils.getPathFromUri(SAMPLE_BOOK_PATH));

        // then
        assertTrue(importedBook.getTitle().endsWith("a-study-in-scarlett.docx"));
        assertEquals(14, importedBook.getChapters().size());
    }

    @Test
    void exportImportedBook() throws Exception {
        // given

        // when
        final Book importedBook = DocxImporter.importBookFromFile(FileUtils.getPathFromUri(SAMPLE_BOOK_PATH));
        importedBook.setTitle("Book Title");
        XmlWriter.exportBookToXmlFile(importedBook, new File(OUTPUT_XML_PATH));

        // then
        FileAssert.assertEquals(new File(EXPECTED_XML_PATH), new File(OUTPUT_XML_PATH));
    }
}