package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
import com.o3.storyinspector.domain.BookStructure;
import com.o3.storyinspector.domain.Chapter;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Character;
import com.o3.storyinspector.storydom.Location;
import com.o3.storyinspector.storydom.Metadata;
import com.o3.storyinspector.storydom.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookstructure")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BookStructureApi {

    final Logger logger = LoggerFactory.getLogger(BookStructureApi.class);

    @Autowired
    private JdbcTemplate db;

    @GetMapping("/{bookId}")
    public BookStructure one(@PathVariable final Long bookId) {
        logger.trace("BOOK STRUCTURE BOOK ID=[" + bookId + "]");
        final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
        BookStructure bookStructure;
        try {
            final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));
            book.setAuthor(bookDAO.getAuthor());    // FIXME: parse this at the appropriate spot
            bookStructure = buildBookStructureFromBook(book);
        } catch (final Exception e) {
            final String errMsg = "Unexpected error when building book structure report. Book bookId: " +
                    bookId + "Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            return null;
        }

        return bookStructure;
    }

    private static BookStructure buildBookStructureFromBook(final Book book) {
        final List<Chapter> chapterList = new ArrayList<>();
        long bookWordcount = 0;
        long id = 1;
        for (final com.o3.storyinspector.storydom.Chapter chapter : book.getChapters()) {
            final Metadata chapterMetadata = chapter.getMetadata();
            final long chapterWordcount = Long.parseLong(chapterMetadata.getWordCount());
            bookWordcount += chapterWordcount;
            chapterList.add(new Chapter(id++,
                    chapter.getTitle(),
                    chapterWordcount,
                    chapterMetadata.getCharacters().getCharacters().stream().map(Character::getName).collect(Collectors.toList()),
                    chapterMetadata.getLocations().getLocations().stream().map(Location::getName).collect(Collectors.toList())));
        }
        return new BookStructure(
                book.getTitle(),
                book.getAuthor(),
                bookWordcount,
                chapterList
        );
    }

}
