package com.o3.storyinspector.bookimporter.msword;

import com.o3.storyinspector.bookimporter.plaintext.PlainTextImporter;
import com.o3.storyinspector.storydom.Book;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

/**
 * Imports .DOCX (OpenDoc) format. MS Word format starting 2007.
 */
public class DocxImporter {

    private static final Logger LOG = LoggerFactory.getLogger(DocxImporter.class);

    public static Book importBookFromFile(final String path) {
        LOG.debug("Importing DOCX book: " + path);
        try {
            final FileInputStream fileInputStream = new FileInputStream(path);
            final String docxContentAsString = getDocxContentAsString(fileInputStream);
            fileInputStream.close();
            final StringReader stringReader = new StringReader(docxContentAsString);

            return PlainTextImporter.importBookFromReader(path, stringReader);
        } catch (Exception e) {
            LOG.error("Error while importing DOCX document, message: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static String getDocxContentAsString(final InputStream inputStream) {
        final StringBuilder fullTextBuilder = new StringBuilder();
        try {
            final WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);
            final MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();
            final String textNodesXPath = "//w:t";
            final List<Object> textNodes= mainDocumentPart.getJAXBNodesViaXPath(textNodesXPath, true);
            for (final Object obj : textNodes) {
                final Text text = (Text) ((JAXBElement) obj).getValue();
                final String textValue = text.getValue();
                fullTextBuilder.append(textValue).append(" ");
            }
        } catch (Exception e) {
            LOG.error("Error while importing DOCX document (getDocxContentAsString), message: " + e.getMessage());
            e.printStackTrace();
        }
        return fullTextBuilder.toString();
    }

}
