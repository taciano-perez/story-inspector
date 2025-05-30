package com.o3.storyinspector.api;

import com.o3.storyinspector.api.task.AnnotateBookTask;
import com.o3.storyinspector.db.BookDAO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ProcessBookApiTest {

    private static final String API_ROOT = "http://localhost:8081/api/process-book";

    private static final String API_CREATE_DOM = API_ROOT + "/create-dom";

    private static final String API_PROCESS_BOOK = API_ROOT + "/process-book";

    private static final String API_REPROCESS_BOOK = API_ROOT + "/reprocess-book";

    private static final String INPUT_PLAINTEXT_BOOK = "Chapter 1: A Startling Start.\n" +
            "This is an example chapter wherein wondrous things would be expected by its eager author.\n" +
            "            \n" +
            "Chapter 2: The Unexciting Aftermath.\n" +
            "This is another example chapter, but the action seems to unfold slower than expected. \n";

    private static final String EXPECTED_STORYDOM = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Book title=\"Example Book\">\n" +
            "    <Chapter id=\"1\" title=\"Chapter 1: A Startling Start.\">\n" +
            "        <Block>\n" +
            "            <Body>This is an example chapter wherein wondrous things would be expected by its eager author.</Body>\n" +
            "        </Block>\n" +
            "    </Chapter>\n" +
            "    <Chapter id=\"2\" title=\"Chapter 2: The Unexciting Aftermath.\">\n" +
            "        <Block>\n" +
            "            <Body>This is another example chapter, but the action seems to unfold slower than expected.</Body>\n" +
            "        </Block>\n" +
            "    </Chapter>\n" +
            "</Book>\n";

    private static final String EXPECTED_ANNOTATED_STORYDOM = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Book title=\"Example Book\">\n" +
            "    <Chapter id=\"1\" title=\"Chapter 1: A Startling Start.\">\n" +
            "        <Metadata wordCount=\"15\" fkGrade=\"9.92666666666667\">\n" +
            "            <Locations/>\n" +
            "            <Characters/>\n" +
            "        </Metadata>\n" +
            "        <Block id=\"1#1\" wordCount=\"15\" sentimentScore=\"-0.06\" fkGrade=\"9.92666666666667\">\n" +
            "            <Emotion type=\"anger\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"anticipation\" score=\"0.08693333333333333\"/>\n" +
            "            <Emotion type=\"disgust\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"fear\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"sadness\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"surprise\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"trust\" score=\"0.0672\"/>\n" +
            "            <Body>This is an example chapter wherein wondrous things would be expected by its eager author.</Body>\n" +
            "        </Block>\n" +
            "    </Chapter>\n" +
            "    <Chapter id=\"2\" title=\"Chapter 2: The Unexciting Aftermath.\">\n" +
            "        <Metadata wordCount=\"14\" fkGrade=\"11.784285714285716\">\n" +
            "            <Locations/>\n" +
            "            <Characters/>\n" +
            "        </Metadata>\n" +
            "        <Block id=\"2#1\" wordCount=\"14\" sentimentScore=\"-0.056\" fkGrade=\"11.784285714285716\">\n" +
            "            <Emotion type=\"anger\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"anticipation\" score=\"0.07978571428571428\"/>\n" +
            "            <Emotion type=\"disgust\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"fear\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"sadness\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"surprise\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"trust\" score=\"0.0\"/>\n" +
            "            <Body>This is another example chapter, but the action seems to unfold slower than expected.</Body>\n" +
            "        </Block>\n" +
            "    </Chapter>\n" +
            "</Book>";

    private static final String USER_ID = "999999999999999999999"; // Use different user ID to avoid conflicts with data.sql

    private static final String USER_EMAIL = "contact@storyinspector.com";

    @Autowired
    private JdbcTemplate db;

    @BeforeEach
    void setUp() {
        // Reset auto-increment sequence to start from a unique time-based number to avoid conflicts
        long uniqueId = 20000 + (System.currentTimeMillis() % 1000000);
        db.execute("ALTER TABLE books ALTER COLUMN book_id RESTART WITH " + uniqueId);
    }

    @Test
    void whenProcessBook_CreateDom_noBookExists() {
        // given empty db

        // when
        final long bookId = 123L;
        final Response response = RestAssured.given()
                .param("ID", Long.toString(bookId))
                .post(API_CREATE_DOM + "/id_token");

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Book ID does not exist.", response.getBody().prettyPrint());
    }

    @Test
    void whenProcessBook_CreateDom_thenOK() {
        // given
        final long bookId = BookDAO.saveBook(db, USER_ID, USER_EMAIL, "Example Book", "Example Author", INPUT_PLAINTEXT_BOOK, null, null, null);

        // when
        final Response response = RestAssured.given()
                .param("ID", Long.toString(bookId))
                .post(API_CREATE_DOM + "/id_token");

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        final BookDAO book = BookDAO.findByBookId(bookId, db);
        assertEquals(EXPECTED_STORYDOM, book.getStoryDom());
    }

    @Test
    void whenProcessBook_AnnotateBook_thenOK() {
        // given
        final Long bookId = BookDAO.saveBook(db, USER_ID, USER_EMAIL, "Example Book", "Example Author", INPUT_PLAINTEXT_BOOK, null, null, null);
        final Response givenResponse =
                RestAssured.given()
                        .param("ID", bookId.toString())
                        .post(API_CREATE_DOM + "/id_token");
        assertEquals(HttpStatus.OK.value(), givenResponse.getStatusCode());
        final JavaMailSender mailSenderMock = mock(JavaMailSender.class);
        when(mailSenderMock.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        // when
        final AnnotateBookTask annotateBookTask = new AnnotateBookTask(db, bookId, mailSenderMock);
        annotateBookTask.run();

        // then
        final BookDAO book = BookDAO.findByBookId(bookId, db);
        assertEquals(EXPECTED_ANNOTATED_STORYDOM.strip(), book.getAnnotatedStoryDom().strip());
        assertTrue(book.isReportAvailable());
        verify(mailSenderMock, times(1)).send((MimeMessage) any());
    }

    @Test
    void whenProcessBook_ReprocessBook() {
        // given
        final long bookId = BookDAO.saveBook(db, USER_ID, USER_EMAIL, "Example Book", "Example Author", INPUT_PLAINTEXT_BOOK, null, null, null);

        // when
        final Response response = RestAssured.given()
                .post(API_REPROCESS_BOOK + "/" + bookId + "/id_token");

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

    @Test
    void whenProcessBook_ProcessBook() {
        // given
        final long bookId = BookDAO.saveBook(db, USER_ID, USER_EMAIL, "Example Book", "Example Author", INPUT_PLAINTEXT_BOOK, null, null, null);

        // when
        final Response response = RestAssured.given()
                .param("ID", Long.toString(bookId))
                .param("id_token", "")
                .post(API_PROCESS_BOOK);

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }
}