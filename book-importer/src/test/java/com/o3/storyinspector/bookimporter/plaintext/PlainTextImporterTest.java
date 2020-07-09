package com.o3.storyinspector.bookimporter.plaintext;

import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.io.XmlWriter;
import junitx.framework.FileAssert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlainTextImporterTest {

    private static final String SAMPLE_BOOK_PATH = PlainTextImporterTest.class.getResource("/a-study-in-scarlett-244-0.txt").getPath().replaceFirst("/", "");
    private static final String EXPECTED_XML_PATH = PlainTextImporterTest.class.getResource("/expected-storydom-a-study-in-scarlett.xml").getPath().replaceFirst("/", "");
    private static final String OUTPUT_XML_PATH = "./target/storydom-a-study-in-scarlett.xml";

    private static final String PUNK_ROMANA_PATH = PlainTextImporterTest.class.getResource("/punk-romana-dave-kavanaugh.txt").getPath().replaceFirst("/", "");
    private static final String OUTPUT_PUNK_ROMANA_PATH = "./target/storydom-punk-romana-dave-kavanaugh.xml";

    private static final String WINTER_PATH = PlainTextImporterTest.class.getResource("/winter-juho-finn.txt").getPath().replaceFirst("/", "");
    private static final String OUTPUT_WINTER_PATH = "./target/storydom-winter-juho-finn.xml";

    @Test
    void importBook() {
        // given

        // when
        final Book importedBook = PlainTextImporter.importBook(SAMPLE_BOOK_PATH);

        // then
        assertTrue(importedBook.getTitle().endsWith("a-study-in-scarlett-244-0.txt"));
        assertEquals(14, importedBook.getChapters().size());
    }

    @Test
    void exportImportedBook() throws JAXBException {
        // given

        // when
        final Book importedBook = PlainTextImporter.importBook(SAMPLE_BOOK_PATH);
        XmlWriter.exportBookToXmlFile(importedBook, new File(OUTPUT_XML_PATH));

        // then
        FileAssert.assertEquals(new File(EXPECTED_XML_PATH), new File(OUTPUT_XML_PATH));
    }

    @Disabled
    @Test
    void testWithPunkRomana() throws JAXBException {
        final Book importedBook = PlainTextImporter.importBook(PUNK_ROMANA_PATH);
        XmlWriter.exportBookToXmlFile(importedBook, new File(OUTPUT_PUNK_ROMANA_PATH));
    }

    @Disabled
    @Test
    void testWithWinter() throws JAXBException {
        final Book importedBook = PlainTextImporter.importBook(WINTER_PATH);
        XmlWriter.exportBookToXmlFile(importedBook, new File(OUTPUT_WINTER_PATH));
    }

}