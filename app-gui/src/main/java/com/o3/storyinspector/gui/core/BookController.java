package com.o3.storyinspector.gui.core;

import com.o3.storyinspector.bookimporter.plaintext.PlainTextImporter;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.io.XmlWriter;

import javax.xml.bind.JAXBException;
import java.io.File;

public class BookController {

    public static String importStorydomFromFile(final File file) {
        final Book importedBook =
                PlainTextImporter.importBookFromFile(file.getAbsolutePath());
        try {
            return XmlWriter.exportBookToString(importedBook);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}
