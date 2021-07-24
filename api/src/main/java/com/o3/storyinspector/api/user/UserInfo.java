package com.o3.storyinspector.api.user;

import com.o3.storyinspector.api.ApplicationConfig;

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

    public boolean isAdmin() {
        return ApplicationConfig.ADMIN_USER_ID.equals(this.getId());
    }

    public void failIfNotAdmin() {
        if (!this.isAdmin()) throw new ForbiddenException();
    }

    public boolean emailMatches(final String otherEmail) {
        return (this.email != null && this.email.equals(otherEmail));
    }

}
