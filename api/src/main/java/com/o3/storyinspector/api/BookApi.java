package com.o3.storyinspector.api;

import com.o3.storyinspector.api.user.ForbiddenException;
import com.o3.storyinspector.api.user.GoogleId;
import com.o3.storyinspector.api.user.UserInfo;
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
public class BookApi {

    final Logger logger = LoggerFactory.getLogger(BookApi.class);

    @Autowired
    private GoogleId userValidator;

    private static List<BookDAO> bookList = new ArrayList<>();

    @Autowired
    private JdbcTemplate db;

    @GetMapping("/list")
    public Map<String, List<BookDAO>> findAllByUser(@RequestParam("id_token") final String idToken) {
        logger.trace("QUERYING ALL BOOKS idToken: " + idToken);

        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        final String userId = user.getId();
        logger.trace("userId: " + userId);

        final List<BookDAO> books = BookDAO.findAll(db, userId);
        bookList.addAll(books);
        return Collections.singletonMap("books", books);
    }

    @GetMapping("/{id}")
    public BookDAO one(@PathVariable final Long id, @RequestParam("id_token") final String idToken) {
        logger.trace("QUERYING BOOK ID: [" + id + "]");
        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        final BookDAO book = BookDAO.findByBookId(id, db);

        if (user.emailMatches(book.getUserEmail()) || user.isAdmin()) {
            // we don't need to send these over the wire
            book.setRawInput("");
            book.setStoryDom("");
        }
        return book;
    }

    @DeleteMapping(value = "/{id}/{id_token}")
    public ResponseEntity<Long> deleteBook(@PathVariable final Long id, @PathVariable("id_token") final String idToken) {
        logger.trace("DELETING BOOK: " + id);
        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        final BookDAO book = BookDAO.findByBookId(id, db);
        if (user.emailMatches(book.getUserEmail()) || user.isAdmin()) {
            BookDAO.deleteBook(db, book);
            return new ResponseEntity<>(id, HttpStatus.OK);
        } else {
            throw new ForbiddenException();
        }
    }

    @GetMapping("/admin/list")
    public Map<String, List<BookDAO>> adminFindAllById(@RequestParam("userId") final String userId, @RequestParam("id_token") final String idToken) {
        logger.trace("ADMIN QUERYING ALL BOOKS userId: " + userId);
        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        user.failIfNotAdmin();

        final List<BookDAO> books = BookDAO.findAll(db);
        bookList.addAll(books);
        return Collections.singletonMap("books", books);
    }

    @GetMapping("/admin/{id}")
    public BookDAO adminOne(@PathVariable final Long id, @RequestParam("id_token") final String idToken) {
        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        user.failIfNotAdmin();

        logger.trace("ADMIN QUERYING BOOK ID: [" + id + "]");
        return BookDAO.findByBookId(id, db);
    }

}