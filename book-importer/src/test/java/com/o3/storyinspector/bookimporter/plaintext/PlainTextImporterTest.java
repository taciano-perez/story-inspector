package com.o3.storyinspector.bookimporter.plaintext;

import com.o3.storyinspector.bookimporter.util.FileUtils;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.io.XmlWriter;
import junitx.framework.FileAssert;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlainTextImporterTest {

    private static final String SAMPLE_BOOK_PATH = PlainTextImporterTest.class.getResource("/a-study-in-scarlett-244-0.txt").getPath();
    private static final String EXPECTED_XML_PATH = PlainTextImporterTest.class.getResource("/expected-storydom-a-study-in-scarlett.xml").getPath();
    private static final String OUTPUT_XML_PATH = "./target/storydom-a-study-in-scarlett.xml";

    @Test
    void importBook() throws IOException {
        // given

        // when
        final Book importedBook = PlainTextImporter.importBookFromFile(FileUtils.getPathFromUri(SAMPLE_BOOK_PATH));

        // then
        assertTrue(importedBook.getTitle().endsWith("a-study-in-scarlett-244-0.txt"));
        assertEquals(14, importedBook.getChapters().size());
    }

    @Test
    void exportImportedBook() throws JAXBException, IOException {
        // given

        // when
        final Book importedBook = PlainTextImporter.importBookFromFile(FileUtils.getPathFromUri(SAMPLE_BOOK_PATH));
        importedBook.setTitle("Book Title");
        XmlWriter.exportBookToXmlFile(importedBook, new File(OUTPUT_XML_PATH));

        // then
        FileAssert.assertEquals(new File(EXPECTED_XML_PATH), new File(OUTPUT_XML_PATH));
    }

}