package com.o3.storyinspector.api;

import com.o3.storyinspector.api.task.AnnotateBookTask;
import com.o3.storyinspector.api.user.GoogleId;
import com.o3.storyinspector.api.user.UserInfo;
import com.o3.storyinspector.bookimporter.plaintext.PlainTextImporter;
import com.o3.storyinspector.db.BookDAO;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.io.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.sql.Types;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@RestController
@RequestMapping("/api/process-book")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProcessBookApi {

    final static Logger logger = LoggerFactory.getLogger(ProcessBookApi.class);

    public static final int ENGINE_VERSION = 4;

    @Autowired
    private JdbcTemplate db;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private GoogleId userValidator;

    @RequestMapping(value = "/process-book", method = RequestMethod.POST)
    public void processBook(@RequestParam("ID") Long bookId, @RequestParam("id_token") final String idToken) {
        logger.trace(String.format("PROCESS BOOK ID - %s", bookId));

        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        if (!user.isAdmin()) {
            final BookDAO book = BookDAO.findByBookId(bookId, db);
            user.emailMatches(book.getUserEmail());
        }

        BookDAO.updateBookValidation(db, true, bookId);

        taskScheduler.execute(new AnnotateBookTask(db, bookId, emailSender));
        logger.trace("PROCESS BOOK ID - BOOK SCHEDULED FOR ANNOTATION");
    }

    @GetMapping("/admin/task-queue/{userId}")
    public Long adminQueryTaskQueue(@PathVariable("userId") final String userId, @RequestParam("id_token") final String idToken) {
        logger.trace("ADMIN QUERY TASK QUEUE userId: " + userId);
        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        user.failIfNotAdmin();
        final ScheduledThreadPoolExecutor executor = taskScheduler.getScheduledThreadPoolExecutor();
        return executor.getTaskCount() - executor.getCompletedTaskCount();
    }

    @RequestMapping(value = "/reprocess-book/{bookId}/{id_token}", method = RequestMethod.POST)
    public void reprocessBook(@PathVariable("bookId") Long bookId, @PathVariable("id_token") final String idToken) {
        logger.trace(String.format("REPROCESS BOOK ID - %s", bookId));
        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        user.failIfNotAdmin();
        taskScheduler.execute(new AnnotateBookTask(db, bookId, null));
        logger.trace("PROCESS BOOK ID - BOOK SCHEDULED FOR ANNOTATION");
    }

    @RequestMapping(value = "/create-dom/{id_token}", method = RequestMethod.POST)
    public ResponseEntity<Object> createDom(@RequestParam("ID") Long bookId,
                                            @PathVariable("id_token") final String idToken) {
        logger.trace(String.format("CREATE DOM ID - %s", bookId));
        final UserInfo user = userValidator.retrieveUserInfo(idToken);

        try {
            final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
            if (!user.isAdmin()) user.emailMatches(bookDAO.getUserEmail());

            final Book importedBook =
                    PlainTextImporter.importBookFromReader(bookDAO.getTitle(), new StringReader(bookDAO.getRawInput()));
            final String importedBookAsString;
            try {
                importedBookAsString = XmlWriter.exportBookToString(importedBook);
            } catch (JAXBException e) {
                logger.error(e.getLocalizedMessage());
                e.printStackTrace();
                return new ResponseEntity<>("Unexpected error when creating storydom. Error: " +
                        e.getLocalizedMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

            final String sql = "UPDATE books SET storydom = ? WHERE book_id = ?";
            final Object[] params = {importedBookAsString, bookId.toString()};
            final int[] types = {Types.CLOB, Types.INTEGER};
            final int updatedRowCount = db.update(sql, params, types);
            if (updatedRowCount != 1) {
                final String errMsg = "Unexpected error when importing book. Book id: " +
                        bookId + ", updated row count: " + updatedRowCount;
                logger.error(errMsg);
                return new ResponseEntity<>(errMsg,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException erdae) {
            logger.error(erdae.getLocalizedMessage());
            erdae.printStackTrace();
            return new ResponseEntity<>("Book ID does not exist.",
                    HttpStatus.BAD_REQUEST);
        }
    }

}
