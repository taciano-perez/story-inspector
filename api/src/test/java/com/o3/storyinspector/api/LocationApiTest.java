package com.o3.storyinspector.api;

import com.dumbster.smtp.SimpleSmtpServer;
import com.o3.storyinspector.db.BookDAO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class LocationApiTest {

    private static final String API_ROOT = "http://localhost:8081/api/location";

    private static final String ANNOTATED_STORYDOM = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Book title=\"Example Book\">\n" +
            "    <Chapter id=\"1\" title=\"Chapter 1\">\n" +
            "        <Metadata wordCount=\"16\">\n" +
            "            <Locations>\n" +
            "                <Location name=\"London\" type=\"CITY\"/>\n" +
            "            </Locations>\n" +
            "            <Characters>\n" +
            "                <Character name=\"Holmes\"/>\n" +
            "            </Characters>\n" +
            "        </Metadata>\n" +
            "        <Block wordCount=\"16\" sentimentScore=\"-0.0640\">\n" +
            "            <Emotion type=\"anger\" score=\"0.00\"/>\n" +
            "            <Emotion type=\"anticipation\" score=\"0.08693333333333333\"/>\n" +
            "            <Emotion type=\"disgust\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"fear\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"sadness\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"surprise\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"trust\" score=\"0.0672\"/>\n" +
            "            <Body>This is an example chapter wherein wondrous things would be expected by its eager author .</Body>\n" +
            "        </Block>\n" +
            "    </Chapter>\n" +
            "    <Chapter id=\"2\" title=\"Chapter 2\">\n" +
            "        <Metadata wordCount=\"16\">\n" +
            "            <Locations>\n" +
            "                <Location name=\"Paris\" type=\"CITY\"/>\n" +
            "            </Locations>\n" +
            "            <Characters>\n" +
            "                <Character name=\"Watson\"/>\n" +
            "            </Characters>\n" +
            "        </Metadata>\n" +
            "        <Block wordCount=\"16\" sentimentScore=\"-0.0640\">\n" +
            "            <Emotion type=\"anger\" score=\"0.00\"/>\n" +
            "            <Emotion type=\"anticipation\" score=\"0.07978571428571428\"/>\n" +
            "            <Emotion type=\"disgust\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"fear\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"sadness\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"surprise\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"trust\" score=\"0.0\"/>\n" +
            "            <Body>This is another example chapter , but the action seems to unfold slower than expected .</Body>\n" +
            "        </Block>\n" +
            "    </Chapter>\n" +
            "</Book>\n";

    private final static String EXPECTED_STORYDOM_DELETE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Book title=\"Example Book\">\n" +
            "    <Chapter id=\"1\" title=\"Chapter 1\">\n" +
            "        <Metadata wordCount=\"16\">\n" +
            "            <Locations>\n" +
            "                <Location name=\"London\" type=\"CITY\"/>\n" +
            "            </Locations>\n" +
            "            <Characters>\n" +
            "                <Character name=\"Holmes\"/>\n" +
            "            </Characters>\n" +
            "        </Metadata>\n" +
            "        <Block wordCount=\"16\" sentimentScore=\"-0.0640\">\n" +
            "            <Emotion type=\"anger\" score=\"0.00\"/>\n" +
            "            <Emotion type=\"anticipation\" score=\"0.08693333333333333\"/>\n" +
            "            <Emotion type=\"disgust\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"fear\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"sadness\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"surprise\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"trust\" score=\"0.0672\"/>\n" +
            "            <Body>This is an example chapter wherein wondrous things would be expected by its eager author .</Body>\n" +
            "        </Block>\n" +
            "    </Chapter>\n" +
            "    <Chapter id=\"2\" title=\"Chapter 2\">\n" +
            "        <Metadata wordCount=\"16\">\n" +
            "            <Locations>\n" +
            "                <Location name=\"Paris\" type=\"CITY\"/>\n" +
            "            </Locations>\n" +
            "            <Characters>\n" +
            "                <Character name=\"Watson\"/>\n" +
            "            </Characters>\n" +
            "        </Metadata>\n" +
            "        <Block wordCount=\"16\" sentimentScore=\"-0.0640\">\n" +
            "            <Emotion type=\"anger\" score=\"0.00\"/>\n" +
            "            <Emotion type=\"anticipation\" score=\"0.07978571428571428\"/>\n" +
            "            <Emotion type=\"disgust\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"fear\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"sadness\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"surprise\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"trust\" score=\"0.0\"/>\n" +
            "            <Body>This is another example chapter , but the action seems to unfold slower than expected .</Body>\n" +
            "        </Block>\n" +
            "    </Chapter>\n" +
            "    <Modification entity=\"location\" transformation=\"remove\" name=\"London\"/>\n" +
            "</Book>\n";

    private final static String EXPECTED_JSON_GET = "{\"locations\":[" +
            "{\"name\":\"London\",\"chapters\":[1],\"totalPercentageOfChapters\":0.5}," +
            "{\"name\":\"Paris\",\"chapters\":[2],\"totalPercentageOfChapters\":0.5}" +
            "],\"totalNumOfChapters\":2}";

    private final static String EXPECTED_JSON_PUT = "{\"locations\":[" +
            "{\"name\":\"London\",\"chapters\":[1],\"totalPercentageOfChapters\":0.5}," +
            "{\"name\":\"Paris\",\"chapters\":[2],\"totalPercentageOfChapters\":0.5}," +
            "{\"name\":\"Amsterdam\",\"chapters\":[2],\"totalPercentageOfChapters\":0.5}" +
            "],\"totalNumOfChapters\":2}";

    private final static String EXPECTED_JSON_RENAME = "{\"locations\":[" +
            "{\"name\":\"Bristol\",\"chapters\":[1],\"totalPercentageOfChapters\":0.5}," +
            "{\"name\":\"Paris\",\"chapters\":[2],\"totalPercentageOfChapters\":0.5}" +
            "],\"totalNumOfChapters\":2}";

    private static final String USER_ID = "999999999999999999999"; // Use different user ID to avoid conflicts with data.sql

    private static final String USER_EMAIL = "contact@storyinspector.com";

    @Autowired
    private JdbcTemplate db;

    @BeforeEach
    void setUpEach() {
        // Reset auto-increment sequence to start from a unique time-based number to avoid conflicts
        long uniqueId = 50000 + (System.currentTimeMillis() % 1000000);
        db.execute("ALTER TABLE books ALTER COLUMN book_id RESTART WITH " + uniqueId);
    }

    private static SimpleSmtpServer testSmtpServer;

    @BeforeAll
    static void setUp() throws IOException {
        testSmtpServer = SimpleSmtpServer.start(8142);    // mock e-mail server at port 8142
    }

    @AfterAll
    static void tearDown() {
        testSmtpServer.stop();
    }

    @Test
    void testGetOne() throws JSONException {
        // given
        final long bookId = BookDAO.saveBook(db, USER_ID, USER_EMAIL, "Example Book", "Example Author", "", "", ANNOTATED_STORYDOM, new Timestamp(System.currentTimeMillis()));

        // when
        final Response response = RestAssured.given()
                .param("ID", bookId)
                .param("id_token", "")
                .get(API_ROOT + "/" + bookId);

        // then
        final String jsonOutput = response.getBody().print();
        System.out.println("jsonOutput=[" +jsonOutput +"], expected=["+ EXPECTED_JSON_GET +"]");
        JSONAssert.assertEquals(EXPECTED_JSON_GET, jsonOutput, false);
    }

    @Test
    void testPutCharacter() throws JSONException {
        // given
        final long bookId = BookDAO.saveBook(db, USER_ID, USER_EMAIL, "Example Book", "Example Author", "", "", ANNOTATED_STORYDOM, new Timestamp(System.currentTimeMillis()));

        // when
        final Response responsePost = RestAssured.given()
                .param("ID", bookId)
                .put(API_ROOT + "/" + bookId + "/2/Amsterdam/id_token");

        // then
        assertEquals(HttpStatus.OK.value() ,responsePost.getStatusCode());
        final Response responseGet = RestAssured.given()
                .param("ID", bookId)
                .param("id_token", "")
                .get(API_ROOT + "/" + bookId);
        final String jsonOutput = responseGet.getBody().print();
        System.out.println("jsonOutput=[" +jsonOutput +"], expected=["+ EXPECTED_JSON_PUT +"]");
        JSONAssert.assertEquals(EXPECTED_JSON_PUT, jsonOutput, false);
    }

    @Test
    void testRenameCharacter() throws JSONException {
        // given
        final long bookId = BookDAO.saveBook(db, USER_ID, USER_EMAIL, "Example Book", "Example Author", "", "", ANNOTATED_STORYDOM, new Timestamp(System.currentTimeMillis()));

        // when
        final Response responsePost = RestAssured.given()
                .param("ID", bookId)
                .post(API_ROOT + "/rename/" + bookId + "/London/Bristol/id_token");

        // then
        assertEquals(HttpStatus.OK.value() ,responsePost.getStatusCode());
        final Response responseGet = RestAssured.given()
                .param("ID", bookId)
                .param("id_token", "")
                .get(API_ROOT + "/" + bookId);
        final String jsonOutput = responseGet.getBody().print();
        System.out.println("jsonOutput=[" +jsonOutput +"], expected=["+ EXPECTED_JSON_RENAME +"]");
        JSONAssert.assertEquals(EXPECTED_JSON_RENAME, jsonOutput, false);
    }

    @Test
    void testDeleteCharacter() {
        // given
        final long bookId = BookDAO.saveBook(db, USER_ID, USER_EMAIL, "Example Book", "Example Author", "", "", ANNOTATED_STORYDOM, new Timestamp(System.currentTimeMillis()));

        // when
        final Response response = RestAssured.given()
                .param("ID", bookId)
                .delete(API_ROOT + "/" + bookId + "/London/id_token");

        // then
        assertEquals(HttpStatus.OK.value() ,response.getStatusCode());
        final BookDAO outputBook = BookDAO.findByBookId(bookId, db);
        final String outputStoryDom = outputBook.getAnnotatedStoryDom();
        assertEquals(EXPECTED_STORYDOM_DELETE, outputStoryDom);
    }

}
