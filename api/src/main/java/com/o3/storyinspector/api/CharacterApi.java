package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
import com.o3.storyinspector.domain.BookStructure;
import com.o3.storyinspector.domain.Characters;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.StringReader;

@RestController
@RequestMapping("/api/character")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CharacterApi {
    final Logger logger = LoggerFactory.getLogger(CharacterApi.class);

    @Autowired
    private JdbcTemplate db;

    @GetMapping("/{bookId}")
    public Characters one(@PathVariable final Long bookId) {
        logger.trace("CHARACTERS BOOK ID=[" + bookId + "]");
        final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
        Characters characters;
        try {
            final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));
            characters = Characters.buildFromBook(book);
        } catch (final Exception e) {
            final String errMsg = "Unexpected error when building book structure report. Book bookId: " +
                    bookId + "Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            return null;
        }

        return characters;
    }


}
