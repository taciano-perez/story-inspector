package com.o3.storyinspector.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BookApiTest {

    final Logger logger = LoggerFactory.getLogger(BookApiTest.class);

    private static final String BASE_URI = "http://localhost:8081";

    private static final String API_ROOT = BASE_URI + "/api/books/list";

    private static final String PARAMS = "userId=108700212624021084744";

    private static final String API_ALL_BOOKS = API_ROOT + "?" + PARAMS;

    @Test
    public void whenGetAllBooks_thenOK() {
        logger.trace("Connecting to: " + API_ALL_BOOKS);
        final Response response = RestAssured.get(API_ALL_BOOKS);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

}