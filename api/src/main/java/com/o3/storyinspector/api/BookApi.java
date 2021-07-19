package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BookApi {

    final Logger logger = LoggerFactory.getLogger(BookApi.class);

    private static List<BookDAO> bookList = new ArrayList<>();

    @Autowired
    private JdbcTemplate db;

    @GetMapping("/list")
    @Transactional
    public Map<String, List<BookDAO>> findAllById(@RequestParam("userId") final String userId) throws SQLException {
        logger.trace("QUERYING ALL BOOKS userId: " + userId);
        BookDAO.dbTrace(db);
        final List<BookDAO> books = BookDAO.findAll(db, userId);
        bookList.addAll(books);
        return Collections.singletonMap("books", books);
    }

    @GetMapping("/{id}")
    @Transactional
    public BookDAO one(@PathVariable final Long id) {
        logger.trace("QUERYING BOOK ID: [" + id + "]");
        final BookDAO book = BookDAO.findByBookId(id, db);
        // we don't need to send these over the wire
        book.setRawInput("");
        book.setStoryDom("");
        return book;
    }

    @DeleteMapping(value = "/{id}")
    @Transactional
    public ResponseEntity<Long> deleteBook(@PathVariable final Long id) {
        logger.trace("DELETING BOOK: " + id);
        // FIXME: delegate query to BookDAO
        db.execute("DELETE FROM books WHERE book_id=" + id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/admin/list")
    @Transactional
    public Map<String, List<BookDAO>> adminFindAllById(@RequestParam("userId") final String userId) {
        logger.trace("ADMIN QUERYING ALL BOOKS userId: " + userId);
        if (ApplicationConfig.ADMIN_USER_ID.equals(userId)) {
            final List<BookDAO> books = BookDAO.findAll(db);
            bookList.addAll(books);
            return Collections.singletonMap("books", books);
        } else {
            return null;
        }
    }

    @GetMapping("/admin/{id}")
    @Transactional
    public BookDAO adminOne(@PathVariable final Long id) {
        logger.trace("ADMIN QUERYING BOOK ID: [" + id + "]");
        return BookDAO.findByBookId(id, db);
    }

}