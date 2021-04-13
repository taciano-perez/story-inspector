package com.o3.storyinspector.storydom.constants;

import java.security.InvalidParameterException;

public enum EntityType {
    CHARACTER("character"),
    LOCATION("location");

    private String entityString;

    EntityType(final String entityString) {
        this.entityString = entityString;
    }

    public String asString() {
        return entityString;
    }
    public static EntityType entityTypeFor(final String name) {
        if (EntityType.CHARACTER.asString().equals(name.toLowerCase())) {
            return EntityType.CHARACTER;
        }
        if (EntityType.LOCATION.asString().equals(name.toLowerCase())) {
            return EntityType.LOCATION;
        }
        throw new InvalidParameterException("unknown entity type");
    }

}
