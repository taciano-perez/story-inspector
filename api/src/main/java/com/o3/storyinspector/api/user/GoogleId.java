package com.o3.storyinspector.api.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Google Identity Management Service.
 */
@Component("googleId")
public class GoogleId {

    final static Logger logger = LoggerFactory.getLogger(GoogleId.class);

    private static final String CLIENT_ID = "561138250104-iaovmquhh4jim78lsi0umt64gaai8qe9.apps.googleusercontent.com";

    /**
     * Retrieves UserInfo from Google API.
     *
     * @param idTokenString the id token
     * @return UserInfo or null if an error occurs
     */
    public UserInfo retrieveUserInfo(final String idTokenString) {
        final NetHttpTransport transport = new NetHttpTransport.Builder().build();
        final JacksonFactory jacksonFactory = new JacksonFactory();
        final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jacksonFactory)
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        // (Receive idTokenString by HTTPS POST)
        try {
            final GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                final GoogleIdToken.Payload payload = idToken.getPayload();
                // Get profile information from payload
                final String userId = payload.getSubject();
                final String email = payload.getEmail();
//                final boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                final String name = (String) payload.get("name");
                logger.trace("User ID: " + userId + ", name: " + name + ", email: " + email);
//                final String pictureUrl = (String) payload.get("picture");
//                final String locale = (String) payload.get("locale");
//                final String familyName = (String) payload.get("family_name");
//                final String givenName = (String) payload.get("given_name");

                return new UserInfo(userId, name, email);

            } else {
                logger.error("An error occurred recovering credentials from Google API. idToken:" + idTokenString + ", error: Invalid ID Token.");
            }
        } catch (Exception e) {
            logger.error("Error recovering credentials from Google API. idToken:" + idTokenString + ", error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
