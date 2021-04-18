package com.o3.storyinspector.bookimporter.msword;

import com.o3.storyinspector.bookimporter.plaintext.PlainTextImporter;
import com.o3.storyinspector.storydom.Book;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Imports .DOC (MS Word proprietary format), MS Word standard prior to 2007.
 */
public class DocImporter {

    private static final Logger LOG = LoggerFactory.getLogger(DocImporter.class);

    public static Book importBookFromFile(final String path) {
        LOG.debug("Importing DOC book: " + path);
        try {
            final FileInputStream fileInputStream = new FileInputStream(path);
            final String docxContentAsString = getDocContentAsString(fileInputStream);
            fileInputStream.close();
            final StringReader stringReader = new StringReader(docxContentAsString);

            return PlainTextImporter.importBookFromReader(path, stringReader);
        } catch (Exception e) {
            LOG.error("Error while importing DOC document, message: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static String getDocContentAsString(final InputStream inputStream) {
        final StringBuilder fullTextBuilder = new StringBuilder();
        try {
            final HWPFDocument document = new HWPFDocument(inputStream);
            final WordExtractor extractor = new WordExtractor(document);
            final String[] fileData = extractor.getParagraphText();
            for (String datum : fileData) {
                if (datum != null) {
                    final String fileDatum = datum;
                    fullTextBuilder.append(fileDatum).append(" ");
                }
            }
        } catch (final Exception e){
            LOG.error("Error while importing DOC document (getDocContentAsString), message: " + e.getMessage());
            e.printStackTrace();
        }
        return fullTextBuilder.toString();
    }

}
