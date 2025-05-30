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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Data Access Object for book entities in the DB.
 */
public class BookDAO {

    final static Logger logger = LoggerFactory.getLogger(BookDAO.class);

    final static DateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private long id;
    private String title;
    private String author;
    private String userEmail;
    private int engineVersion;
    private String rawInput;
    private String storyDom;
    private String annotatedStoryDom;
    private boolean isReportAvailable;
    private String message;
    private int percentageComplete;
    private int remainingMinutes;
    private String createTime;
    private String annotationStartTime = null;
    private Boolean validatedByUser = false;

    public BookDAO(final long id, final String title, final String author, final boolean isReportAvailable, final String message, final int percentageComplete, final int remainingMinutes, final Timestamp annotationStartTime, final Boolean validatedByUser) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isReportAvailable = isReportAvailable;
        this.message = message;
        this.percentageComplete = percentageComplete;
        this.remainingMinutes = remainingMinutes;
        this.annotationStartTime = (annotationStartTime == null) ? null : DATE_TIME_FORMATTER.format(annotationStartTime.getTime());
        this.validatedByUser = validatedByUser;
    }

    public BookDAO(final long id, final String title, final String author, final String userEmail, final int engineVersion, final String rawInput, final String storyDom, final String annotatedStoryDom, final boolean isReportAvailable, final String message, final Timestamp annotationStartTime, final Boolean validatedByUser) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.userEmail = userEmail;
        this.engineVersion = engineVersion;
        this.rawInput = rawInput;
        this.storyDom = storyDom;
        this.annotatedStoryDom = annotatedStoryDom;
        this.isReportAvailable = isReportAvailable;
        this.message = message;
        this.annotationStartTime = (annotationStartTime == null) ? null : DATE_TIME_FORMATTER.format(annotationStartTime.getTime());
        this.validatedByUser = validatedByUser;
    }

    public BookDAO(final long id, final String title, final String author, final String userEmail, final int engineVersion, final boolean isReportAvailable, final String message, final int percentageComplete, final int remainingMinutes, final Timestamp createTime, final Timestamp annotationStartTime, final Boolean validatedByUser) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.userEmail = userEmail;
        this.engineVersion = engineVersion;
        this.isReportAvailable = isReportAvailable;
        this.message = message;
        this.percentageComplete = percentageComplete;
        this.remainingMinutes = remainingMinutes;
        this.createTime = DATE_TIME_FORMATTER.format(createTime.getTime());
        this.annotationStartTime = (annotationStartTime == null) ? null : DATE_TIME_FORMATTER.format(annotationStartTime.getTime());
        this.validatedByUser = validatedByUser;
    }

    public BookDAO(final long id, final String title, final String author, final String userEmail, final int engineVersion, final boolean isReportAvailable, final String message, final int percentageComplete, final int remainingMinutes) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.userEmail = userEmail;
        this.engineVersion = engineVersion;
        this.isReportAvailable = isReportAvailable;
        this.message = message;
        this.percentageComplete = percentageComplete;
        this.remainingMinutes = remainingMinutes;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public int getPercentageComplete() {
        return percentageComplete;
    }

    public int getRemainingMinutes() {
        return remainingMinutes;
    }

    public void setPercentageComplete(int percentageComplete) {
        this.percentageComplete = percentageComplete;
    }

    public void setRemainingMinutes(int remainingMinutes) {
        this.remainingMinutes = remainingMinutes;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAnnotationStartTime() {
        return annotationStartTime;
    }

    public void setAnnotationStartTime(String annotationStartTime) {
        this.annotationStartTime = annotationStartTime;
    }

    public Boolean getValidatedByUser() {
        return validatedByUser;
    }

    public void setValidatedByUser(Boolean validatedByUser) {
        this.validatedByUser = validatedByUser;
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

    public static void deleteBook(final JdbcTemplate db, final BookDAO book) {
        final String sql = "DELETE FROM books WHERE book_id = ?";
        final Object[] params = {book.getId()};
        final int[] types = {Types.INTEGER};
        final int deletedRowCount = db.update(sql, params, types);
        if (deletedRowCount != 1) {
            logger.error("Unexpected error when deleting book. Book id: " + book.getId() + ", deleted row count: " + deletedRowCount);
        }
    }

    public static void updateBookProgress(final JdbcTemplate db, final int percentageComplete, final int remainingMinutes, final long bookId) {
        final String sql = "UPDATE books SET percent_complete = ?, remain_mins = ? WHERE book_id = ?";
        final Object[] params = {percentageComplete, remainingMinutes, bookId};
        final int[] types = {Types.INTEGER, Types.INTEGER, Types.INTEGER};
        final int updatedRowCount = db.update(sql, params, types);
        if (updatedRowCount != 1) {
            logger.error("Unexpected error when annotating book. Book id: " + bookId + ", updated row count: " + updatedRowCount);
        }
    }

    public static void updateBookAnnotationStartTime(final JdbcTemplate db, final Timestamp annotationStartTime, final Long bookId) {
        final String sql = "UPDATE books SET annotation_start_time = ? WHERE book_id = ?";
        final Object[] params = {annotationStartTime, bookId};
        final int[] types = {Types.TIMESTAMP, Types.INTEGER};
        final int updatedRowCount = db.update(sql, params, types);
        if (updatedRowCount != 1) {
            logger.error("Unexpected error when updating annotation start time. Book id: " + bookId + ", updated row count: " + updatedRowCount);
        }
    }
    public static void updateBookValidation(final JdbcTemplate db, final Boolean validBook, final Long bookId) {
        final String sql = "UPDATE books SET validated_by_user = ? WHERE book_id = ?";
        final Object[] params = {validBook, bookId};
        final int[] types = {Types.BOOLEAN, Types.INTEGER};
        final int updatedRowCount = db.update(sql, params, types);
        if (updatedRowCount != 1) {
            logger.error("Unexpected error when updating book validation. Book id: " + bookId + ", updated row count: " + updatedRowCount);
        }
    }

    public static void updateBook(final JdbcTemplate db, final String annotatedBookAsString, final Long bookId) {
        final String sql = "UPDATE books SET annotated_storydom = ?, is_report_available=TRUE WHERE book_id = ?";
        final Object[] params = {annotatedBookAsString, bookId};
        final int[] types = {Types.LONGNVARCHAR, Types.INTEGER};
        final int updatedRowCount = db.update(sql, params, types);
        if (updatedRowCount != 1) {
            logger.error("Unexpected error when annotating book. Book id: " + bookId + ", updated row count: " + updatedRowCount);
        }
    }

    public static void updateBook(final JdbcTemplate db, final String annotatedBookAsString, final Date annotationCompleteTime, final String bookId) {
        final String sql = "UPDATE books SET annotated_storydom = ?, annotation_complete_time = ?, engine_version = ?, is_report_available=TRUE WHERE book_id = ?";
        final Object[] params = {annotatedBookAsString, annotationCompleteTime, ProcessBookApi.ENGINE_VERSION, bookId};
        final int[] types = {Types.LONGNVARCHAR, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER};
        final int updatedRowCount = db.update(sql, params, types);
        if (updatedRowCount != 1) {
            logger.error("Unexpected error when annotating book. Book id: " + bookId + ", updated row count: " + updatedRowCount);
        }
    }

    /**
     * Returns ALL books (irrespective of owner).
     * @param db JDBC template
     * @return all books.
     */
    public static List<BookDAO> findAll(final JdbcTemplate db) {
        return db.query("SELECT book_id, title, author, user_email, engine_version, is_report_available, percent_complete, remain_mins, message, create_time, annotation_start_time, validated_by_user FROM books",
                (rs, rowNum) ->
                        new BookDAO(rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getString("user_email"),
                                rs.getInt("engine_version"),
                                rs.getBoolean("is_report_available"),
                                rs.getString("message"),
                                rs.getInt("percent_complete"),
                                rs.getInt("remain_mins"),
                                rs.getTimestamp("create_time"),
                                rs.getTimestamp("annotation_start_time"),
                                rs.getBoolean("validated_by_user")));
    }

    public static List<BookDAO> findAll(final JdbcTemplate db, final String userId) {
        return db.query("SELECT book_id, title, author, is_report_available, percent_complete, remain_mins, message, annotation_start_time, validated_by_user FROM books WHERE user_id = ?",
                new Object[]{userId},
                (rs, rowNum) ->
                        new BookDAO(rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getBoolean("is_report_available"),
                                rs.getString("message"),
                                rs.getInt("percent_complete"),
                                rs.getInt("remain_mins"),
                                rs.getTimestamp("annotation_start_time"),
                                rs.getBoolean("validated_by_user")));
    }


    public static BookDAO findByBookId(final Long bookId, final JdbcTemplate db) throws EmptyResultDataAccessException {
        return db.queryForObject("SELECT book_id, title, author, user_email, engine_version, raw_input, storydom, annotated_storydom, is_report_available, message, annotation_start_time, validated_by_user FROM books WHERE book_id = ?",
                new Object[]{bookId},
                (rs, rowNum) ->
                        new BookDAO(rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getString("user_email"),
                                rs.getInt("engine_version"),
                                rs.getString("raw_input"),
                                rs.getString("storydom"),
                                rs.getString("annotated_storydom"),
                                rs.getBoolean("is_report_available"),
                                rs.getString("message"),
                                rs.getTimestamp("annotation_start_time"),
                                rs.getBoolean("validated_by_user")));
    }
}