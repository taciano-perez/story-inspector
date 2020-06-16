package com.o3.storyinspector.storydom.io;

import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlReaderTest {

    private static String INPUT_XML_PATH = "./target/test-classes/expected-test-story-dom-exporter.xml";

    @org.junit.jupiter.api.Test
    void testImportBookFromXmlFile() throws JAXBException, IOException {
        // given
        final Book expectedBook = dummyBook();

        // when
        final Book importedBook = XmlReader.readBookFromXmlFile(INPUT_XML_PATH);

        // then
        assertEquals(expectedBook.getTitle(), importedBook.getTitle());
        final Chapter expectedChapter1 = expectedBook.getChapters().get(0);
        final Chapter importedChapter1 = importedBook.getChapters().get(0);
        assertEquals(expectedChapter1.getTitle(), importedChapter1.getTitle());
        assertEquals(expectedChapter1.getBody(), importedChapter1.getBody());
    }

    private Book dummyBook() {
        Book book = new Book();
        Chapter chapter1 = new Chapter();
        chapter1.setTitle("Chapter 1 - The first test");
        chapter1.setBody("This is the body of chapter 1.");
        book.getChapters().add(chapter1);
        Chapter chapter2 = new Chapter();
        chapter2.setTitle("Chapter 2 - The test continues");
        chapter2.setBody("This is the body of chapter 2.");
        book.getChapters().add(chapter2);
        return book;
    }
}