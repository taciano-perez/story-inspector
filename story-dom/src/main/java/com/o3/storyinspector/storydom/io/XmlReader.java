package com.o3.storyinspector.storydom.io;

import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.modifications.BookProcessor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Parses StoryDom in XML format to Java objects.
 */
public class XmlReader {

    public static Book readBookFromXmlStream(final Reader bookStream) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(Book.class);
        final Book book = (Book) context.createUnmarshaller().unmarshal(bookStream);
        return BookProcessor.applyModifications(book);
    }

    public static Book readBookFromXmlFile(final String filePath) throws JAXBException, IOException {
        return readBookFromXmlStream(new FileReader(filePath));
    }
}
