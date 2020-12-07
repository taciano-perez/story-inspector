package com.o3.storyinspector.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Class with utility functions to handle API endpoints.
 */
public class ApiUtils {

    final static Logger logger = LoggerFactory.getLogger(ApiUtils.class);

    public static final String API_ROOT = "http://localhost:8081/api/process-book";

    public static final String API_CREATE_DOM = API_ROOT + "/create-dom";

    public static final String API_ANNOTATE_BOOK = API_ROOT + "/annotate-book";

    public static final String API_PROCESS_BOOK_ENDPOINT = API_ROOT + "/process-book";

    @Async
    public static void callAsyncApiWithParameter(final String endpoint, final String paramName, final String paramValue) {
        logger.trace("Calling async endpoint " + endpoint + " with parameter " + paramName + "=" + paramValue);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(paramName, paramValue);
        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(endpoint, request, String.class);
    }

    public static ResponseEntity<String> callApiWithParameter(final String endpoint, final String paramName, final String paramValue) {
        logger.trace("Calling endpoint " + endpoint + " with parameter " + paramName + "=" + paramValue);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(paramName, paramValue);
        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(endpoint, request, String.class);
    }
}
