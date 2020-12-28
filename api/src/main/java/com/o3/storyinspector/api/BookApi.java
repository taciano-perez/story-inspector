package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

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
    public Map<String, List<BookDAO>> findAllById(@RequestParam("userId") final String userId) {
        logger.trace("QUERYING ALL BOOKS userId=" + userId);
        final List<BookDAO> books = BookDAO.findAll(db, userId);
        bookList.addAll(books);
        return Collections.singletonMap("books", books);
    }

    @GetMapping("/{id}")
    public BookDAO one(@PathVariable final Long id) {
        logger.trace("QUERYING BOOK ID=[" + id + "]");
        final BookDAO book = BookDAO.findByBookId(id, db);
        // we don't need to send these over the wire
        book.setRawInput("");
        book.setStoryDom("");
        return book;
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Long> deleteBook(@PathVariable final Long id) {
        logger.trace("DELETING BOOK: " + id);
        db.execute("DELETE FROM books WHERE book_id=" + id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}