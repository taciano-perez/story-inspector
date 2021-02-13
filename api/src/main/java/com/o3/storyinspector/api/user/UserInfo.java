package com.o3.storyinspector.api.user;

/**
 * User information retrieved from Identity Management service.
 */
public class UserInfo {

    private String id;
    private String name;
    private String email;

    public UserInfo(final String id, final String name, final String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
