package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BlockApiTest {

    private static final String ANNOTATED_STORYDOM = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Book title=\"Example Book\">\n" +
            "    <Chapter title=\"Chapter 1\">\n" +
            "        <Metadata wordCount=\"16\" fkGrade=\"5.1\">\n" +
            "            <Locations>\n" +
            "                <Location name=\"London\" type=\"CITY\"/>\n" +
            "            </Locations>\n" +
            "            <Characters>\n" +
            "                <Character name=\"Holmes\"/>\n" +
            "            </Characters>\n" +
            "        </Metadata>\n" +
            "        <Block wordCount=\"16\" sentimentScore=\"-0.064\" fkGrade=\"5.1\">\n" +
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
            "    <Chapter title=\"Chapter 2\">\n" +
            "        <Metadata wordCount=\"16\" fkGrade=\"5.1\">\n" +
            "            <Locations>\n" +
            "                <Location name=\"Paris\" type=\"CITY\"/>\n" +
            "            </Locations>\n" +
            "            <Characters>\n" +
            "                <Character name=\"Watson\"/>\n" +
            "            </Characters>\n" +
            "        </Metadata>\n" +
            "        <Block wordCount=\"16\" sentimentScore=\"-0.064\" fkGrade=\"5.1\">\n" +
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

    private final static String EXPECTED_JSON_STRUCTURE =
            "{\"bookTitle\":\"Example Book\",\"bookAuthor\":\"Example Author\"," +
            "\"blocks\":[{\"id\":1,\"body\":\"This is an example chapter wherein wondrous things would be expected by its eager author .\",\"chapterName\":\"Chapter #1 Chapter 1\",\"fkGrade\":5.1,\"sentences\":[{\"body\":\"This is an example chapter wherein wondrous things would be expected by its eager author .\",\"fkGrade\":9.92666666666667,\"wordCount\":16}]},{\"id\":2,\"body\":\"This is another example chapter , but the action seems to unfold slower than expected .\",\"chapterName\":\"Chapter #2 Chapter 2\",\"fkGrade\":5.1,\"sentences\":[{\"body\":\"This is another example chapter , but the action seems to unfold slower than expected .\"," +
            "\"fkGrade\":11.784285714285716,\"wordCount\":16}]}]}";

    private static final String USER_ID = "999999999999999999999"; // Use different user ID to avoid conflicts with data.sql

    private static final String USER_EMAIL = "contact@storyinspector.com";

    private static final String API_ROOT = "http://localhost:8081/api/blocks";

    @Autowired
    private JdbcTemplate db;

    @BeforeEach
    void setUp() {
        // Reset auto-increment sequence to start from a unique time-based number to avoid conflicts
        long uniqueId = 40000 + (System.currentTimeMillis() % 1000000);
        db.execute("ALTER TABLE books ALTER COLUMN book_id RESTART WITH " + uniqueId);
    }

    @Test
    void findAllByBook() throws JSONException {
        // given
        final long bookId = BookDAO.saveBook(db, USER_ID, USER_EMAIL, "Example Book", "Example Author", "", "", ANNOTATED_STORYDOM, new Timestamp(System.currentTimeMillis()));

        // when
        final Response response = RestAssured.given()
                .param("ID", bookId)
                .param("id_token", "")
                .get(API_ROOT + "/list/" + bookId);

        // then
        final String jsonOutput = response.getBody().print();
        JSONAssert.assertEquals(EXPECTED_JSON_STRUCTURE, jsonOutput, true);
    }
}