package com.o3.storyinspector.storydom.io;

import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import junitx.framework.FileAssert;

import javax.xml.bind.JAXBException;
import java.io.File;

class XmlWriterTest {

    private static String OUTPUT_XML_PATH = "./target/test-story-dom-exporter.xml";
    private static String EXPECTED_XML_PATH = "./target/test-classes/expected-test-story-dom-exporter.xml";

    @org.junit.jupiter.api.Test
    void testExportBookToXmlFile() throws JAXBException {
        // given
        final Book book = new Book();
        final Chapter chapter1 = new Chapter();
        chapter1.setTitle("Chapter 1 - The first test");
        final Block block1 = new Block();
        block1.setWordCount("100");
        block1.setBody("This is the body of chapter 1.");
        chapter1.getBlocks().add(block1);
        book.getChapters().add(chapter1);
        final Chapter chapter2 = new Chapter();
        chapter2.setTitle("Chapter 2 - The test continues");
        final Block block2 = new Block();
        block2.setSentimentScore("-1");
        block2.setBody("This is the body of chapter 2.");
        chapter2.getBlocks().add(block2);
        book.getChapters().add(chapter2);

        // when
        XmlWriter.exportBookToXmlFile(book, new File(OUTPUT_XML_PATH));

        // then
        FileAssert.assertEquals(new File(EXPECTED_XML_PATH), new File(OUTPUT_XML_PATH));
    }
}