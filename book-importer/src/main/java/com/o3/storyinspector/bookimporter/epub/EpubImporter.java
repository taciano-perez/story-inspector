package com.o3.storyinspector.bookimporter.epub;

import com.o3.storyinspector.storydom.Chapter;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: implement this class using ChapterTokenizer
 */
public class EpubImporter {

    public static com.o3.storyinspector.storydom.Book importBook(final String path) throws IOException {
        com.o3.storyinspector.storydom.Book storyDomBook = new com.o3.storyinspector.storydom.Book();
        // read epub file
        EpubReader epubReader = new EpubReader();
        Book book = epubReader.readEpub(new FileInputStream(path));

        // book object
        List<String> titles = book.getMetadata().getTitles();
        storyDomBook.setTitle(titles.isEmpty() ? "book has no title" : titles.get(0));

        // chapter objects
        final List<SpineReference> spineReferences = book.getSpine().getSpineReferences();
        final List<Chapter> chapters = spineReferences.stream().map(ref -> {
            final Chapter ch = new Chapter();
            final Resource res = ref.getResource();
            final String title = ref.getResource().getTitle();
            ch.setTitle(title == null ? "" : title);
            try {
                ch.getBlocks().get(0).setBody(new String(res.getData(), Charset.forName(res.getInputEncoding())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ch;
        }).collect(Collectors.toList());
        storyDomBook.getChapters().addAll(chapters);

        // TODO: continue from here

        return storyDomBook;
    }
}
