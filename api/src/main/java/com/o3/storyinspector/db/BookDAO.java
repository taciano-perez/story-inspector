package com.o3.storyinspector.db;

import com.o3.storyinspector.api.ProcessBookApi;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.sql.*;
import java.util.List;

/**
 * Data Access Object for book entities in the DB.
 */
public class BookDAO {

    final static Logger logger = LoggerFactory.getLogger(BookDAO.class);

    private long id;
    private String title;
    private String author;
    private int engineVersion;
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

    public BookDAO(final long id, final String title, final String author, final int engineVersion, final String rawInput, final String storyDom, final String annotatedStoryDom, final boolean isReportAvailable, final String message) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.engineVersion = engineVersion;
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

    public int getEngineVersion() {
        return engineVersion;
    }

    public Book asBook() throws JAXBException {
        final Book book = XmlReader.readBookFromXmlStream(new StringReader(this.getAnnotatedStoryDom()));
        book.setAuthor(this.getAuthor());
        book.setTitle(this.getTitle());
        return book;
    }

    public static long saveBook(final JdbcTemplate db, final String userId, final String userEmail, final String title, final String author, final String rawInput, final String storyDom, final String annotatedStoryDom, final Timestamp annotationCompleteTime) {
        final String sql = "INSERT INTO books (user_id, user_email, title, author, engine_version, create_time, raw_input, storydom, annotated_storydom, annotation_complete_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        final KeyHolder holder = new GeneratedKeyHolder();
        db.update(connection -> {
            final PreparedStatement ps = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, userId);
            ps.setString(2, userEmail);
            ps.setString(3, title);
            ps.setString(4, author);
            ps.setInt(5, ProcessBookApi.ENGINE_VERSION);
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.setClob(7, new StringReader(rawInput));
            ps.setString(8, storyDom);
            ps.setString(9, annotatedStoryDom);
            ps.setTimestamp(10, annotationCompleteTime);
            return ps;
        }, holder);
        final Number bookId = holder.getKey();
        if (bookId != null) {
            return bookId.longValue();
        }
        throw new RuntimeException("No generated book id returned.");
    }

    public static void updateBook(final JdbcTemplate db, final String annotatedBookAsString, final Date annotationCompleteTime, final String bookId) {
        final String sql = "UPDATE books SET annotated_storydom = ?, annotation_complete_time = ?, is_report_available=TRUE WHERE book_id = ?";
        final Object[] params = {annotatedBookAsString, annotationCompleteTime, bookId};
        final int[] types = {Types.LONGNVARCHAR, Types.TIMESTAMP, Types.INTEGER};
        final int updatedRowCount = db.update(sql, params, types);
        if (updatedRowCount != 1) {
            logger.error("Unexpected error when annotating book. Book id: " + bookId + ", updated row count: " + updatedRowCount);
        }
    }

    public static List<BookDAO> findAll(final JdbcTemplate db, final String userId) {
        return db.query("SELECT book_id, title, author, is_report_available, message FROM books WHERE user_id = ?",
                new Object[]{userId},
                (rs, rowNum) ->
                        new BookDAO(rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getBoolean("is_report_available"),
                                rs.getString("message")));
    }


    public static BookDAO findByBookId(final Long bookId, final JdbcTemplate db) throws EmptyResultDataAccessException {
        return db.queryForObject("SELECT book_id, title, author, engine_version, raw_input, storydom, annotated_storydom, is_report_available, message FROM books WHERE book_id = ?",
                new Object[]{bookId},
                (rs, rowNum) ->
                        new BookDAO(rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getInt("engine_version"),
                                rs.getString("raw_input"),
                                rs.getString("storydom"),
                                rs.getString("annotated_storydom"),
                                rs.getBoolean("is_report_available"),
                                rs.getString("message")));
    }
}