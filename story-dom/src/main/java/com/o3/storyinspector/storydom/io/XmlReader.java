package com.o3.storyinspector.storydom.io;

import com.o3.storyinspector.storydom.Book;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileReader;
import java.io.IOException;

public class XmlReader {

    public static Book readBookFromXmlFile(final String filePath) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(Book.class);
        return (Book) context.createUnmarshaller()
                .unmarshal(new FileReader(filePath));
    }
}
