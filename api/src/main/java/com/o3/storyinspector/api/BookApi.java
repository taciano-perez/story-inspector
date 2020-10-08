package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
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

    private static List<BookDAO> bookList = new ArrayList<>();

    @Autowired
    private JdbcTemplate db;

    @GetMapping
    public Map<String, List<BookDAO>> findAll() {
        final List<BookDAO> books = db.query("SELECT book_id, title, author, raw_input FROM books", new Object[]{}, (rs, rowNum) ->
                new BookDAO(rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("raw_input")));
        bookList.addAll(books);
        return Collections.singletonMap("books", books);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Long> deleteBook(@PathVariable Long id) {
        System.err.println("DELETING BOOK: " + id);
        db.execute("DELETE FROM books WHERE book_id=" + id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}