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

import java.sql.Timestamp;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BookStructureApiTest {
    private static final String API_ROOT = "http://localhost:8081/api/bookstructure";

    private static final String ANNOTATED_STORYDOM = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Book title="Example Book">
                <Chapter title="Chapter 1">
                    <Metadata wordCount="16">
                        <Locations>
                            <Location name="London" type="CITY"/>
                        </Locations>
                        <Characters>
                            <Character name="Holmes"/>
                        </Characters>
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
                        <Locations>
                            <Location name="Paris" type="CITY"/>
                        </Locations>
                        <Characters>
                            <Character name="Watson"/>
                        </Characters>
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

    private final static String EXPECTED_JSON_STRUCTURE = """
            {"title": "Example Book",
            "author": "Example Author",
            "wordcount": 32,
            "chapters":
                [{
                         id:
                         1, title:'Chapter 1', wordcount:16, characters: ['Holmes'],locations: ['London']},
                     {
                         id:
                         2, title:'Chapter 2', wordcount:16, characters: ['Watson'],locations: ['Paris']}
                         ]
                     }
                 ]}
            """;

    private static final String USER_ID = "108700212624021084744";

    @Autowired
    private JdbcTemplate db;

    @Test
    void testBookStructure() throws JSONException {
        // given
        final long bookId = BookDAO.saveBook(db, USER_ID, "Example Book", "Example Author", "", "", ANNOTATED_STORYDOM, new Timestamp(System.currentTimeMillis()));

        // when
        final Response response = RestAssured.given()
                .param("ID", bookId)
                .get(API_ROOT + "/" + bookId);

        // then
        final String jsonOutput = response.getBody().print();
        JSONAssert.assertEquals(EXPECTED_JSON_STRUCTURE, jsonOutput, false);
    }
}