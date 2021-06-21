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

    static boolean forceHttps = false;

    public static void setForceHttps(final boolean forceHttpsProtocol) {
        forceHttps = forceHttpsProtocol;
    }

    @Async
    public static void callAsyncApiWithParameter(final String endpointPath, final String paramName, final String paramValue) {
        final String endpoint = getBaseUrl(true) + endpointPath;
        logger.trace("Calling async endpoint " + endpoint + " with parameter " + paramName + "=" + paramValue);
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(endpoint, buildRestRequest(paramName, paramValue), String.class);
    }

    public static ResponseEntity<String> callApiWithParameter(final String endpointPath, final String paramName, final String paramValue) {
        final String endpoint = getBaseUrl(true) + endpointPath;
        logger.trace("Calling endpoint " + endpoint + " with parameter " + paramName + "=" + paramValue);
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(endpoint, buildRestRequest(paramName, paramValue), String.class);
    }

    private static HttpEntity<MultiValueMap<String, String>> buildRestRequest(final String paramName, final String paramValue) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(paramName, paramValue);
        return new HttpEntity<>(map, headers);
    }

    private static String getBaseUrl(final boolean forceHttpsProtocol) {
        String baseUrl = "http://localhost:8081";
        final Optional<HttpServletRequest> currentHttpRequest = getCurrentHttpRequest();
        try {
            final URL requestURL = new URL(currentHttpRequest.get().getRequestURL().toString());
            final String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
            final String protocol = forceHttpsProtocol ? "https" : requestURL.getProtocol();
            baseUrl = protocol + "://" + requestURL.getHost() + port;
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
