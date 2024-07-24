package com.o3.storyinspector.api.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class GoogleIdTest {

    @Autowired
    private GoogleId idValidator;

    @Test
    public void testDevProfile_authOK() {
        // given
        // spring.profiles.active = dev

        // when
        final UserInfo userInfo = idValidator.retrieveUserInfo("");

        // then
        assertEquals("taciano.perez@gmail.com", userInfo.getEmail());
    }

}