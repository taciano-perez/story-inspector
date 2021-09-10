package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
import com.o3.storyinspector.domain.Blocks;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.StringReader;

@RestController
@RequestMapping("/api/blocks")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BlockApi {

    final Logger logger = LoggerFactory.getLogger(BlockApi.class);

    @Autowired
    private JdbcTemplate db;

    @GetMapping("/list/{bookId}")
    public Blocks findAllByBook(@PathVariable final Long bookId) {
        logger.trace("LIST ALL BLOCKS bookId: " + bookId);
        final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
        try {
            final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));
            return Blocks.buildBlocks(book, bookDAO.getTitle(), bookDAO.getAuthor());
        } catch (final Exception e) {
            final String errMsg = "Unexpected error when listing book blocks. Book bookId: " +
                    bookId + ", Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            e.printStackTrace();
            return null;
        }
    }


}
