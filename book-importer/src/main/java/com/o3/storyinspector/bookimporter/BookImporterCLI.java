package com.o3.storyinspector.bookimporter;

import com.o3.storyinspector.bookimporter.plaintext.PlainTextImporter;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.io.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;

@CommandLine.Command(
        name = "BookImporter",
        description = "Reads an input file (plain text) and creates a storydom file."
)
public class BookImporterCLI implements Runnable {

    public static final String TYPE_PLAINTEXT_FILE = "plain";

    private static final Logger LOG = LoggerFactory.getLogger(BookImporterCLI.class);

    @CommandLine.Option(names = {"-T", "--type"}, required = true)
    private String fileType;

    @CommandLine.Option(names = {"-I", "--input"}, required = true)
    private String inputPath;

    @CommandLine.Option(names = {"-O", "--output"}, required = true)
    private String outputPath;

    public static void main(String[] args) {
        CommandLine.run(new BookImporterCLI(), args);
    }

    @Override
    public void run() {
        LOG.info("Preparing to import book, type=[" + fileType + "], input=[" + inputPath + "], output=[" + outputPath + "]");
        if (TYPE_PLAINTEXT_FILE.equals(fileType.toLowerCase())) {
            try {
                final Book importedBook = PlainTextImporter.importBook(inputPath);
                XmlWriter.exportBookToXmlFile(importedBook, new File(outputPath));
            } catch (Exception e) {
                LOG.error("An error has occurred. Message: " + e.getLocalizedMessage());
            }
        } else {
            LOG.error("Unknown file type. Valid types: 'plain'.");
        }
    }

}
