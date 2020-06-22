package com.o3.storyinspector.storydom.io;

import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlReaderTest {

    private static final String INPUT_XML_PATH = "./target/test-classes/expected-test-story-dom-exporter.xml";

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
        assertEquals(expectedChapter1.getBlocks().get(0).getBody(), importedChapter1.getBlocks().get(0).getBody());
    }

    private Book dummyBook() {
        final Book book = new Book();
        final Chapter chapter1 = new Chapter();
        chapter1.setTitle("Chapter 1 - The first test");
        final Block block1 = new Block();
        block1.setBody("This is the body of chapter 1.");
        chapter1.getBlocks().add(block1);
        book.getChapters().add(chapter1);
        final Chapter chapter2 = new Chapter();
        chapter2.setTitle("Chapter 2 - The test continues");
        final Block block2 = new Block();
        block2.setBody("This is the body of chapter 2.");
        chapter2.getBlocks().add(block2);
        book.getChapters().add(chapter2);
        return book;
    }
}