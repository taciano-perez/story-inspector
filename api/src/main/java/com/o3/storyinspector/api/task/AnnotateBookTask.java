package com.o3.storyinspector.api.task;

import com.o3.storyinspector.annotation.AnnotationEngine;
import com.o3.storyinspector.db.BookDAO;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.io.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.StringReader;
import java.sql.Date;

public class AnnotateBookTask implements Runnable {

    final static Logger logger = LoggerFactory.getLogger(AnnotateBookTask.class);

    private JdbcTemplate db;

    private Long bookId;

    public AnnotateBookTask(final JdbcTemplate db, final Long bookId) {
        this.db = db;
        this.bookId = bookId;
    }

    @Override
    public void run() {
        logger.trace(String.format("ANNOTATE BOOK ID - %s", bookId));

        try {
            final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);

            String annotatedBookAsString = "";
            try {
                final Book annotatedBook = AnnotationEngine.annotateBook(new StringReader(bookDAO.getStoryDom()));
                annotatedBookAsString = XmlWriter.exportBookToString(annotatedBook);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
                e.printStackTrace();
                logger.error("Unexpected error when annotating storydom. Error: " + e.getLocalizedMessage());
            }

            BookDAO.updateBook(db, annotatedBookAsString, new Date(System.currentTimeMillis()), bookId.toString());

        } catch (EmptyResultDataAccessException erdae) {
            erdae.printStackTrace();
            logger.error("Book ID does not exist.");
        }
        logger.trace(String.format("ANNOTATE BOOK COMPLETE ID - %s", bookId));
    }
}
