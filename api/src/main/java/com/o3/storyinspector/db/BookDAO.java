package com.o3.storyinspector.db;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.io.Reader;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * Data Access Object for book entities in the DB.
 */
public class BookDAO {
    private long id;
    private String title;
    private String author;
    private String rawInput;
    private String storyDom;
    private String annotatedStoryDom;
    private boolean isReportAvailable;
    private String message;

    public BookDAO(final long id, final String title, final String author, final boolean isReportAvailable, final String message) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isReportAvailable = isReportAvailable;
        this.message = message;
    }

    public BookDAO(final long id, final String title, final String author, final String rawInput, final String storyDom, final String annotatedStoryDom, final boolean isReportAvailable, final String message) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.rawInput = rawInput;
        this.storyDom = storyDom;
        this.annotatedStoryDom = annotatedStoryDom;
        this.isReportAvailable = isReportAvailable;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRawInput() {
        return rawInput;
    }

    public void setRawInput(String rawInput) {
        this.rawInput = rawInput;
    }

    public String getStoryDom() {
        return storyDom;
    }

    public void setStoryDom(String storyDom) {
        this.storyDom = storyDom;
    }

    public String getAnnotatedStoryDom() {
        return annotatedStoryDom;
    }

    public void setAnnotatedStoryDom(String annotatedStoryDom) {
        this.annotatedStoryDom = annotatedStoryDom;
    }

    public boolean isReportAvailable() {
        return isReportAvailable;
    }

    public void setReportAvailable(boolean reportAvailable) {
        isReportAvailable = reportAvailable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static long saveBook(final JdbcTemplate db, final String title, final String author, final String rawInput) {
        final String sql = "INSERT INTO books (title, author, raw_input) VALUES (?, ?, ?)";
        final KeyHolder holder = new GeneratedKeyHolder();
        db.update(connection -> {
            final PreparedStatement ps = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, author);
            final Reader reader = new StringReader(rawInput);
            ps.setClob(3, reader);
            return ps;
        }, holder);
        final Number bookId = holder.getKey();
        if (bookId != null) {
            return bookId.longValue();
        }
        throw new RuntimeException("No generated book id returned.");
    }

    public static List<BookDAO> findAll(final JdbcTemplate db) {
        return db.query("SELECT book_id, title, author, is_report_available, message FROM books",
                new Object[]{},
                (rs, rowNum) ->
                        new BookDAO(rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getBoolean("is_report_available"),
                                rs.getString("message")
                        ));
    }


    public static BookDAO findByBookId(final Long bookId, final JdbcTemplate db) throws EmptyResultDataAccessException {
        return db.queryForObject("SELECT book_id, title, author, raw_input, storydom, annotated_storydom, is_report_available, message FROM books WHERE book_id = ?",
                new Object[]{bookId},
                (rs, rowNum) ->
                        new BookDAO(rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getString("raw_input"),
                                rs.getString("storydom"),
                                rs.getString("annotated_storydom"),
                                rs.getBoolean("is_report_available"),
                                rs.getString("message")
                        ));
    }
}