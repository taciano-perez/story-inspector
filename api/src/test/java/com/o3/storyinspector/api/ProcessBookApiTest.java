package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ProcessBookApiTest {

    private static final String API_ROOT = "http://localhost:8081/api/process-book";

    private static final String API_CREATE_DOM = API_ROOT + "/create-dom";

    private static final String API_ANNOTATE_BOOK = API_ROOT + "/annotate-book";

    private static final String API_PROCESS_BOOK = API_ROOT + "/process-book";

    private static final String INPUT_PLAINTEXT_BOOK = """
            Chapter 1: A Startling Start.
            This is an example chapter wherein wondrous things would be expected by its eager author.
                        
            Chapter 2: The Unexciting Aftermath.
            This is another example chapter, but the action seems to unfold slower than expected. 
            """;

    private static final String EXPECTED_STORYDOM = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Book title="Example Book">
                <Chapter title="Chapter 1">
                    <Block>
                        <Body>This is an example chapter wherein wondrous things would be expected by its eager author .</Body>
                    </Block>
                </Chapter>
                <Chapter title="Chapter 2">
                    <Block>
                        <Body>This is another example chapter , but the action seems to unfold slower than expected .</Body>
                    </Block>
                </Chapter>
            </Book>
            """;

    private static final String EXPECTED_ANNOTATED_STORYDOM = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Book title="Example Book">
                <Chapter title="Chapter 1">
                    <Metadata wordCount="16">
                        <Locations/>
                        <Characters/>
                    </Metadata>
                    <Block wordCount="16" sentimentScore="-0,0640">
                        <Emotion type="anger" score="0.0"/>
                        <Emotion type="anticipation" score="0.08693333333333333"/>
                        <Emotion type="disgust" score="0.0"/>
                        <Emotion type="fear" score="0.0"/>
                        <Emotion type="sadness" score="0.0"/>
                        <Emotion type="surprise" score="0.0"/>
                        <Emotion type="trust" score="0.0672"/>
                        <Body>This is an example chapter wherein wondrous things would be expected by its eager author .</Body>
                    </Block>
                </Chapter>
                <Chapter title="Chapter 2">
                    <Metadata wordCount="16">
                        <Locations/>
                        <Characters/>
                    </Metadata>
                    <Block wordCount="16" sentimentScore="-0,0640">
                        <Emotion type="anger" score="0.0"/>
                        <Emotion type="anticipation" score="0.07978571428571428"/>
                        <Emotion type="disgust" score="0.0"/>
                        <Emotion type="fear" score="0.0"/>
                        <Emotion type="sadness" score="0.0"/>
                        <Emotion type="surprise" score="0.0"/>
                        <Emotion type="trust" score="0.0"/>
                        <Body>This is another example chapter , but the action seems to unfold slower than expected .</Body>
                    </Block>
                </Chapter>
            </Book>
            """;

    private static final String USER_ID = "108700212624021084744";

    @Autowired
    private JdbcTemplate db;

    @Test
    void whenProcessBook_CreateDom_noBookExists() {
        // given empty db

        // when
        final long bookId = 123L;
        final Response response = RestAssured.given().param("ID", Long.toString(bookId)).post(API_CREATE_DOM);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Book ID does not exist.", response.getBody().prettyPrint());
    }

    @Test
    void whenProcessBook_CreateDom_thenOK() {
        // given
        final long bookId = BookDAO.saveBook(db, USER_ID, "Example Book", "Example Author", INPUT_PLAINTEXT_BOOK, null, null);

        // when
        final Response response = RestAssured.given()
                .param("ID", Long.toString(bookId))
                .post(API_CREATE_DOM);

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        final BookDAO book = BookDAO.findByBookId(bookId, db);
        assertEquals(EXPECTED_STORYDOM, book.getStoryDom());
    }

    @Disabled   // disabled to avoid dependency to emolex scores
    @Test
    void whenProcessBook_AnnotateBook_thenOK() {
        // given
        final Long bookId = BookDAO.saveBook(db, USER_ID, "Example Book", "Example Author", INPUT_PLAINTEXT_BOOK, null, null);
        final Response givenResponse =
                RestAssured.given()
                        .param("ID", bookId.toString())
                        .post(API_CREATE_DOM);
        assertEquals(HttpStatus.OK.value(), givenResponse.getStatusCode());

        // when
        final Response response = RestAssured.given()
                .param("ID", bookId.toString())
                .post(API_ANNOTATE_BOOK);

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        final BookDAO book = BookDAO.findByBookId(bookId, db);
        assertEquals(EXPECTED_ANNOTATED_STORYDOM, book.getAnnotatedStoryDom());
        assertTrue(book.isReportAvailable());
    }

    @Disabled   // disabled to avoid dependency to emolex scores
    @Test
    void whenProcessBook_ProcessBook() {
        // given
        final long bookId = BookDAO.saveBook(db, USER_ID, "Example Book", "Example Author", INPUT_PLAINTEXT_BOOK, null, null);

        // when
        final Response response = RestAssured.given()
                .param("ID", Long.toString(bookId))
                .post(API_PROCESS_BOOK);

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }
}