package com.o3.storyinspector.api;

import com.o3.storyinspector.annotation.AnnotationEngine;
import com.o3.storyinspector.api.util.ApiUtils;
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
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.sql.Types;

@RestController
@RequestMapping("/api/process-book")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProcessBookApi {

    final Logger logger = LoggerFactory.getLogger(ProcessBookApi.class);

    @Autowired
    private JdbcTemplate db;

    @RequestMapping(value = "/process-book", method = RequestMethod.POST)
    public void processBook(@RequestParam("ID") Long bookId) {
        logger.trace(String.format("PROCESS BOOK ID - %s", bookId));

        final ResponseEntity<String> res1 = ApiUtils.callApiWithParameter(ApiUtils.API_CREATE_DOM, "ID", bookId.toString());
        if (res1.getStatusCode() != HttpStatus.OK) {
            logger.error("PROCESS BOOK ID - ERROR CREATING DOM " + res1.getBody());
            return;
        }
        logger.trace("PROCESS BOOK ID - CREATED DOM");

        final ResponseEntity<String> res2 = ApiUtils.callApiWithParameter(ApiUtils.API_ANNOTATE_BOOK, "ID", bookId.toString());
        if (res2.getStatusCode() != HttpStatus.OK) {
            logger.error("PROCESS BOOK ID - ERROR ANNOTATING BOOK " + res2.getBody());
            return;
        }
        logger.trace("PROCESS BOOK ID - ANNOTATED BOOK");
    }


    @RequestMapping(value = "/create-dom", method = RequestMethod.POST)
    public ResponseEntity<Object> createDom(@RequestParam("ID") Long bookId) {
        logger.trace(String.format("CREATE DOM ID - %s", bookId));

        try {
            final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);

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

    @RequestMapping(value = "/annotate-book", method = RequestMethod.POST)
    public ResponseEntity<Object> annotateBook(@RequestParam("ID") Long bookId) {
        logger.trace(String.format("ANNOTATE BOOK ID - %s", bookId));

        try {
            final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);

            String annotatedBookAsString;
            try {
                final Book annotatedBook = AnnotationEngine.annotateBook(new StringReader(bookDAO.getStoryDom()));
                annotatedBookAsString = XmlWriter.exportBookToString(annotatedBook);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
                e.printStackTrace();
                return new ResponseEntity<>("Unexpected error when annotating storydom. Error: " + e.getLocalizedMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

            final String sql = "UPDATE books SET annotated_storydom = ?, is_report_available=TRUE WHERE book_id = ?";
            final Object[] params = {annotatedBookAsString, bookId.toString()};
            final int[] types = {Types.CLOB, Types.INTEGER};
            final int updatedRowCount = db.update(sql, params, types);
            if (updatedRowCount != 1) {
                final String errMsg = "Unexpected error when annotating book. Book id: " + bookId + ", updated row count: " + updatedRowCount;
                logger.error(errMsg);
                return new ResponseEntity<>(errMsg, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException erdae) {
            erdae.printStackTrace();
            return new ResponseEntity<>("Book ID does not exist.", HttpStatus.BAD_REQUEST);
        }
    }

}
