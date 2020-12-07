package com.o3.storyinspector.storydom.io;

import com.o3.storyinspector.storydom.Book;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.StringWriter;

public class XmlWriter {

    public static void exportBookToXmlFile(final Book book, File writeTo) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(Book.class);
        final Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(book, writeTo);
    }

    public static String exportBookToString(final Book book) throws JAXBException {
        final StringWriter writer = new StringWriter();
        final JAXBContext context = JAXBContext.newInstance(Book.class);
        final Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(book, writer);
        return writer.toString();
    }

}
