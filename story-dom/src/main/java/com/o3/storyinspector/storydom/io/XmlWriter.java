package com.o3.storyinspector.storydom.io;

import com.o3.storyinspector.storydom.Book;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

public class XmlWriter {

    public static void exportBookToXmlFile(final Book book, File writeTo) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Book.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(book, writeTo);
    }
}
