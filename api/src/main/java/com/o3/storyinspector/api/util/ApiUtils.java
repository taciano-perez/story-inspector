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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * Class with utility functions to handle API endpoints.
 */
public class ApiUtils {

    final static Logger logger = LoggerFactory.getLogger(ApiUtils.class);

    public static final String API_ROOT = "/api/process-book";

    public static final String API_CREATE_DOM = API_ROOT + "/create-dom";

    public static final String API_PROCESS_BOOK_ENDPOINT = API_ROOT + "/process-book";

    private static final String ID_TOKEN_PARAM_NAME = "id_token";

    @Async
    public static void callAsyncApiWithParameter(final String endpointPath, final String idToken, final String paramName, final String paramValue) {
        final String endpoint = getBaseUrl() + endpointPath;
        logger.trace("Calling async endpoint " + endpoint + " with parameter " + paramName + "=" + paramValue);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(paramName, paramValue);
        map.add(ID_TOKEN_PARAM_NAME, idToken);
        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(endpoint, request, String.class);
    }

    public static ResponseEntity<String> callApiWithParameter(final String endpointPath, final String idToken, final String paramName, final String paramValue) {
        final String endpoint = getBaseUrl() + endpointPath;
        logger.trace("Calling endpoint " + endpoint + " with parameter " + paramName + "=" + paramValue);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(paramName, paramValue);
        map.add(ID_TOKEN_PARAM_NAME, idToken);
        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(endpoint, request, String.class);
    }

    private static String getBaseUrl() {
        String baseUrl = "http://localhost:8081";
        final Optional<HttpServletRequest> currentHttpRequest = getCurrentHttpRequest();
        try {
            final URL requestURL = new URL(currentHttpRequest.get().getRequestURL().toString());
            final String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
            baseUrl = requestURL.getProtocol() + "://" + requestURL.getHost() + port;
            logger.trace("Base URL: " + baseUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return baseUrl;
    }

    private static Optional<HttpServletRequest> getCurrentHttpRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(requestAttributes -> ServletRequestAttributes.class.isAssignableFrom(requestAttributes.getClass()))
                .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes))
                .map(ServletRequestAttributes::getRequest);
    }
}
