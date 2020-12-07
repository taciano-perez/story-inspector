package com.o3.storyinspector.bookimporter.plaintext;

import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.io.XmlWriter;
import junitx.framework.FileAssert;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlainTextImporterTest {

    private static final String SAMPLE_BOOK_PATH = PlainTextImporterTest.class.getResource("/a-study-in-scarlett-244-0.txt").getPath().replaceFirst("/", "");
    private static final String EXPECTED_XML_PATH = PlainTextImporterTest.class.getResource("/expected-storydom-a-study-in-scarlett.xml").getPath().replaceFirst("/", "");
    private static final String OUTPUT_XML_PATH = "./target/storydom-a-study-in-scarlett.xml";

    @Test
    void importBook() {
        // given

        // when
        final Book importedBook = PlainTextImporter.importBookFromFile(SAMPLE_BOOK_PATH);

        // then
        assertTrue(importedBook.getTitle().endsWith("a-study-in-scarlett-244-0.txt"));
        assertEquals(14, importedBook.getChapters().size());
    }

    @Test
    void exportImportedBook() throws JAXBException {
        // given

        // when
        final Book importedBook = PlainTextImporter.importBookFromFile(SAMPLE_BOOK_PATH);
        XmlWriter.exportBookToXmlFile(importedBook, new File(OUTPUT_XML_PATH));

        // then
        FileAssert.assertEquals(new File(EXPECTED_XML_PATH), new File(OUTPUT_XML_PATH));
    }

}