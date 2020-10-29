package com.o3.storyinspector.storydom.io;

import com.o3.storyinspector.storydom.Book;

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
        return (Book) context.createUnmarshaller()
                .unmarshal(bookStream);
    }

    public static Book readBookFromXmlFile(final String filePath) throws JAXBException, IOException {
        return readBookFromXmlStream(new FileReader(filePath));
    }
}
