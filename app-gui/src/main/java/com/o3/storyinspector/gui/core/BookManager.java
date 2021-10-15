package com.o3.storyinspector.gui.core;

import com.o3.storyinspector.annotation.AnnotationEngine;
import com.o3.storyinspector.annotation.BookProcessingStatusListener;
import com.o3.storyinspector.bookimporter.plaintext.PlainTextImporter;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import com.o3.storyinspector.storydom.io.XmlReader;
import com.o3.storyinspector.storydom.io.XmlWriter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BookManager {

    public static String STORYDOM_EXTENSION = "storydom";

    static String[] STORYDOM_EXTENSIONS = { STORYDOM_EXTENSION };

    static final Logger LOGGER = LoggerFactory.getLogger(BookManager.class);

    static List<BookEventListener> eventListeners = new ArrayList<>();

    public static void registerEventListener(final BookEventListener listener) {
        eventListeners.add(listener);
    }

    public static void fireBookEvent(final BookEvent event) {
        eventListeners.stream().forEach(l -> l.handleEvent(event));
    }

    public static List<Chapter> readChapterList(final File bookFile) {
        try {
            final String xml = BookManager.importStorydomFromFile(bookFile);
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

    public static String importStorydomFromFile(final File file, final String title, final String author) {
        final Book importedBook = PlainTextImporter.importBookFromFile(file.getAbsolutePath());
        importedBook.setTitle(title);
        importedBook.setAuthor(author);
        try {
            return XmlWriter.exportBookToString(importedBook);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static String annotateStorydom(final String storydom, final BookProcessingStatusListener statusListener) throws JAXBException {
        final StringReader storydomReader = new StringReader(storydom);
        final Book annotatedBook = AnnotationEngine.annotateBook(storydomReader, statusListener);
        fireBookEvent(new BookEvent(BookEvent.BOOK_ADDED, annotatedBook));
        return XmlWriter.exportBookToString(annotatedBook);
    }

    public static List<Book> getAllBooks() throws IOException, JAXBException {
        List<Book> books = new ArrayList<>();
        final Collection<File> files = FileUtils.listFiles(new File(getBookLibraryDir()), STORYDOM_EXTENSIONS, false);
        LOGGER.info("Reading dir: " + getBookLibraryDir());
        for (File file : files) {
            LOGGER.info("Found file: " + file.getName());
            final Book book = XmlReader.readBookFromXmlStream(new FileReader(file));
            books.add(book);
        }
        return books;
    }

    public static String getBookLibraryDir() throws IOException {
        return new java.io.File(".").getCanonicalPath();
    }

}
