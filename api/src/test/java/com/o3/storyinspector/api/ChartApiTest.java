package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ChartApiTest {

    private static final String API_ROOT = "http://localhost:8081/api/charts";

    private static final String ANNOTATED_STORYDOM = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Book title="Example Book">
                <Chapter title="Chapter 1">
                    <Metadata wordCount="16">
                        <Locations/>
                        <Characters/>
                    </Metadata>
                    <Block wordCount="16" sentimentScore="-0,0640">
                        <Emotion type="anger" score="0.00"/>
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
                        <Emotion type="anger" score="0.00"/>
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

    private final static String EXPECTED_JSON_SENTIMENT = """
            {"labels":
                ["#1","#2"],
            "blocks":
                ["This is an example chapter wherein wondrous things would be expected by its eager author .",
                "This is another example chapter , but the action seems to unfold slower than expected ."],
            "scores":
                [-0.064,-0.064]}
            """;

    private final static String EXPECTED_JSON_EMOTION_ANGER = """
            {"labels":
                ["#1","#2"],
            "blocks":
                ["This is an example chapter wherein wondrous things would be expected by its eager author .",
                "This is another example chapter , but the action seems to unfold slower than expected ."],
            "scores":
                [0.00, 0.00]}
            """;

    @Autowired
    private JdbcTemplate db;

    @Test
    void testSentiment() throws JSONException {
        // given
        final long bookId = BookDAO.saveBook(db, "Example Book", "Example Author", "", "", ANNOTATED_STORYDOM);

        // when
        final Response response = RestAssured.given()
                .param("ID", bookId)
                .get(API_ROOT + "/" + bookId + "/posneg/");

        // then
        final String jsonOutput = response.getBody().print();
        JSONAssert.assertEquals(EXPECTED_JSON_SENTIMENT, jsonOutput, false);
    }

    @Test
    void testEmotionAnger() throws JSONException {
        // given
        final long bookId = BookDAO.saveBook(db, "Example Book", "Example Author", "", "", ANNOTATED_STORYDOM);

        // when
        final Response response = RestAssured.given()
                .get(API_ROOT + "/" + bookId + "/anger/");

        // then
        final String jsonOutput = response.getBody().print();
        JSONAssert.assertEquals(EXPECTED_JSON_EMOTION_ANGER, jsonOutput, false);
    }

}