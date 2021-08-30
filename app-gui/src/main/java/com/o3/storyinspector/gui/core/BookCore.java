package com.o3.storyinspector.gui.core;

import com.o3.storyinspector.annotation.AnnotationEngine;
import com.o3.storyinspector.bookimporter.plaintext.PlainTextImporter;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import com.o3.storyinspector.storydom.io.XmlReader;
import com.o3.storyinspector.storydom.io.XmlWriter;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class BookCore {

    public static List<Chapter> readChapterList(final File bookFile) {
        try {
            final String xml = BookCore.importStorydomFromFile(bookFile);
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(xml));
            return book.getChapters();
        } catch (JAXBException jaxbe) {
            jaxbe.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static String importStorydomFromFile(final File file) {
        final Book importedBook = PlainTextImporter.importBookFromFile(file.getAbsolutePath());
        try {
            return XmlWriter.exportBookToString(importedBook);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static String annotateStorydom(final String storydom) throws JAXBException {
        final StringReader storydomReader = new StringReader(storydom);
        final Book annotatedBook = AnnotationEngine.annotateBook(storydomReader, (percentageCompleted, minutesLeft) -> {
            System.out.println(percentageCompleted + "% complete, " + minutesLeft + "min(s) left");
        });
        return XmlWriter.exportBookToString(annotatedBook);
    }

}
