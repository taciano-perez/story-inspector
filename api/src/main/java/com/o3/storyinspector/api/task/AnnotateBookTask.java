package com.o3.storyinspector.api.task;

import com.o3.storyinspector.annotation.AnnotationEngine;
import com.o3.storyinspector.api.util.EmailUtils;
import com.o3.storyinspector.db.BookDAO;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.io.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import java.io.StringReader;
import java.sql.Date;

public class AnnotateBookTask implements Runnable {

    final static Logger logger = LoggerFactory.getLogger(AnnotateBookTask.class);

    private JdbcTemplate db;

    private Long bookId;

    private JavaMailSender emailSender;

    public AnnotateBookTask(final JdbcTemplate db, final Long bookId, final JavaMailSender emailSender) {
        this.db = db;
        this.bookId = bookId;
        this.emailSender = emailSender;
    }

    @Override
    public void run() {
        logger.trace(String.format("ANNOTATE BOOK ID - %s", bookId));

        try {
            final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);

            String annotatedBookAsString = "";
            try {
                final Book annotatedBook = AnnotationEngine.annotateBook(new StringReader(bookDAO.getStoryDom()),
                        (double percentageCompleted, int minutesLeft) -> {this.updateStatus(percentageCompleted, minutesLeft, bookDAO, db);}
                        );
                annotatedBookAsString = XmlWriter.exportBookToString(annotatedBook);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
                e.printStackTrace();
                logger.error("Unexpected error when annotating storydom. Error: " + e.getLocalizedMessage());
            }

            BookDAO.updateBook(db, annotatedBookAsString, new Date(System.currentTimeMillis()), bookId.toString());

            try {
                EmailUtils.sendHtmlEmail(emailSender, bookDAO.getUserEmail(),
                        "StoryInspector analysis complete: " + bookDAO.getTitle(),
                        "<p>StoryInspector has finished analyzing the book \"" + bookDAO.getTitle() + "\" by " + bookDAO.getAuthor() + ".\n" +
                        "<p>Click <a href='http://www.storyinspector.com/report-structure.html?book_id=" + bookDAO.getId() + "'>here</a> to access the results." +
                        "<p>We hope you enjoy it. Thanks for using StoryInspector." +
                        "<br><img src=\"www.storyinspector.com/img/logo.png\" alt=\"Story Inspector\" width=\"56\" height=\"56\">");
            } catch (MessagingException e) {
                logger.error("Error sending mail to: " + bookDAO.getUserEmail() + " for book id: " + bookId);
                logger.error(e.getMessage());
                e.printStackTrace();
            }

        } catch (EmptyResultDataAccessException erdae) {
            erdae.printStackTrace();
            logger.error("Book ID does not exist.");
        }
        logger.trace(String.format("ANNOTATE BOOK COMPLETE ID - %s", bookId));
    }

    private void updateStatus(final double percentageCompleted, final int minutesLeft, final BookDAO bookDAO, final JdbcTemplate db) {
        int roundedPercentage = (int) Math.ceil(percentageCompleted * 100);
        logger.debug("PROGRESS UPDATE: " + roundedPercentage + "% completed, " + minutesLeft + " minute(s) left.");
        bookDAO.setPercentageComplete(roundedPercentage);
        bookDAO.setRemainingMinutes(minutesLeft);
        BookDAO.updateBookProgress(db, roundedPercentage, minutesLeft, bookDAO.getId());
    }

}
